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
 * $Id: InvokeTestCase.java,v 1.4 2005/05/03 13:46:01 tanderson Exp $
 */
package org.exolab.jms.net.invoke;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.HashMap;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import junit.framework.TestCase;

import org.exolab.jms.net.EchoServer;
import org.exolab.jms.net.EchoService;
import org.exolab.jms.net.EchoServiceImpl;
import org.exolab.jms.net.jvm.JVM;
import org.exolab.jms.net.orb.ORB;
import org.exolab.jms.net.orb.ORBFactory;
import org.exolab.jms.net.proxy.Proxy;
import org.exolab.jms.net.registry.Registry;
import org.exolab.jms.net.util.SSLUtil;


/**
 * Tests remote method invocation.
 *
 * @author <a href="mailto:tma@netspace.net.au">Tim Anderson</a>
 * @version $Revision: 1.4 $ $Date: 2005/05/03 13:46:01 $
 */
public abstract class InvokeTestCase extends TestCase {

    /**
     * The export URI.
     */
    private final String _uri;

    /**
     * The route URI.
     */
    private final String _routeURI;

    /**
     * Determines if the echo service will be run in the same JVM.
     */
    private final boolean _embeddedService;

    /**
     * Connection properties used when establishing a connection to the remote
     * ORB. May be <code>null</code>
     */
    private final Map _connectionProps;

    /**
     * Connection properties used when constructing the local ORB. May
     * be <code>null</code>
     */
    private final Map _acceptorProps;

    /**
     * The ORB.
     */
    private ORB _orb;

    /**
     * Tracks errors during {@link #testConcurrency}.
     */
    private Throwable _failure;

    /**
     * The JVM for running the echo service when <code>_embeddedService ==
     * false</clode>.
     */
    private JVM _jvm;

    /**
     * The logger.
     */
    private static final Log _log = LogFactory.getLog(InvokeTestCase.class);

    /**
     * Echo service name.
     */
    private static final String ECHO_SERVICE = "echo";


    /**
     * Construct an instance of this class for a specific test case.
     *
     * @param name            the name of test case
     * @param uri             the export URI
     * @param embeddedService determines if the service will be run in the
     *                        current JVM
     */
    public InvokeTestCase(String name, String uri, boolean embeddedService) {
        this(name, uri, embeddedService, null);
    }

    /**
     * Construct an instance of this class for a specific test case.
     *
     * @param name            the name of test case
     * @param uri             the export URI
     * @param embeddedService determines if the service will be run in the
     *                        current JVM
     * @param properties      connection properties. May be <code>null</code>
     */
    public InvokeTestCase(String name, String uri, boolean embeddedService,
                          Map properties) {
        this(name, uri, null, embeddedService, properties);
    }

    /**
     * Construct an instance of this class for a specific test case.
     *
     * @param name            the name of test case
     * @param uri             the export URI
     * @param routeURI        the route URI
     * @param embeddedService determines if the service will be run in the
     *                        current JVM
     */
    public InvokeTestCase(String name, String uri, String routeURI,
                          boolean embeddedService) {
        this(name, uri, routeURI, embeddedService, null);
    }

    /**
     * Construct an instance of this class for a specific test case.
     *
     * @param name            the name of test case
     * @param uri             the export URI
     * @param routeURI        the route URI
     * @param embeddedService determines if the service will be run in the
     *                        current JVM
     * @param properties      connection properties. May be <code>null</code>
     */
    public InvokeTestCase(String name, String uri, String routeURI,
                          boolean embeddedService, Map properties) {
        this(name, uri, routeURI, embeddedService, properties, properties);
    }

