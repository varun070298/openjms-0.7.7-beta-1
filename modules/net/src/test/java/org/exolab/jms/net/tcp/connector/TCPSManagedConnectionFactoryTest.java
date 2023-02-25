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
 * Copyright 2004 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id: TCPSManagedConnectionFactoryTest.java,v 1.5 2005/05/03 13:46:02 tanderson Exp $
 */
package org.exolab.jms.net.tcp.connector;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.exolab.jms.net.connector.ManagedConnectionFactory;
import org.exolab.jms.net.socket.SocketRequestInfo;
import org.exolab.jms.net.socket.SocketManagedConnectionFactoryTestCase;
import org.exolab.jms.net.tcp.TCPSManagedConnectionFactory;
import org.exolab.jms.net.tcp.TCPSRequestInfo;
import org.exolab.jms.net.uri.URI;
import org.exolab.jms.net.util.SSLUtil;


/**
 * Tests the {@link TCPSManagedConnectionFactory}.
 *
 * @author <a href="mailto:tma@netspace.net.au">Tim Anderson</a>
 * @version $Revision: 1.5 $ $Date: 2005/05/03 13:46:02 $
 */
public class TCPSManagedConnectionFactoryTest
        extends SocketManagedConnectionFactoryTestCase {

    /**
     * Construct an instance of this class for a specific test case.
     *
     * @param name the name of test case
     * @throws Exception for any error
     */
    public TCPSManagedConnectionFactoryTest(String name) throws Exception {
        super(name, "tcps://localhost:5099");
    }

    /**
     * Sets up the test suite.
     *
     * @return a test suite
     */
    public static Test suite() {
        return new TestSuite(TCPSManagedConnectionFactoryTest.class);
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
        return new TCPSManagedConnectionFactory();
    }

    /**
     * Returns socket request info, for the specified URI, suitable for creating
     * a managed connection and connection acceptor.
     *
     * @return socket request info for creating a managed connection
     * @throws Exception for any error
     */
    protected SocketRequestInfo getSocketRequestInfo(URI uri)
            throws Exception {
        TCPSRequestInfo info = new TCPSRequestInfo(uri);
        info.setSSLProperties(
                SSLUtil.getSSLProperties("test.keystore", "secret"));
        return info;
    }

}
