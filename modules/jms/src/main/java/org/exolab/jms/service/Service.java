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
 * Copyright 1999-2004 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id: Service.java,v 1.2 2005/08/30 04:56:14 tanderson Exp $
 */
package org.exolab.jms.service;


/**
 * <code>Service</code> is an implementation of the {@link Serviceable}
 * interface that provides default implementations for the {@link #start} and
 * {@link #stop} methods.
 *
 * @version     $Revision: 1.2 $ $Date: 2005/08/30 04:56:14 $
 * @author      <a href="mailto:jima@comware.com.au">Jim Alateras</a>
 * @see         Serviceable
 */
public abstract class Service implements Serviceable {

    /**
     * The name of the service. May be <code>null</code>.
     */
    private final String _name;

    /**
     * Determines if the service is started.
     */
    private volatile boolean _started = false;


    /**
     * Construct a new <code>Service</code>, with no name.
     */
    public Service() {
        this(null);
    }

    /**
     * Construct a new <code>Service</code>, specifying its name.
     * 
     * @param name the name of the service. May be <code>null</code>
     */
    protected Service(String name) {
        _name = name;
    }

    /**
     * Start the service.
     *
     * @throws ServiceException if the service fails to start, or is already
     * started
     */
    public void start() throws ServiceException {
        if (_started) {
            throw new ServiceException("Service already started");
        }

        doStart();

        _started = true;
    }

    /**
     * Stop the service.
     *
     * @throws ServiceException if the service fails to stop, or is already
     * stopped
     */
    public void stop() throws ServiceException {
        if (!_started) {
            throw new ServiceException("Service not started");
        }
        doStop();

        _started = false;
    }

    /**
     * Convenience method for restarting the service. This operation can
     * be called regardless the current state of the service.
     *
     * @throws ServiceException if the service fails to restart
     */
    public void restart() throws ServiceException {
        if (_started) {
            stop();
        }
        start();
    }

    /**
     * Determines if this service is started.
     *
     * @return <code>true</code> if the service is started;
     * otherwise <code>false</code>
     */
    public boolean isStarted() {
        return _started;
    }

    /**
     * Return the name of the service.
     *
     * @return the service name, or <code>null</code> if none was set.
     */
    public String getName() {
        return _name;
    }

    /**
     * Return the state of the object as a string.
     *
     * @return a string form of the object state
     */
    public String toString() {
        StringBuffer buf = new StringBuffer("Service:[");
        buf.append("name=");
        buf.append(_name);
        buf.append("started=");
        buf.append(_started);
        buf.append("]");
        return buf.toString();
    }

    /**
     * Start the service.
     *
     * @throws ServiceException if the service fails to start
     */
    protected void doStart() throws ServiceException {
    }

    /**
     * Stop the service.
     *
     * @throws ServiceException if the service fails to stop
     */
    protected void doStop() throws ServiceException {
    }

}
