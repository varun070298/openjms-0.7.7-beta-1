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
 * Copyright 2005 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id: CachedMessageRef.java,v 1.3 2005/12/20 20:31:59 tanderson Exp $
 */
package org.exolab.jms.messagemgr;

import javax.jms.JMSException;

import org.exolab.jms.message.MessageImpl;


/**
 * An {@link MessageRef} which references messages located in an {@link
 * MessageCache}.
 *
 * @author <a href="mailto:tma@netspace.net.au">Tim Anderson</a>
 * @version $Revision: 1.3 $ $Date: 2005/12/20 20:31:59 $
 */
class CachedMessageRef extends AbstractMessageRef {

    /**
     * The cache that holds the message.
     */
    private final MessageCache _cache;


    /**
     * Construct a new <code>CachedMessageRef</code>.
     *
     * @param message    the message to reference
     * @param persistent if <code>true</code> the message is persistent
     * @param cache      the cache which manages the message
     */
    public CachedMessageRef(MessageImpl message, boolean persistent,
                            MessageCache cache) {
        super(message.getMessageId().getId(), persistent);

        if (cache == null) {
            throw new IllegalArgumentException("Argument 'cache' is null");
        }
        _cache = cache;
    }

    /**
     * Construct a new <code>CachedMessageRef</code>.
     *
     * @param messageId  the message to reference
     * @param persistent if <code>true</code> the message is persistent
     * @param cache      the cache which manages the message
     */
    public CachedMessageRef(String messageId, boolean persistent,
                            MessageCache cache) {
        super(messageId, persistent);

        if (cache == null) {
            throw new IllegalArgumentException("Argument 'cache' is null");
        }
        _cache = cache;
    }

    /**
     * Returns the message associated with this reference.
     *
     * @return the associated message, or <code>null</code> if the reference is
     *         no longer valid
     * @throws JMSException for any error
     */
    public MessageImpl getMessage() throws JMSException {
        return _cache.getMessage(this);
    }

    /**
     * Destroy the message, irrespective of the number of current references.
     *
     * @throws JMSException for any error
     */
    public synchronized void destroy() throws JMSException {
        if (!isDestroyed()) {
            _cache.destroy(this);
            setDestroyed();
        }
    }

}
