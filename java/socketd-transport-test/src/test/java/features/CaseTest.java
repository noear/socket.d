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
    public void testCase01() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            TestCase01_client_send testCase = new TestCase01_client_send(s1, 1000 + i);
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
    public void testCase11() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            TestCase11_autoReconnect testCase = new TestCase11_autoReconnect(s1, 2000 + i);
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
    public void testCase12() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            TestCase12_session_close testCase = new TestCase12_session_close(s1, 3000 + i);
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
    public void testCase13() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            TestCase13_sendAndRequest_timeout testCase = new TestCase13_sendAndRequest_timeout(s1, 4000 + i);
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
    public void testCase14() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            if(s1.startsWith("udp")){
                continue;
            }

            TestCase14_file testCase = new TestCase14_file(s1, 4000 + i);
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
    public void testCase15() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            if(s1.startsWith("udp")){
                continue;
            }

            TestCase15_size testCase = new TestCase15_size(s1, 4000 + i);
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
