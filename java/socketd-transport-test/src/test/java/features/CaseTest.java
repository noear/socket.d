package features;

import features.cases.TestCase01;
import features.cases.TestCase11;
import features.cases.TestCase12;
import features.cases.TestCase13_sendAndRequest_timeout;
import org.junit.jupiter.api.Test;

/**
 * @author noear
 * @since 2.0
 */
public class CaseTest {
    static final String[] schemas = new String[]{
            "tcp-java", "tcp-netty", "tcp-smartsocket",
            "ws-java",
            "udp-java", "udp-netty"};

    /**
     * 用于调试
     * */
//    public static void main(String[] args) throws Exception {
//        String s1 = schemas[4];
//        TestCase11 testCase01 = new TestCase11(s1, 2000);
//        try {
//            testCase01.start();
//            testCase01.stop();
//        } catch (Exception e) {
//            testCase01.onError();
//            e.printStackTrace();
//        }
//    }

    @Test
    public void testCase01() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            TestCase01 testCase01 = new TestCase01(s1, 1000 + i);
            try {
                testCase01.start();
                testCase01.stop();
            } catch (Exception e) {
                testCase01.onError();
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testCase11() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            TestCase11 testCase01 = new TestCase11(s1, 2000 + i);
            try {
                testCase01.start();
                testCase01.stop();
            } catch (Exception e) {
                testCase01.onError();
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testCase12() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            TestCase12 testCase01 = new TestCase12(s1, 3000 + i);
            try {
                testCase01.start();
                testCase01.stop();
            } catch (Exception e) {
                testCase01.onError();
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testCase13() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            TestCase13_sendAndRequest_timeout testCase01 = new TestCase13_sendAndRequest_timeout(s1, 4000 + i);
            try {
                testCase01.start();
                testCase01.stop();
            } catch (Exception e) {
                testCase01.onError();
                e.printStackTrace();
            }
        }
    }
}