    /**
     * Construct an instance of this class for a specific test case.
     *
     * @param name       the name of test case
     * @param uri        the export URI
     * @param routeURI   the route URI
     * @param embeddedService determines if the service will be run in the
     *                        current JVM
     * @param connectionProps connection properties. May be <code>null</code>
     * @param acceptorProps acceptor properites. May be <code>null</code>
     */
    public InvokeTestCase(String name, String uri, String routeURI,
                          boolean embeddedService,
                          Map connectionProps, Map acceptorProps) {
        super(name);
        _uri = uri;
        _routeURI = routeURI;
        _embeddedService = embeddedService;
        _connectionProps = connectionProps;
        _acceptorProps = acceptorProps;
    }

    /**
     * Tests remote method invocation for primitives.
     *
     * @throws Exception for any error
     */
    public void testPrimitives() throws Exception {
        Registry registry = _orb.getRegistry(getConnectionProperties());
        EchoService echo = (EchoService) registry.lookup(ECHO_SERVICE);

        assertEquals(true, echo.echoBoolean(true));
        assertEquals(false, echo.echoBoolean(false));

        assertEquals(Byte.MIN_VALUE, echo.echoByte(Byte.MIN_VALUE));
        assertEquals(Byte.MAX_VALUE, echo.echoByte(Byte.MAX_VALUE));

        assertEquals(Character.MIN_VALUE, echo.echoChar(Character.MIN_VALUE));
        assertEquals(Character.MAX_VALUE, echo.echoChar(Character.MAX_VALUE));

        assertEquals(Short.MIN_VALUE, echo.echoShort(Short.MIN_VALUE));
        assertEquals(Short.MAX_VALUE, echo.echoShort(Short.MAX_VALUE));

        assertEquals(Integer.MIN_VALUE, echo.echoInt(Integer.MIN_VALUE));
        assertEquals(Integer.MAX_VALUE, echo.echoInt(Integer.MAX_VALUE));

        assertEquals(Long.MIN_VALUE, echo.echoLong(Long.MIN_VALUE));
        assertEquals(Long.MAX_VALUE, echo.echoLong(Long.MAX_VALUE));

        assertEquals(Float.MIN_VALUE, echo.echoFloat(Float.MIN_VALUE), 0.0f);
        assertEquals(Float.MAX_VALUE, echo.echoFloat(Float.MAX_VALUE), 0.0f);

        assertEquals(Double.MIN_VALUE, echo.echoDouble(Double.MIN_VALUE), 0.0);
        assertEquals(Double.MAX_VALUE, echo.echoDouble(Double.MAX_VALUE), 0.0);
    }

    /**
     * Tests remote method invocation for primitive arrays.
     *
     * @throws Exception for any error
     */
    public void testPrimitiveArrays() throws Exception {
        final int size = 4096;

        Registry registry = _orb.getRegistry(getConnectionProperties());
        EchoService echo = (EchoService) registry.lookup(ECHO_SERVICE);

        // byte arrays
        byte[] bytes = new byte[size];
        for (int i = 0; i < bytes.length; ++i) {
            bytes[i] = (byte) i;
        }
        byte[] bytesResult = (byte[]) echo.echoObject(bytes);
        assertTrue(Arrays.equals(bytes, bytesResult));

        // int arrays
        int[] ints = new int[size];
        for (int i = 0; i < ints.length; ++i) {
            ints[i] = (int) i;
        }
        int[] intsResult = (int[]) echo.echoObject(ints);
        assertTrue(Arrays.equals(ints, intsResult));

        // float arrays
        float[] floats = new float[size];
        for (int i = 0; i < floats.length; ++i) {
            floats[i] = i;
        }
        float[] floatsResult = (float[]) echo.echoObject(floats);
        assertTrue(Arrays.equals(floats, floatsResult));
    }

    /**
     * Verifies invocations can be made concurrently.
     *
     * @throws Exception for any error
     */
    public void testConcurrency() throws Exception {
        Thread[] threads = new Thread[10];

        Registry registry = _orb.getRegistry(getConnectionProperties());
        EchoService echo = (EchoService) registry.lookup(ECHO_SERVICE);

        for (int i = 0; i < threads.length; ++i) {
            threads[i] = new Thread(new IntInvoker(echo, i, 1000));
        }

        for (int i = 0; i < threads.length; ++i) {
            threads[i].start();
        }

        for (int i = 0; i < threads.length; ++i) {
            try {
                threads[i].join();
            } catch (InterruptedException ignore) {
            }
        }
        if (_failure != null) {
            if (_failure instanceof Error) {
                throw (Error) _failure;
            } else if (_failure instanceof Exception) {
                throw (Exception) _failure;
            } else {
                throw new Exception("testConcurrency failed: "
                                    + _failure.getMessage());
            }
        }
    }

