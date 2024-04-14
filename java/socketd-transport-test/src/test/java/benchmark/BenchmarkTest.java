package benchmark;

import benchmark.cases.TestCase01;
import benchmark.cases.TestCase02;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author noear
 * @since 2.0
 */
@ExtendWith(SolonJUnit5Extension.class)
@SolonTest
public class BenchmarkTest {
    static final String[] schemas = new String[]{
            "sd:tcp-java",
            "sd:tcp-netty",
            "sd:tcp-smartsocket",
            "sd:ws-java",
            "sd:udp-java",
            "sd:udp-netty",
            "sd:kcp-java",
    };

    int count = 100_000;
    int timeout = 10;

    @Test
    public void testCase01() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            TestCase01 testCase01 = new TestCase01(s1, timeout, count, 9386 + i);
            try {
                testCase01.start();

                testCase01.send(false);
                testCase01.send();
                testCase01.sendAndRequest(false);
                testCase01.sendAndRequest();
                testCase01.sendAndSubscribe(false);
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
    public void testCase02() throws Exception {
        for (int i = 0; i < schemas.length; i++) {
            String s1 = schemas[i];
            TestCase02 testCase02 = new TestCase02(s1, timeout, count, 9386 + i);
            try {
                testCase02.start();

                testCase02.send(false);
                testCase02.send();
                testCase02.sendAndRequest(false);
                testCase02.sendAndRequest();
                testCase02.sendAndSubscribe(false);
                testCase02.sendAndSubscribe();

                //testCase02.stop();

                Thread.sleep(timeout * 1000);
            } catch (Exception e) {
                testCase02.onError();
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testCase01_send() throws Exception {
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
