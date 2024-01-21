package labs;

import features.cases.*;
import org.junit.jupiter.api.Test;

public class DebugTest {

    static final String[] schemas = new String[]{
            "sd:tcp-java",
            "sd:tcp-netty",
            "sd:tcp-smartsocket",
            "sd:ws-java",
            "sd:udp-java",
            "sd:udp-netty",
            "sd:kcp-java",
    };

    /**
     * 用于调试
     */

    public static void main(String[] args) throws Exception {
        String s1 = schemas[0];
        BaseTestCase testCase = new TestCase31_openAnTry(s1, 2100);
        try {
            testCase.start();
            //testCase.stop();
        } catch (Exception e) {
            testCase.onError();
            e.printStackTrace();
        }
    }
}
