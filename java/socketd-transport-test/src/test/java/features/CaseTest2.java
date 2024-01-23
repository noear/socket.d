package features;

import features.cases.*;
import org.junit.jupiter.api.Test;

/**
 * @author noear
 * @since 2.0
 */
public class CaseTest2 {
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

    @Test
    public void TestCase23_time2m() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[3];

            BaseTestCase testCase = new TestCase23_time2m(s1, 2300 + i);
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
