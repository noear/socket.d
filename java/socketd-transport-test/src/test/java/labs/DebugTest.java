package labs;

import features.cases.BaseTestCase;
import features.cases.TestCase01_client_send;
import features.cases.TestCase14_file;

public class DebugTest {

    static final String[] schemas = new String[]{
            "tcp-java", "tcp-netty", "tcp-smartsocket",
            "ws-java",
            "udp-java"};

    /**
     * 用于调试
     */
    public static void main(String[] args) throws Exception {
        String s1 = schemas[2];
        BaseTestCase testCase01 = new TestCase01_client_send(s1, 2000);
        try {
            testCase01.start();
            testCase01.stop();
        } catch (Exception e) {
            testCase01.onError();
            e.printStackTrace();
        }
    }
}
