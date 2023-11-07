package labs;

import features.cases.BaseTestCase;
import features.cases.TestCase01_client_send;
import features.cases.TestCase14_file;
import features.cases.TestCase17_idleTimeout;

public class DebugTest {

    static final String[] schemas = new String[]{
            "sd:tcp-java", "sd:tcp-netty", "sd:tcp-smartsocket",
            "sd:ws-java",
            "sd:udp-java"};

    /**
     * 用于调试
     */
    public static void main(String[] args) throws Exception {
        String s1 = schemas[3];
        BaseTestCase testCase = new TestCase17_idleTimeout(s1, 2000);
        try {
            testCase.start();
            //testCase.stop();
        } catch (Exception e) {
            testCase.onError();
            e.printStackTrace();
        }
    }
}
