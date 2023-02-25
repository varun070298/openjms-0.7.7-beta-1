/**
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright
 *    statements and notices.  Redistributions must also contain a
 *    copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the
 *    above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 *
 * 3. The name "Exolab" must not be used to endorse or promote
 *    products derived from this Software without prior written
 *    permission of Exoffice Technologies.  For written permission,
 *    please contact info@exolab.org.
 *
 * 4. Products derived from this Software may not be called "Exolab"
 *    nor may "Exolab" appear in their names without prior written
 *    permission of Exoffice Technologies. Exolab is a registered
 *    trademark of Exoffice Technologies.
 *
 * 5. Due credit should be given to the Exolab Project
 *    (http://www.exolab.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY EXOFFICE TECHNOLOGIES AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * EXOFFICE TECHNOLOGIES OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 2000-2005 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id: BytesMessageImpl.java,v 1.2 2005/03/18 03:50:12 tanderson Exp $
 */
package org.exolab.jms.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.UTFDataFormatException;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MessageEOFException;
import javax.jms.MessageFormatException;
import javax.jms.MessageNotReadableException;
import javax.jms.MessageNotWriteableException;


/**
 * This class implements the {@link BytesMessage} interface.
 *
 * @version     $Revision: 1.2 $ $Date: 2005/03/18 03:50:12 $
 * @author      <a href="mailto:mourikis@intalio.com">Jim Mourikis</a>
 * @author      <a href="mailto:tima@netspace.net.au">Tim Anderson</a>
 * @see         BytesMessage
 */
