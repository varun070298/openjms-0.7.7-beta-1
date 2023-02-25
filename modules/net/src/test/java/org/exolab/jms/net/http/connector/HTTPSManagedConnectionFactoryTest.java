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
 * Copyright 2004-2005 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id: HTTPSManagedConnectionFactoryTest.java,v 1.3 2005/04/19 12:32:26 tanderson Exp $
 */
package org.exolab.jms.net.http.connector;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.exolab.jms.net.connector.ConnectionRequestInfo;
import org.exolab.jms.net.connector.ManagedConnectionFactory;
import org.exolab.jms.net.connector.ManagedConnectionFactoryTestCase;
import org.exolab.jms.net.http.HTTPRequestInfo;
import org.exolab.jms.net.http.HTTPSManagedConnectionFactory;
import org.exolab.jms.net.socket.SocketRequestInfo;
import org.exolab.jms.net.uri.URI;
import org.exolab.jms.net.util.SSLProperties;
import org.exolab.jms.net.util.SSLUtil;


/**
 * Tests the {@link HTTPSManagedConnectionFactory}.
 *
 * @author <a href="mailto:tma@netspace.net.au">Tim Anderson</a>
 * @version $Revision: 1.3 $ $Date: 2005/04/19 12:32:26 $
 */
public class HTTPSManagedConnectionFactoryTest
        extends ManagedConnectionFactoryTestCase {

    /**
     * Construct an instance of this class for a specific test case.
     *
     * @param name the name of test case
     */
    public HTTPSManagedConnectionFactoryTest(String name) {
        super(name);
    }

    /**
     * Sets up the test suite.
     *
     * @return a test suite
     */
    public static Test suite() {
        return new TestSuite(HTTPSManagedConnectionFactoryTest.class);
    }

    /**
     * The main line used to execute the test cases.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Creates a managed connection factory.
     *
     * @return the new managed connection factory
     * @throws Exception for any error
     */
    protected ManagedConnectionFactory createManagedConnectionFactory()
            throws Exception {
        return new HTTPSManagedConnectionFactory();
    }

    /**
     * Returns connection request info suitable for creating a managed
     * connection.
     *
     * @return connection request info for creating a managed connection
     * @throws Exception for any error
     */
    protected ConnectionRequestInfo getManagedConnectionRequestInfo()
            throws Exception {
        HTTPRequestInfo info = new HTTPRequestInfo(
                new URI("https://localhost:8443/openjms-tunnel/tunnel"));
        SSLProperties properties =
                SSLUtil.getSSLProperties("test.keystore", "secret");
        info.setSSLProperties(properties);
        return info;
    }

    /**
     * Returns connection request info suitable for creating a managed
     * connection acceptor.
     *
     * @return connection request info for creating a managed connection
     *         acceptor
     * @throws Exception for any error
     */
    protected ConnectionRequestInfo getAcceptorConnectionRequestInfo()
            throws Exception {
        return new SocketRequestInfo(new URI("https-server://localhost:3030"));
    }

}
