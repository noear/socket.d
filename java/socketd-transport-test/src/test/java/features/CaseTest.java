package features;

import features.cases.*;
import org.junit.jupiter.api.Test;

/**
 * @author noear
 * @since 2.0
 */
public class CaseTest {
    static final String[] schemas = new String[]{
            "sd:tcp-java", "sd:tcp-netty", "sd:tcp-smartsocket",
            "sd:ws-java",
            "sd:udp-java"};

    @Test
    public void TestCase01_client_send() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            BaseTestCase testCase = new TestCase01_client_send(s1, 1000 + i);
            try {
                testCase.start();
                testCase.stop();
            } catch (Exception e) {
                testCase.onError();
                e.printStackTrace();
                assert false;
            }
        }
    }

    @Test
    public void TestCase11_autoReconnect() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            BaseTestCase testCase = new TestCase11_autoReconnect(s1, 1100 + i);
            try {
                testCase.start();
                testCase.stop();
            } catch (Exception e) {
                testCase.onError();
                e.printStackTrace();
                assert false;
            }
        }
    }

    @Test
    public void TestCase12_session_close() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            BaseTestCase testCase = new TestCase12_client_session_close(s1, 1200 + i);
            try {
                testCase.start();
                testCase.stop();
            } catch (Exception e) {
                testCase.onError();
                e.printStackTrace();
                assert false;
            }
        }
    }

    @Test
    public void TestCase13_sendAndRequest_timeout() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            BaseTestCase testCase = new TestCase13_sendAndRequest_timeout(s1, 1300 + i);
            try {
                testCase.start();
                testCase.stop();
            } catch (Exception e) {
                testCase.onError();
                e.printStackTrace();
                assert false;
            }
        }
    }

    @Test
    public void TestCase14_file() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            if(s1.contains("udp")){
                continue;
            }

            BaseTestCase testCase = new TestCase14_file(s1, 1400 + i);
            try {
                testCase.start();
                testCase.stop();
            } catch (Exception e) {
                testCase.onError();
                e.printStackTrace();
                assert false;
            }
        }
    }

    @Test
    public void TestCase15_size() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            if(s1.contains("udp")){
                continue;
            }

            BaseTestCase testCase = new TestCase15_size(s1, 1500 + i);
            try {
                testCase.start();
                testCase.stop();
            } catch (Exception e) {
                testCase.onError();
                e.printStackTrace();
                assert false;
            }
        }
    }

    @Test
    public void TestCase16_url_auth() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];

            BaseTestCase testCase = new TestCase16_url_auth(s1, 1600 + i);
            try {
                testCase.start();
                testCase.stop();
            } catch (Exception e) {
                testCase.onError();
                e.printStackTrace();
                assert false;
            }
        }
    }

    @Test
    public void TestCase17_idleTimeout() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];

            if("sd:udp-java".equals(s1)){
                continue;
            }

            BaseTestCase testCase = new TestCase17_idleTimeout(s1, 1700 + i);
            try {
                testCase.start();
                testCase.stop();
            } catch (Exception e) {
                testCase.onError();
                e.printStackTrace();
                assert false;
            }
        }
    }

    @Test
    public void TestCase18_clientCloseReconnect() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];

            BaseTestCase testCase = new TestCase18_clientCloseReconnect(s1, 1800 + i);
            try {
                testCase.start();
                testCase.stop();
            } catch (Exception e) {
                testCase.onError();
                e.printStackTrace();
                assert false;
            }
        }
    }

    @Test
    public void TestCase19_serverCloseReconnect() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];

            BaseTestCase testCase = new TestCase19_serverCloseReconnect(s1, 1900 + i);
            try {
                testCase.start();
                testCase.stop();
            } catch (Exception e) {
                testCase.onError();
                e.printStackTrace();
                assert false;
            }
        }
    }

    @Test
    public void TestCase20_sendAndRequest2rep() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];

            BaseTestCase testCase = new TestCase20_sendAndRequest2rep(s1, 2000 + i);
            try {
                testCase.start();
                testCase.stop();
            } catch (Exception e) {
                testCase.onError();
                e.printStackTrace();
                assert false;
            }
        }
    }

    @Test
    public void TestCase21_sendAndSubscribe2rep() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];

            BaseTestCase testCase = new TestCase21_sendAndSubscribe2rep(s1, 2100 + i);
            try {
                testCase.start();
                testCase.stop();
            } catch (Exception e) {
                testCase.onError();
                e.printStackTrace();
                assert false;
            }
        }
    }

    @Test
    public void TestCase24_bigFile_1g() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];

            if("sd:udp-java".equals(s1)){
                continue;
            }

            BaseTestCase testCase = new TestCase24_bigFile_1g(s1, 2400 + i);
            try {
                testCase.start();
                testCase.stop();
            } catch (Exception e) {
                testCase.onError();
                e.printStackTrace();
                assert false;
            }
        }
    }

    @Test
    public void TestCase25_bigString() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];

            if("sd:udp-java".equals(s1)){
                continue;
            }

            BaseTestCase testCase = new TestCase25_bigString(s1, 2500 + i);
            try {
                testCase.start();
                testCase.stop();
            } catch (Exception e) {
                testCase.onError();
                e.printStackTrace();
                assert false;
            }
        }
    }

    @Test
    public void TestCase26_sendAndRequest_async() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];

            BaseTestCase testCase = new TestCase26_sendAndRequest_async(s1, 2600 + i);
            try {
                testCase.start();
                testCase.stop();
            } catch (Exception e) {
                testCase.onError();
                e.printStackTrace();
                assert false;
            }
        }
    }

    @Test
    public void TestCase27_smallFile() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];

            if("sd:udp-java".equals(s1)){
                continue;
            }

            BaseTestCase testCase = new TestCase27_smallFile(s1, 2700 + i);
            try {
                testCase.start();
                testCase.stop();
            } catch (Exception e) {
                testCase.onError();
                e.printStackTrace();
                assert false;
            }
        }
    }

    @Test
    public void TestCase28_timeout() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];

            if("sd:udp-java".equals(s1)){
                continue;
            }

            BaseTestCase testCase = new TestCase28_timeout(s1, 2800 + i);
            try {
                testCase.start();
                testCase.stop();
            } catch (Exception e) {
                testCase.onError();
                e.printStackTrace();
                assert false;
            }
        }
    }
}
