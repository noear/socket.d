package benchmark2;

import benchmark2.cases.TestCase01;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Threads(1)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class JMHMain_send {
    static final String[] schemas = new String[]{
            "tcp-java", "tcp-netty", "tcp-smartsocket",
            "ws-java",
            "udp-java"};

    TestCase01 testCase_tcp_java;
    TestCase01 testCase_tcp_netty;
    TestCase01 testCase_tcp_smartsocket;
    TestCase01 testCase_ws_java;
    TestCase01 testCase_udp_java;

    public static void main(String[] args) throws RunnerException {
        Options opt = new
                OptionsBuilder()
                .include(JMHMain_send.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }

    @Setup
    public void init() throws Exception {
        testCase_tcp_java = new TestCase01(schemas[0], 4000);
        testCase_tcp_java.start();

        testCase_tcp_netty = new TestCase01(schemas[1], 4000 + 1);
        testCase_tcp_netty.start();

        testCase_tcp_smartsocket = new TestCase01(schemas[2], 4000 + 2);
        testCase_tcp_smartsocket.start();

        testCase_ws_java = new TestCase01(schemas[3], 4000 + 3);
        testCase_ws_java.start();

        testCase_udp_java = new TestCase01(schemas[4], 4000 + 4);
        testCase_udp_java.start();
    }

    @Benchmark
    public void testCase_tcp_java_send() throws Exception {
        testCase_tcp_java.send();
    }

    @Benchmark
    public void testCase_tcp_netty_send() throws Exception {
        testCase_tcp_netty.send();
    }

    @Benchmark
    public void testCase_tcp_smartsocket_send() throws Exception {
        testCase_tcp_smartsocket.send();
    }

    @Benchmark
    public void testCase_ws_java_send() throws Exception {
        testCase_ws_java.send();
    }

    @Benchmark
    public void testCase_udp_java_send() throws Exception {
        testCase_udp_java.send();
    }
}
