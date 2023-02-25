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
package org.exolab.jms.persistence;


import java.sql.Connection;


/**
 * This interface is used to support different connection pooling packages
 * such as Tyrex, DBCP, Minerva, PoolMan and Proxool. The client must first set
 * the properties of the connection manager before making a call to
 * {link #getConnection}
 *
 * @version     $Revision: 1.1 $ $Date: 2004/11/26 01:50:44 $
 * @author      <a href="mailto:jima@intalio.com">Jim Alateras</a>
 **/
public interface DBConnectionManager {

    /**
     * Sets the user name that is used to obtain the connection
     *
     * @param name the user name
     */
    void setUser(String name);

    /**
     * Sets the user's password that is used to access the database
     *
     * @param password the user's password
     */
    void setPassword(String password);

    /**
     * Sets the JDBC driver class name
     *
     * @param driver the JDBC driver class name
     */
    void setDriver(String driver);

    /**
     * Sets the URL to the database
     *
     * @param url the JDBC URL
     */
    void setURL(String url);

    /**
     * Sets the maximum number of active connections that can be allocated from
     * this pool at the same time, or zero for no limit.
     *
     * @param active the maximum number of active connections
     */
    void setMaxActive(int active);

    /**
     * Sets the maximum number of connections that can sit idle in the 
     * connection pool, before connections are evicted.
     *
     * @param idle the maximum number of idle connections
     */
    void setMaxIdle(int idle);

    /**
     * Sets the minimum time that a connection may remain idle
     * before it may be evicted, or zero for no eviction.
     *
     * @param time the idle time, in seconds
     */
    void setMinIdleTime(long time);

    /**
     * Sets the interval between checking idle connections for eviction.
     * Idle connections are removed after {@link #setMinIdleTime} seconds, 
     * or if {@ link #testQuery} is specified, and the query fails.
     *
     * @param interval the eviction interval, in seconds
     */
    void setEvictionInterval(long interval);

    /**
     * Specifies an SQL query to validate connections. This query
     * should return at least one row, and be fast to execute.
     *
     * @param query the test query
     */
    void setTestQuery(String query);

    /**
     * Determines if connections should be tested before use.
     * If true, each connection is tested before being returned.
     * If a connection fails, it is discarded, and another connection
     * allocated. This ensures a higher reliability, at the cost of
     * performance.
     *
     * @param test if <code>true</code>, each connection is tested use.
     */
    void setTestBeforeUse(boolean test);

    /**
     * Initialise the connection manager. This must be called before a call to
     * {@link #getConnection} is made and after all the properties have been
     * set.
     *
     * @throws PersistenceException - if there is a problem with the init
     */
    void init() throws PersistenceException;

    /**
     * Retrieve a connection to the underlying database for the pool of 
     * connections. 
     * This can only be called after the properties have been set and the 
     * manager has been initialized
     *
     * @throws PersistenceException - if there is a problem with the init
     */
    Connection getConnection() throws PersistenceException;
}
