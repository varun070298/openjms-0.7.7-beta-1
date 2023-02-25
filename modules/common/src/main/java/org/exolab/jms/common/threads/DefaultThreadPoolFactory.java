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
 * Copyright 2006 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id: DefaultThreadPoolFactory.java,v 1.1 2006/02/23 11:07:05 tanderson Exp $
 */
package org.exolab.jms.common.threads;

import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import EDU.oswego.cs.dl.util.concurrent.Channel;


/**
 * Default {@link ThreadPoolFactory} implementation that enables a {@link
 * ThreadListener} to be associated with each pool.
 *
 * @author <a href="mailto:tma@netspace.net.au">Tim Anderson</a>
 * @version $Revision: 1.1 $ $Date: 2006/02/23 11:07:05 $
 */
public class DefaultThreadPoolFactory implements ThreadPoolFactory {

    /**
     * The thread listener. May be <code>null</code>.
     */
    private final ThreadListener _listener;


    /**
     * Construct a new <code>DefaultThreadPoolFactory</code>.
     *
     * @param listener the thread listener. May be <code>null</code>
     */
    public DefaultThreadPoolFactory(ThreadListener listener) {
        _listener = listener;
    }

    /**
     * Construct a new <code>PooledExecutor</code>.
     *
     * @param name        the name to assign the thread group and worker
     *                    threads
     * @param maxPoolSize the maximum no. of threads to use
     */
    public PooledExecutor create(String name, int maxPoolSize) {
        return init(new ThreadPool(name, maxPoolSize));
    }

    /**
     * Construct a new <code>ThreadPool</code>.
     *
     * @param name        the name to assign the thread group and worker
     *                    threads
     * @param channel     the channel for queueing
     * @param maxPoolSize the maximum no. of threads to use
     */
    public PooledExecutor create(String name, Channel channel, int maxPoolSize) {
        return init(new ThreadPool(name, channel, maxPoolSize));
    }

    /**
     * Initialises a pool.
     *
     * @param pool the pool to initialise
     * @return the pool
     */
    protected ThreadPool init(ThreadPool pool) {
        pool.setThreadListener(_listener);
        return pool;
    }

}