    /**
     * Returns connection properties for establishing a connection to the remote
     * ORB.
     *
     * @return the connection properties, or <code>null</code> if the default
     *         connection properties should be used
     * @throws IOException if a store cannot be found
     */
    protected Map getConnectionProperties() throws IOException {
        Map properties = new HashMap();
        properties.put(ORB.PROVIDER_URI, getServerURI());
        if (_connectionProps != null) {
            properties.putAll(_connectionProps);
        }
        return properties;
    }

    /**
     * Returns the acceptor properties to use when accepting connections.
     *
     * @return the acceptor properties, or <code>null</code> if the default
     *         connection properties should be used
     * @throws Exception for any error
     */
    protected Map getAcceptorProperties() throws Exception {
        Map properties = new HashMap();
        properties.put(ORB.PROVIDER_URI, _uri);
        if (_acceptorProps != null) {
            properties.putAll(_acceptorProps);
        }
        return properties;
    }

    /**
     * Helper to return the server URI.
     *
     * @return the server URI
     */
    protected String getServerURI() {
        return (_routeURI != null) ? _routeURI : _uri;
    }

    /**
     * Sets up the test case.
     *
     * @throws Exception for any error
     */
    protected void setUp() throws Exception {
        _log.debug("setUp() [test=" + getName() + ", uri=" + _uri + "]");
        _orb = ORBFactory.createORB(getAcceptorProperties());
        if (_routeURI != null) {
            _orb.addRoute(_uri, _routeURI);
        }

        if (_embeddedService) {
            Registry serverRegistry = _orb.getRegistry();

            Proxy proxy = _orb.exportObject(new EchoServiceImpl());
            serverRegistry.bind(ECHO_SERVICE, proxy);
        } else {
            Properties props = new Properties();
            final String key = "log4j.configuration";
            String log4j = System.getProperty(key);
            if (log4j != null) {
                props.setProperty("log4j.configuration", key);
            }
            _jvm = new JVM(EchoServer.class.getName(), null, props,
                           getServerURI());
            _jvm.start();
            // give the JVM time to start
            Thread.sleep(2000);
        }
    }

    /**
     * Cleans up the test case
     *
     * @throws Exception for any error
     */
    protected void tearDown() throws Exception {
        _log.debug("tearDown() [test=" + getName() + ", uri=" + _uri + "]");
        _orb.shutdown();
        if (!_embeddedService && _jvm != null) {
            _jvm.stop();
            _jvm.waitFor();
        }

        // reset any SSL properties that may have been set.
        SSLUtil.clearProperties();
    }

    /**
     * Helper classed used in concurrency tests.
     */
    private class IntInvoker implements Runnable {

        /**
         * The echo service.
         */
        private final EchoService _echo;

        /**
         * The value to invoke the service with.
         */
        private final int _value;

        /**
         * No. of invocations to perform.
         */
        private final int _count;


        /**
         * Construct a new <code>IntInvoker</code>.
         *
         * @param echo  the echo service
         * @param value the value to invoke the echo service with
         * @param count the no. of invocations to perform
         */
        public IntInvoker(EchoService echo, int value, int count) {
            _echo = echo;
            _value = value;
            _count = count;
        }

        /**
         * Run the invoker.
         */
        public void run() {
            try {
                for (int i = 0; i < _count; ++i) {
                    assertEquals(_value, _echo.echoInt(_value));
                }
            } catch (Throwable exception) {
                _failure = exception;
            }
        }
    }
}
