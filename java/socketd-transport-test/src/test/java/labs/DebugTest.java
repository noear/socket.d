package labs;

import features.cases.*;
import org.junit.jupiter.api.Test;

public class DebugTest {
    static final String[] schemas = new String[]{
            "sd:tcp-java",
            "sd:tcp-netty",
            "sd:tcp-smartsocket",
            "sd:tcp-neta",//3
            "sd:ws-java",
            "sd:udp-java",//5
            "sd:udp-netty",
            "sd:kcp-java",//7
    };

    /**
     * 用于调试
     */

    public static void main(String[] args) throws Exception {
        String s1 = schemas[2];
        BaseTestCase testCase = new TestCase33_closeStarting(s1, 8602);
        try {
            testCase.start();
            //testCase.stop();
        } catch (Exception e) {
            testCase.onError();
            e.printStackTrace();
        }
    }
}
