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
 * Copyright 2000 (C) Exoffice Technologies Inc. All Rights Reserved.
 */

package org.exolab.jms.util;


/**
 * Helper class for manipulating JMSMessageIDs
 *
 * @version     $Revision: 1.1 $ $Date: 2004/11/26 01:51:02 $
 * @author      <a href="mailto:tima@intalio.com">Tim Anderson</a>
 **/
public class MessageIdHelper {

    /**
     * Return the 'null' JMSMessageID.
     * This is the first Id recognised by OpenJMS, but not assigned
     * to messages.
     *
     * @return      String              the 'null' message Id
     */
    public static String getNull() {
        return NULL_ID;
    }

    /**
     * Generate a JMSMessageID
     *
     * @param       Id                 numeric representation of the Id
     * @return      String             the generated JMSMessageID
     */
    public static String generate(long Id) {
        return ID_PREFIX + Id;
    }

    /**
     * Convert a JMSMessageID to its long value
     *
     * @param       messageId           JMSMessageID to convert
     * @return      long                value of the JMSMessageID
     */
    public static long convert(String messageId) {
        // strip off 'ID:' prefix
        return Long.parseLong(messageId.substring(ID_PREFIX.length()));
    }

    private static final String ID_PREFIX = "ID:";
    private static final String NULL_ID = "ID:0";
}
