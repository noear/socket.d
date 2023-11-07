package features;

import features.cases.*;
import org.junit.jupiter.api.Test;

/**
 * @author noear
 * @since 2.0
 */
public class CaseTest {
    static final String[] schemas = new String[]{
            "tcp-java", "tcp-netty", "tcp-smartsocket",
            "ws-java",
            "udp-java"};


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
            }
        }
    }

    @Test
    public void TestCase11_autoReconnect() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            BaseTestCase testCase = new TestCase11_autoReconnect(s1, 2000 + i);
            try {
                testCase.start();
                testCase.stop();
            } catch (Exception e) {
                testCase.onError();
                e.printStackTrace();
            }
        }
    }

    @Test
    public void TestCase12_session_close() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            BaseTestCase testCase = new TestCase12_session_close(s1, 3000 + i);
            try {
                testCase.start();
                testCase.stop();
            } catch (Exception e) {
                testCase.onError();
                e.printStackTrace();
            }
        }
    }

    @Test
    public void TestCase13_sendAndRequest_timeout() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            BaseTestCase testCase = new TestCase13_sendAndRequest_timeout(s1, 4000 + i);
            try {
                testCase.start();
                testCase.stop();
            } catch (Exception e) {
                testCase.onError();
                e.printStackTrace();
            }
        }
    }

    @Test
    public void TestCase14_file() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            if(s1.startsWith("udp")){
                continue;
            }

            BaseTestCase testCase = new TestCase14_file(s1, 4000 + i);
            try {
                testCase.start();
                testCase.stop();
            } catch (Exception e) {
                testCase.onError();
                e.printStackTrace();
            }
        }
    }

    @Test
    public void TestCase15_size() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            if(s1.startsWith("udp")){
                continue;
            }

            BaseTestCase testCase = new TestCase15_size(s1, 4000 + i);
            try {
                testCase.start();
                testCase.stop();
            } catch (Exception e) {
                testCase.onError();
                e.printStackTrace();
            }
        }
    }

    @Test
    public void TestCase16_url_auth() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];

            BaseTestCase testCase = new TestCase16_url_auth(s1, 4000 + i);
            try {
                testCase.start();
                testCase.stop();
            } catch (Exception e) {
                testCase.onError();
                e.printStackTrace();
            }
        }
    }

    @Test
    public void TestCase17_idleTimeout() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];

            BaseTestCase testCase = new TestCase17_idleTimeout(s1, 4000 + i);
            try {
                testCase.start();
                testCase.stop();
            } catch (Exception e) {
                testCase.onError();
                e.printStackTrace();
            }
        }
    }
}
