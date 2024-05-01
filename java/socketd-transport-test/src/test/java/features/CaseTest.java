package features;

import features.cases.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;

/**
 * @author noear
 * @since 2.0
 */
@ExtendWith(SolonJUnit5Extension.class)
@SolonTest
public class CaseTest {
    static final String[] schemas = new String[]{
            "sd:tcp-java",
            "sd:tcp-netty",
            "sd:tcp-smartsocket",
//            "sd:tcp-neta",
            "sd:ws-java",
            "sd:udp-java",
            "sd:udp-netty",
            "sd:kcp-java",
    };

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
    public void TestCase02_handshake() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            BaseTestCase testCase = new TestCase02_handshake(s1, 1020 + i);
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
    public void TestCase14_fileUpload() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];

            if (s1.contains("udp") || s1.contains("kcp")) {
                continue;
            }

            BaseTestCase testCase = new TestCase14_fileUpload(s1, 1400 + i);
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
    public void TestCase15_metaSizeLimit() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];

            if (s1.contains("udp") || s1.contains("kcp")) {
                continue;
            }

            BaseTestCase testCase = new TestCase15_metaSizeLimit(s1, 1500 + i);
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

            if (s1.contains("udp") || s1.contains("kcp")) {
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

            if (s1.contains("udp") || s1.contains("kcp")) {
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

            if (s1.contains("udp") || s1.contains("kcp")) {
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

            if (s1.contains("udp") || s1.contains("kcp")) {
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

    @Test
    public void TestCase29_downloadProgress() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];

            if (s1.contains("udp") || s1.contains("kcp")) {
                continue;
            }

            BaseTestCase testCase = new TestCase29_downloadProgress(s1, 2900 + i);
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
    public void TestCase30_meta_auth() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];

            BaseTestCase testCase = new TestCase30_meta_auth(s1, 3000 + i);
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
    public void TestCase31_openAnTry() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];

            BaseTestCase testCase = new TestCase31_openAnTry(s1, 3100 + i);
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
    public void TestCase32_openAnTry2() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];

            BaseTestCase testCase = new TestCase32_openAnTry2(s1, 3200 + i);
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
    public void TestCase33_client_preclose() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];

            BaseTestCase testCase = new TestCase33_client_preclose(s1, 3300 + i);
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
    public void TestCase34_inner_close() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];

            BaseTestCase testCase = new TestCase34_inner_close(s1, 3400 + i);
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
    public void TestCase35_sendAlarm() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];

            BaseTestCase testCase = new TestCase35_sendAlarm(s1, 3500 + i);
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
    public void TestCase36_sendAlarm_async() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];

            BaseTestCase testCase = new TestCase36_sendAlarm_async(s1, 3600 + i);
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
    public void TestCase37_server_prestop() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];

            BaseTestCase testCase = new TestCase37_server_prestop(s1, 3700 + i);
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
    public void TestCase38_clientCloseSelf() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];

            BaseTestCase testCase = new TestCase38_clientCloseSelf(s1, 3800 + i);
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
    public void TestCase39_serverCloseSelf() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];

            BaseTestCase testCase = new TestCase39_serverCloseSelf(s1, 3900 + i);
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
