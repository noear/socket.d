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

    @Test
    public void testCase01() throws Exception {
        int count = 1000000;
        int timeout = 2;

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
        int count = 1000000;
        int timeout = 2;

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
    public void testCase01_sendAndRequest() throws Exception {
        int count = 1000000;
        int timeout = 2;

        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            TestCase01 testCase01 = new TestCase01(s1, timeout, count, 9386 + i);
            try {
                testCase01.start();

                testCase01.sendAndRequest();

                Thread.sleep(timeout * 1000);
            } catch (Exception e) {
                testCase01.onError();
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testCase01_sendAndSubscribe() throws Exception {
        int count = 1000000;
        int timeout = 2;

        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            TestCase01 testCase01 = new TestCase01(s1, timeout, count, 9386 + i);
            try {
                testCase01.start();

                testCase01.sendAndSubscribe();

                Thread.sleep(timeout * 1000);
            } catch (Exception e) {
                testCase01.onError();
                e.printStackTrace();
            }
        }
    }
}
