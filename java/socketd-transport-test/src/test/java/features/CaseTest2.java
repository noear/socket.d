package features;

import features.cases.*;
import org.junit.jupiter.api.Test;

/**
 * @author noear
 * @since 2.0
 */
public class CaseTest2 {
    static final String[] schemas = new String[]{
            "sd:tcp-java", "sd:tcp-netty", "sd:tcp-smartsocket",
            "sd:ws-java",
            "sd:udp-java"};

    @Test
    public void TestCase23_time2m() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];

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
