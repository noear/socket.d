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
@SolonTest
public class CaseTest2 {
    static final String[] schemas = new String[]{
            "sd:tcp-java",
            "sd:tcp-netty",
            "sd:tcp-smartsocket",
//            "sd:tcp-neta",
            "sd:ws-java",
            "sd:udp-java",
//            "sd:udp-netty",
            "sd:kcp-java",
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
