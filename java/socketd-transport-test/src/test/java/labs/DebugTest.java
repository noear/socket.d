package labs;

import features.cases.*;

/**
 * @author noear 2023/11/4 created
 */
public class DebugTest {

    static final String[] schemas = new String[]{
            "tcp-java", "tcp-netty", "tcp-smartsocket",
            "ws-java",
            "udp-java"};

    /**
     * 用于调试
     */
    public static void main(String[] args) throws Exception {
        String s1 = schemas[1];
        TestCase11_autoReconnect testCase01 = new TestCase11_autoReconnect(s1, 2000);
        try {
            testCase01.start();
            testCase01.stop();
        } catch (Exception e) {
            testCase01.onError();
            e.printStackTrace();
        }
    }
}
