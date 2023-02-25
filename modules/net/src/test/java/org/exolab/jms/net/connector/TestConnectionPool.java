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
 * $Id: TestConnectionPool.java,v 1.2 2005/06/04 14:56:40 tanderson Exp $
 */

package org.exolab.jms.net.connector;


/**
 * An {@link ConnectionPool} used for test purposes.
 *
 * @author <a href="mailto:tma@netspace.net.au">Tim Anderson</a>
 * @version $Revision: 1.2 $ $Date: 2005/06/04 14:56:40 $
 */
public class TestConnectionPool extends DefaultConnectionPool {

    /**
     * The no. of connections
     */
    private int _count = 0;


    /**
     * Construct a new <code>TestConnectionPool</code>.
     *
     * @param factory  the managed connection factory
     * @param handler  the invocation handler, assigned to each new managed
     *                 connection
     * @param resolver the connection factory for resolving connections via
     *                 their URI
     * @throws ResourceException if any configuration property is invalid
     */
    public TestConnectionPool(ManagedConnectionFactory factory,
                              InvocationHandler handler,
                              ConnectionFactory resolver)
            throws ResourceException {
        super(factory, handler, resolver, null);
    }

    /**
     * Returns the no. of the physical connections in the pool
     *
     * @return the  no. of the physical connections in the pool
     */
    public int getPooledConnections() {
        return _count;
    }

    /**
     * Adds a connection to the pool. If the connection was created, a {@link
     * ManagedConnectionHandle} will be returned, wrapping the supplied
     * connection.
     *
     * @param connection the connection to add
     * @param accepted   if <code>true</code> the connection was accepted via an
     *                   {@link ManagedConnectionAcceptor}, otherwise it was
     *                   created via {@link ManagedConnectionFactory#createManagedConnection}
     * @return the (possibly wrapped) connection
     * @throws ResourceException if the connection cannot be added
     */
    protected ManagedConnection add(ManagedConnection connection,
                                    boolean accepted) throws ResourceException {
        ManagedConnection result = super.add(connection, accepted);
        ++_count;
        return result;
    }

    /**
     * Remove a connection from the pool.
     *
     * @param connection the connection to remove
     */
    protected void remove(ManagedConnection connection) {
        super.remove(connection);
        --_count;
    }

}
