package benchmark;

import benchmark.cases.TestCase01;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author noear
 * @since 2.0
 */
public class BenchmarkTest {
    static final String[] schemas = new String[]{
            "tcp-java", "tcp-netty", "tcp-smartsocket",
            "ws-java",
            "udp-java"};


    /**
     * 用于调试
     */
    public static void main(String[] args) throws Exception {
        int count = 1000;
        int timeout = 1;

        String s1 = schemas[3];
        TestCase01 testCase01 = new TestCase01(s1, timeout, count, 9386);
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
        int count = 10000000;
        int timeout = 10;

        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            TestCase01 testCase01 = new TestCase01(s1, timeout, count, 9386 + i);
            try {
                testCase01.start();

                testCase01.send();
                testCase01.sendAndRequest();
                testCase01.sendAndSubscribe();

                //testCase01.stop();

                Thread.sleep(timeout * 1000);
            } catch (Exception e) {
                testCase01.onError();
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testCase01_send() throws Exception {
        int count = 10000000;
        int timeout = 10;

        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            TestCase01 testCase01 = new TestCase01(s1, timeout, count, 9386 + i);
            try {
                testCase01.start();

                testCase01.send();

                Thread.sleep(timeout * 1000);
            } catch (Exception e) {
                testCase01.onError();
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testCase01_send_th() throws Exception {
        int count = 1000000;
        int timeout = 10;
        List<Integer> tasks = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        String s1 = schemas[0];
        TestCase01 testCase01 = new TestCase01(s1, timeout, count, 9386);
        try {
            testCase01.start();
            testCase01.send();
            testCase01.send();
            testCase01.send();

            long start = System.currentTimeMillis();

            tasks.parallelStream().forEach(n -> {
                try {
                    testCase01.send();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            System.out.println(s1+"::" + (System.currentTimeMillis() - start));
            Thread.sleep(timeout * 1000);
        } catch (Exception e) {
            testCase01.onError();
            e.printStackTrace();
        }
    }
}
