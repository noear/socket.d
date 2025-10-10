package labs;

import features.cases.*;
import org.junit.jupiter.api.Test;
import org.noear.solon.test.SolonTest;

@SolonTest
public class DebugTest {
    static final String[] schemas = new String[]{
            "sd:tcp-java",
            "sd:tcp-neta",//1
            "sd:tcp-netty",
            "sd:tcp-smartsocket",
            "sd:ws-java",
            "sd:udp-java",//5
            "sd:udp-neta",
            "sd:udp-netty",
            "sd:kcp-java",//8
    };

    /**
     * 用于调试
     */
    @Test
    public  void main() throws Exception {
        String s1 = schemas[0];
        BaseTestCase testCase = new TestCase22_ssl(s1, 8602);
        try {
            testCase.start();
            //testCase.stop();
        } catch (Exception e) {
            testCase.onError();
            e.printStackTrace();
        }
    }
}
