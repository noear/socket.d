package benchmark;

import benchmark.cases.TestCase01;
import org.junit.jupiter.api.Test;

/**
 * @author noear
 * @since 2.0
 */
public class BenchmarkTest {
    static final String[] schemas = new String[]{
            "tcp-java", "tcp-netty", "tcp-smartsocket",
            "udp-java",
            "ws-java",};


    /**
     * 用于调试
     */
    public static void main(String[] args) throws Exception {
        String s1 = schemas[3];
        TestCase01 testCase01 = new TestCase01(s1, 100000,9386);
        try {
            testCase01.start();

            testCase01.send();
            testCase01.sendAndRequest();
            testCase01.sendAndSubscribe();

           // testCase01.stop();
        } catch (Exception e) {
            testCase01.onError();
            e.printStackTrace();
        }
    }

    @Test
    public void testCase01() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            TestCase01 testCase01 = new TestCase01(s1, 1000, 9386 + i);
            try {
                testCase01.start();

                testCase01.send();
                testCase01.sendAndRequest();
                testCase01.sendAndSubscribe();

                //testCase01.stop();

                Thread.sleep(1000 * 2);
            } catch (Exception e) {
                testCase01.onError();
                e.printStackTrace();
            }
        }
    }
}