public final class BytesMessageImpl extends MessageImpl
    implements BytesMessage {

    /**
     * Object version no. for serialization.
     */
    static final long serialVersionUID = 1;

    /**
     * Empty byte array for initialisation purposes.
     */
    private static final byte[] EMPTY = new byte[]{};

    /**
     * The byte stream to store data.
     */
    private byte[] _bytes = EMPTY;

    /**
     * The stream used for writes.
     */
    private DataOutputStream _out = null;

    /**
     * The byte stream backing the output stream.
     */
    private ByteArrayOutputStream _byteOut = null;

    /**
     * The stream used for reads.
     */
    private DataInputStream _in = null;

    /**
     * The byte stream backing the input stream.
     */
    private ByteArrayInputStream _byteIn = null;

    /**
     * The offset of the byte stream to start reading from. This defaults
     * to 0, and only applies to messages that are cloned from a
     * read-only instance where part of the stream had already been read.
     */
    private int _offset = 0;

    /**
     * Construct a new BytesMessage. When first created, the message is in
     * write-only mode.
     *
     * @throws JMSException if the message type can't be set
     */
    public BytesMessageImpl() throws JMSException {
        setJMSType("BytesMessage");
    }

    /**
     * Clone an instance of this object.
     *
     * @return a copy of this object
     * @throws CloneNotSupportedException if object or attributes aren't
     * cloneable
     */
    public final Object clone() throws CloneNotSupportedException {
        BytesMessageImpl result = (BytesMessageImpl) super.clone();
        if (_bodyReadOnly) {
            result._bytes = new byte[_bytes.length];
            System.arraycopy(_bytes, 0, result._bytes, 0, _bytes.length);
            if (_byteIn != null) {
                // if a client subsequently reads from the cloned object,
                // start reading from offset of the original stream
                _offset = _bytes.length - _byteIn.available();
            }
            result._byteIn = null;
            result._in = null;
        } else {
            if (_out != null) {
                try {
                    _out.flush();
                } catch (IOException exception) {
                    throw new CloneNotSupportedException(
                        exception.getMessage());
                }
                result._bytes = _byteOut.toByteArray();
                result._byteOut = null;
                result._out = null;
            } else {
                result._bytes = new byte[_bytes.length];
                System.arraycopy(_bytes, 0, result._bytes, 0, _bytes.length);
            }
        }

        return result;
    }

    /**
     * Serialize out this message's data.
     *
     * @param out the stream to serialize out to
     * @throws IOException if any I/O exceptions occurr
     */
    public final void writeExternal(ObjectOutput out) throws IOException {
        // If it was in write mode, extract the byte array.
        if (!_bodyReadOnly && _out != null) {
            _out.flush();
            _bytes = _byteOut.toByteArray();
        }

        super.writeExternal(out);
        out.writeLong(serialVersionUID);
        out.writeInt(_bytes.length);
        out.write(_bytes);
        out.flush();
    }

    /**
     * Serialize in this message's data.
     *
     * @param in the stream to serialize in from
     * @throws ClassNotFoundException if the class for an object being
     * restored cannot be found.
     * @throws IOException if any I/O exceptions occur
     */
    public final void readExternal(ObjectInput in)
        throws ClassNotFoundException, IOException {
        super.readExternal(in);
        long version = in.readLong();
        if (version == serialVersionUID) {
            int length = in.readInt();
            _bytes = new byte[length];
            in.readFully(_bytes);
        } else {
            throw new IOException("Incorrect version enountered: " + version +
                ". This version = " + serialVersionUID);
        }
    }

    /**
     * Gets the number of bytes of the message body when the message is in
     * read-only mode. The value returned can be used to allocate a byte array.
     * The value returned is the entire length of the message body, regardless
     * of where the pointer for reading the message is currently located.
     *
     * @return number of bytes in the message
     * @throws JMSException                if the JMS provider fails to read the
     *                                     message due to some internal error.
     * @throws MessageNotReadableException if the message is in write-only
     *                                     mode.
     */

    public long getBodyLength() throws JMSException {
        checkRead();
        return _bytes.length;
    }

    /**
     * Read a <code>boolean</code> from the bytes message stream.
     *
     * @return the <code>boolean</code> value read
     * @throws JMSException if JMS fails to read message due to some internal
     * JMS error
     * @throws MessageEOFException if end of bytes stream
     * @throws MessageNotReadableException if message is in write-only mode
     */
    public final boolean readBoolean() throws JMSException {
        boolean result = false;
        prepare();
        try {
            result = _in.readBoolean();
        } catch (IOException exception) {
            revert(exception);
        }
        return result;
    }

    /**
     * Read a signed 8-bit value from the bytes message stream.
     *
     * @return the next byte from the bytes message stream as a signed 8-bit
     * <code>byte</code>
     * @throws JMSException if JMS fails to read message due to some internal
     * JMS error
     * @throws MessageEOFException if end of message stream
     * @throws MessageNotReadableException if message is in write-only mode
     */
    public final byte readByte() throws JMSException {
        byte result = 0;
        prepare();
        try {
            result = _in.readByte();
        } catch (IOException exception) {
            revert(exception);
        }
        return result;
    }

    /**
     * Read an unsigned 8-bit number from the bytes message stream.
     *
     * @return the next byte from the bytes message stream, interpreted as an
     * unsigned 8-bit number
     * @throws JMSException if JMS fails to read message due to some internal
     * JMS error
     * @throws MessageNotReadableException if message is in write-only mode
     * @throws MessageEOFException if end of message stream
     */
    public final int readUnsignedByte() throws JMSException {
        int result = 0;
        prepare();
        try {
            result = _in.readUnsignedByte();
        } catch (IOException exception) {
            revert(exception);
        }
        return result;
    }

    /**
     * Read a signed 16-bit number from the bytes message stream.
     *
     * @return the next two bytes from the bytes message stream, interpreted
     * as a signed 16-bit number
     * @throws JMSException if JMS fails to read message due to some internal
     * JMS error
     * @throws MessageEOFException if end of message stream
     * @throws MessageNotReadableException if message is in write-only mode
     */
    public final short readShort() throws JMSException {
        short result = 0;
        prepare();
        try {
            result = _in.readShort();
        } catch (IOException exception) {
            revert(exception);
        }
        return result;
    }

    /**
     * Read an unsigned 16-bit number from the bytes message stream.
     *
     * @return the next two bytes from the bytes message stream, interpreted
     * as an unsigned 16-bit integer
     *
     * @throws JMSException if JMS fails to read message due to some internal
     * JMS error
     * @throws MessageEOFException if end of message stream
     * @throws MessageNotReadableException if message is in write-only mode
     */
    public final int readUnsignedShort() throws JMSException {
        int result = 0;
        prepare();
        try {
            result = _in.readUnsignedShort();
        } catch (IOException exception) {
            revert(exception);
        }
        return result;
    }

    /**
     * Read a Unicode character value from the bytes message stream.
     *
     * @return the next two bytes from the bytes message stream as a Unicode
     * character
     * @throws JMSException if JMS fails to read message due to some internal
     * JMS error
     * @throws MessageEOFException if end of message stream
     * @throws MessageNotReadableException if message is in write-only mode
     */
    public final char readChar() throws JMSException {
        char result = 0;
        prepare();
        try {
            result = _in.readChar();
        } catch (IOException exception) {
            revert(exception);
        }
        return result;
    }

    /**
     * Read a signed 32-bit integer from the bytes message stream.
     *
     * @return the next four bytes from the bytes message stream, interpreted
     * as an <code>int</code>
     * @throws JMSException if JMS fails to read message due to some internal
     * JMS error
     * @throws MessageEOFException if end of message stream
     * @throws MessageNotReadableException if message is in write-only mode
     */
    public final int readInt() throws JMSException {
        int result = 0;
        prepare();
        try {
            result = _in.readInt();
        } catch (IOException exception) {
            revert(exception);
        }
        return result;
    }

    /**
     * Read a signed 64-bit integer from the bytes message stream.
     *
     * @return the next eight bytes from the bytes message stream, interpreted
     * as a <code>long</code>.
     * @throws JMSException if JMS fails to read message due to some internal
     * JMS error
     * @throws MessageEOFException if end of message stream
     * @throws MessageNotReadableException if message is in write-only mode
     */
    public final long readLong() throws JMSException {
        long result = 0;
        prepare();
        try {
            result = _in.readLong();
        } catch (IOException exception) {
            revert(exception);
        }
        return result;
    }

    /**
     * Read a <code>float</code> from the bytes message stream.
     *
     * @return the next four bytes from the bytes message stream, interpreted
     * as a <code>float</code>
     * @throws JMSException if JMS fails to read message due to some internal
     * JMS error
     * @throws MessageEOFException if end of message stream
     * @throws MessageNotReadableException if message is in write-only mode
     */
    public final float readFloat() throws JMSException {
        float result = 0;
        prepare();
        try {
            result = _in.readFloat();
        } catch (IOException exception) {
            revert(exception);
        }
        return result;
    }

    /**
     * Read a <code>double</code> from the bytes message stream.
     *
     * @return the next eight bytes from the bytes message stream,
     *			interpreted as a <code>double</code>
     * @throws JMSException if JMS fails to read message due to some internal
     * JMS error
     * @throws MessageEOFException if end of message stream
     * @throws MessageNotReadableException if message is in write-only mode
     */
    public final double readDouble() throws JMSException {
        double result = 0;
        prepare();
        try {
            result = _in.readDouble();
        } catch (IOException exception) {
            revert(exception);
        }
        return result;
    }

    /**
     * Read in a string that has been encoded using a modified UTF-8 format
     * from the bytes message stream.
     *
     * <p>For more information on the UTF-8 format, see "File System Safe
     * UCS Transformation Format (FSS_UFT)", X/Open Preliminary Specification,
     * X/Open Company Ltd., Document Number: P316. This information also
     * appears in ISO/IEC 10646, Annex P.
     *
     * @return a Unicode string from the bytes message stream
     * @throws JMSException if JMS fails to read message due to some internal
     * JMS error
     * @throws MessageEOFException if end of message stream
     * @throws MessageFormatException if string has an invalid format
     * @throws MessageNotReadableException if message is in write-only mode
     */
    public final String readUTF() throws JMSException {
        String result = null;
        prepare();
        try {
            result = _in.readUTF();
        } catch (IOException exception) {
            revert(exception);
        }
        return result;
    }

    /**
     * Read a byte array from the bytes message stream.
     *
     * <p>If the length of array <code>value</code> is less than
     * the bytes remaining to be read from the stream, the array should
     * be filled. A subsequent call reads the next increment, etc.
     *
     * <p>If the bytes remaining in the stream is less than the length of
     * array <code>value</code>, the bytes should be read into the array.
     * The return value of the total number of bytes read will be less than
     * the length of the array, indicating that there are no more bytes left
     * to be read from the stream. The next read of the stream returns -1.
     *
     * @param value the buffer into which the data is read
     * @return the total number of bytes read into the buffer, or -1 if
     * there is no more data because the end of the stream has been reached
     * @throws JMSException if JMS fails to read message due to some internal
     * JMS error
     * @throws MessageNotReadableException if message is in write-only mode
     */
    public final int readBytes(byte[] value) throws JMSException {
        return readBytes(value, value.length);
    }

    /**
     * Read a portion of the bytes message stream.
     *
     * <p>If the length of array <code>value</code> is less than
     * the bytes remaining to be read from the stream, the array should
     * be filled. A subsequent call reads the next increment, etc.
     *
     * <p>If the bytes remaining in the stream is less than the length of
     * array <code>value</code>, the bytes should be read into the array.
     * The return value of the total number of bytes read will be less than
     * the length of the array, indicating that there are no more bytes left
     * to be read from the stream. The next read of the stream returns -1.
     *
     * <p> If <code>length</code> is negative, or
     * <code>length</code> is greater than the length of the array
     * <code>value</code>, then an <code>IndexOutOfBoundsException</code> is
     * thrown. No bytes will be read from the stream for this exception case.
     *
     * @param value the buffer into which the data is read.
     * @param length the number of bytes to read. Must be less than or equal
     * to value.length.
     * @return the total number of bytes read into the buffer, or -1 if
     * there is no more data because the end of the stream has been reached.
     * @throws IndexOutOfBoundsException if <code>length</code> is invalid
     * @throws JMSException if JMS fails to read message due to some internal
     * JMS error
     * @throws MessageNotReadableException if message is in write-only mode
     */
    public final int readBytes(byte[] value, int length) throws JMSException {
        int read = -1;
        prepare();

        if (length < 0 || length > value.length) {
            throw new IndexOutOfBoundsException(
                "Length must be > 0 and less than array size");
        }
        try {
            _in.mark(length);
            int remain = _in.available();
            if (remain == 0) {
                read = -1;
            } else if (length <= remain) {
                read = length;
                _in.read(value, 0, length);
            } else {
                _in.readFully(value, 0, remain);
                read = remain;
            }
        } catch (EOFException ignore) {
        } catch (IOException exception) {
            revert(exception);
        }
        return read;
    }

    /**
     * Write a <code>boolean</code> to the bytes message stream as a 1-byte
     * value.
     * The value <code>true</code> is written out as the value
     * <code>(byte)1</code>; the value <code>false</code> is written out as
     * the value <code>(byte)0</code>.
     *
     * @param value the <code>boolean</code> value to be written
     * @throws JMSException if JMS fails to write message due to some internal
     * JMS error
     * @throws MessageNotWriteableException if message is in read-only mode
     */
    public final void writeBoolean(boolean value) throws JMSException {
        checkWrite();
        try {
            getOutputStream().writeBoolean(value);
        } catch (IOException exception) {
            raise(exception);
        }
    }

    /**
     * Write out a <code>byte</code> to the bytes message stream as a 1-byte
     * value.
     *
     * @param value the <code>byte</code> value to be written
     * @throws JMSException if JMS fails to write message due to some internal
     * JMS error
     * @throws MessageNotWriteableException if message is in read-only mode
     */
    public final void writeByte(byte value) throws JMSException {
        checkWrite();
        try {
            getOutputStream().writeByte(value);
        } catch (IOException exception) {
            raise(exception);
        }
    }

    /**
     * Write a <code>short</code> to the bytes message stream as two bytes,
     * high byte first.
     *
     * @param value the <code>short</code> to be written
     * @throws JMSException if JMS fails to write message due to some internal
     * JMS error
     * @throws MessageNotWriteableException if message is in read-only mode
     */
    public final void writeShort(short value) throws JMSException {
        checkWrite();
        try {
            getOutputStream().writeShort(value);
        } catch (IOException exception) {
            raise(exception);
        }
    }

    /**
     * Write a <code>char</code> to the bytes message stream as a 2-byte
     * value, high byte first.
     *
     * @param value the <code>char</code> value to be written
     * @throws MessageNotWriteableException if message is in read-only mode
     * @throws JMSException if JMS fails to write message due to some internal
     * JMS error
     */
    public final void writeChar(char value) throws JMSException {
        checkWrite();
        try {
            getOutputStream().writeChar(value);
        } catch (IOException exception) {
            raise(exception);
        }
    }

    /**
     * Write an <code>int</code> to the bytes message stream as four bytes,
     * high byte first.
     *
     * @param value the <code>int</code> to be written
     * @throws JMSException if JMS fails to write message due to some internal
     * JMS error
     * @throws MessageNotWriteableException if message is in read-only mode
     */
    public final void writeInt(int value) throws JMSException {
        checkWrite();
        try {
            getOutputStream().writeInt(value);
        } catch (IOException exception) {
            raise(exception);
        }
    }

    /**
     * Write a <code>long</code> to the bytes message stream as eight bytes,
     * high byte first
     *
     * @param value the <code>long</code> to be written
     * @throws JMSException if JMS fails to write message due to some internal
     * JMS error
     * @throws MessageNotWriteableException if message is in read-only mode
     */
    public final void writeLong(long value) throws JMSException {
        checkWrite();
        try {
            getOutputStream().writeLong(value);
        } catch (IOException exception) {
            raise(exception);
        }
    }

    /**
     * Convert the float argument to an <code>int</code> using the
     * <code>floatToIntBits</code> method in class <code>Float</code>,
     * and then writes that <code>int</code> value to the bytes message
     * stream as a 4-byte quantity, high byte first.
     *
     * @param value the <code>float</code> value to be written.
     * @throws JMSException if JMS fails to write message due to some internal
     * JMS error
     * @throws MessageNotWriteableException if message is in read-only mode
     */
    public final void writeFloat(float value) throws JMSException {
        checkWrite();
        try {
            getOutputStream().writeFloat(value);
        } catch (IOException exception) {
            raise(exception);
        }
    }


    /**
     * Convert the double argument to a <code>long</code> using the
     * <code>doubleToLongBits</code> method in class <code>Double</code>,
     * and then writes that <code>long</code> value to the bytes message
     * stream as an 8-byte quantity, high byte first.
     *
     * @param value the <code>double</code> value to be written.
     * @throws JMSException if JMS fails to write message due to some internal
     * JMS error
     * @throws MessageNotWriteableException if message is in read-only mode
     */
    public final void writeDouble(double value) throws JMSException {
        checkWrite();
        try {
            getOutputStream().writeDouble(value);
        } catch (IOException exception) {
            raise(exception);
        }
    }

    /**
     * Write a string to the bytes message stream using UTF-8 encoding in a
     * machine-independent manner.
     *
     * <p>For more information on the UTF-8 format, see "File System Safe
     * UCS Transformation Format (FSS_UFT)", X/Open Preliminary Specification,
     * X/Open Company Ltd., Document Number: P316. This information also
     * appears in ISO/IEC 10646, Annex P.
     *
     * @param value the <code>String</code> value to be written
     * @throws MessageNotWriteableException if message is in read-only mode
     * @throws JMSException if JMS fails to write message due to some internal
     * JMS error
     */
    public final void writeUTF(String value) throws JMSException {
        checkWrite();
        try {
            getOutputStream().writeUTF(value);
        } catch (IOException exception) {
            raise(exception);
        }
    }

    /**
     * Write a byte array to the bytes message stream.
     *
     * @param value the byte array to be written.
     * @throws JMSException if JMS fails to write message due to some internal
     * JMS error
     * @throws MessageNotWriteableException if message is in read-only mode
     */
    public final void writeBytes(byte[] value) throws JMSException {
        checkWrite();
        try {
            getOutputStream().write(value);
        } catch (IOException exception) {
            raise(exception);
        }
    }

    /**
     * Write a portion of a byte array to the bytes message stream
     *
     * @param value the byte array value to be written.
     * @param offset the initial offset within the byte array.
     * @param length the number of bytes to use.
     * @throws JMSException if JMS fails to write message due to some internal
     * JMS error
     * @throws MessageNotWriteableException if message is in read-only mode
     */
    public final void writeBytes(byte[] value, int offset, int length)
        throws JMSException {
        checkWrite();
        try {
            getOutputStream().write(value, offset, length);
        } catch (IOException exception) {
            raise(exception);
        }
    }

    /**
     * Write a Java object to the bytes message stream.
     *
     * <p>Note that this method only works for the objectified primitive
     * object types (Integer, Double, Long ...), String's and byte arrays.
     *
     * @param value the Java object to be written. Must not be null.
     *
     * @throws JMSException if JMS fails to write message due to some internal
     * JMS error
     * @throws MessageFormatException if object is invalid type
     * @throws MessageNotWriteableException if message in read-only mode
     * @throws NullPointerException if parameter <code>value</code> is null
     */
    public final void writeObject(Object value) throws JMSException {
        if (value instanceof Boolean) {
            writeBoolean(((Boolean) value).booleanValue());
        } else if (value instanceof Byte) {
            writeByte(((Byte) value).byteValue());
        } else if (value instanceof Short) {
            writeShort(((Short) value).shortValue());
        } else if (value instanceof Character) {
            writeChar(((Character) value).charValue());
        } else if (value instanceof Integer) {
            writeInt(((Integer) value).intValue());
        } else if (value instanceof Long) {
            writeLong(((Long) value).longValue());
        } else if (value instanceof Float) {
            writeFloat(((Float) value).floatValue());
        } else if (value instanceof Double) {
            writeDouble(((Double) value).doubleValue());
        } else if (value instanceof String) {
            writeUTF((String) value);
        } else if (value instanceof byte[]) {
            writeBytes((byte[]) value);
        } else if (value == null) {
            throw new NullPointerException(
                "BytesMessage does not support null");
        } else {
            throw new MessageFormatException("Cannot write objects of type=" +
                value.getClass().getName());
        }
    }

    /**
     * Put the message body in read-only mode, and reposition the stream of
     * bytes to the beginning.
     *
     * @throws JMSException if JMS fails to reset the message due to some
     * internal JMS error
     */
    public final void reset() throws JMSException {
        try {
            if (!_bodyReadOnly) {
                _bodyReadOnly = true;
                if (_out != null) {
                    _out.flush();
                    _bytes = _byteOut.toByteArray();
                    _byteOut = null;
                    _out.close();
                    _out = null;
                }
            } else {
                if (_in != null) {
                    _byteIn = null;
                    _in.close();
                    _in = null;
                }
            }
        } catch (IOException exception) {
            raise(exception);
        }
    }

    /**
     * Overide the super class method to reset the streams, and put the
     * message body in write only mode.
     *
     * <p>If <code>clearBody</code> is called on a message in read-only mode,
     * the message body is cleared and the message is in write-only mode.
     * bytes to the beginning.
     *
     *	<p>If <code>clearBody</code> is called on a message already in
     * 	write-only mode, the spec does not define the outcome, so do nothing.
     * 	Client must then call <code>reset</code>, followed by
     *	<code>clearBody</code> to reset the stream at the beginning for a
     *	new write.
     * @throws JMSException if JMS fails to reset the message due to some
     * internal JMS error
     */
    public final void clearBody() throws JMSException {
        try {
            if (_bodyReadOnly) {
                // in read-only mode
                _bodyReadOnly = false;
                if (_in != null) {
                    _byteIn = null;
                    _in.close();
                    _in = null;
                    _offset = 0;
                }
            } else if (_out != null) {
                // already in write-only mode
                _byteOut = null;
                _out.close();
                _out = null;
            }
            _bytes = EMPTY;
            _byteOut = null;
            _out = null;
        } catch (IOException exception) {
            raise(exception);
        }
    }

    /**
     * Set the read-only mode of the message.
     *
     * @param readOnly if true, make the message body and properties read-only,
     * and invoke {@link #reset}
     * @throws JMSException if the read-only mode cannot be changed
     */
    public final void setReadOnly(boolean readOnly) throws JMSException {
        if (readOnly) {
            reset();
        }
        super.setReadOnly(readOnly);
    }

    /**
     * Prepare to do a read.
     *
     * @throws JMSException if the current position in the stream can't be
     * marked
     * @throws MessageNotReadableException if the message is in write-only mode
     */
    private final void prepare() throws JMSException {
        checkRead();
        getInputStream();
        try {
            _in.mark(_bytes.length - _in.available());
        } catch (IOException exception) {
            raise(exception);
        }
    }

    /**
     * Reverts the stream to its prior position if an I/O exception is
     * thrown, and propagates the exception as a JMSException.
     *
     * @param exception the exception that caused the reset
     * @throws JMSException for general IOException errors
     * @throws MessageEOFException if end-of-file was reached
     */
    private final void revert(IOException exception) throws JMSException {
        try {
            _in.reset();
        } catch (IOException ignore) {
        }
        JMSException error = null;
        if (exception instanceof EOFException) {
            error = new MessageEOFException(exception.getMessage());
        } else if (exception instanceof UTFDataFormatException) {
            error = new MessageFormatException(exception.getMessage());
        } else {
            error = new JMSException(exception.getMessage());
        }
        error.setLinkedException(exception);
        throw error;
    }

    /**
     * Initialise the input stream if it hasn't been intialised.
     *
     * @return the input stream
     */
    private DataInputStream getInputStream() {
        if (_in == null) {
            _byteIn = new ByteArrayInputStream(_bytes, _offset,
                _bytes.length - _offset);
            _in = new DataInputStream(_byteIn);
        }
        return _in;
    }

    /**
     * Initialise the output stream if it hasn't been intialised.
     *
     * @return the output stream
     * @throws IOException if the output stream can't be created
     */
    private final DataOutputStream getOutputStream() throws IOException {
        if (_out == null) {
            _byteOut = new ByteArrayOutputStream();
            _out = new DataOutputStream(_byteOut);
            _out.write(_bytes);
        }
        return _out;
    }

    /**
     * Helper to raise a JMSException when an I/O error occurs.
     *
     * @param exception the exception that caused the failure
     * @throws JMSException
     */
    private final void raise(IOException exception) throws JMSException {
        JMSException error = new JMSException(exception.getMessage());
        error.setLinkedException(exception);
        throw error;
    }

}
