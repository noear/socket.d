package features.cases;

import org.junit.jupiter.api.Assertions;
import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.SimpleListener;
import org.noear.socketd.transport.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.socket.Protocol;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.extension.plugins.SslPlugin;
import org.smartboot.socket.extension.processor.AbstractMessageProcessor;
import org.smartboot.socket.extension.ssl.factory.ClientSSLContextFactory;
import org.smartboot.socket.extension.ssl.factory.ServerSSLContextFactory;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioQuickServer;
import org.smartboot.socket.transport.AioSession;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.cert.X509Certificate;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试：ssl
 *
 * @author noear
 * @since 2.0
 */
public class TestCase22_ssl extends BaseTestCase {
    private static Logger log = LoggerFactory.getLogger(TestCase22_ssl.class);

    public TestCase22_ssl(String schema, int port) {
        super(schema, port);
    }

    private Server server;
    private ClientSession clientSession;

    private AtomicInteger serverOnMessageCounter = new AtomicInteger();
    private AtomicInteger clientSubscribeReplyCounter = new AtomicInteger();


    public static ClientSSLContextFactory getClientSSLContext() throws Exception {
        //return new ClientSSLContextFactory().create();
        return new ClientSSLContextFactory(
                ClassLoader.getSystemResourceAsStream("ssl/jks/trustKeystore.jks"),
                "123456",
                ClassLoader.getSystemResourceAsStream("ssl/jks/keystore.jks"),
                "123456"
        );
    }

    public static ServerSSLContextFactory getServerSSLContext() throws Exception {
        //return new AutoServerSSLContextFactory().create();
        return new ServerSSLContextFactory(
                ClassLoader.getSystemResourceAsStream("ssl/jks/keystore.jks"),
                "123456",
                "123456",
                ClassLoader.getSystemResourceAsStream("ssl/jks/trustKeystore.jks"),
                "123456");
    }


    @Override
    public void start() throws Exception {
        log.trace("...");

        SSLContext serverSSLContext = getServerSSLContext().create();
        SSLContext clientSSLContext = getClientSSLContext().create();

        super.start();
        //server
        server = SocketD.createServer(getSchema())
                .config(c -> c.port(getPort()).sslContext(serverSSLContext))
                .listen(new SimpleListener() {
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        System.out.println("::" + message);
                        serverOnMessageCounter.incrementAndGet();

                        if (message.isRequest()) {
                            session.reply(message, new StringEntity("hi reply")); //这之后，无效了
                            session.reply(message, new StringEntity("hi reply**"));
                        }

                        if (message.isSubscribe()) {
                            session.reply(message, new StringEntity("hi reply"));
                            session.reply(message, new StringEntity("hi reply**"));
                            session.replyEnd(message, new StringEntity("hi reply****")); //这之后，无效了
                            session.reply(message, new StringEntity("hi reply******"));
                            session.reply(message, new StringEntity("hi reply********"));
                        }

                        session.send("demo", new StringEntity("test"));
                    }
                }).start();

        //休息下，启动可能要等会儿
        Thread.sleep(1000);

        //client
        String serverUrl = getSchema() + "://127.0.0.1:" + getPort() + "/path?u=a&p=2";
        clientSession = SocketD.createClient(serverUrl).config(c -> c.sslContext(clientSSLContext)).openOrThow();
        clientSession.send("/user/created", new StringEntity("hi"));

        Entity response = clientSession.sendAndRequest("/user/get", new StringEntity("hi")).await();
        System.out.println("sendAndRequest====" + response);

        clientSession.sendAndSubscribe("/user/sub", new StringEntity("hi")).thenReply(message -> {
            clientSubscribeReplyCounter.incrementAndGet();
            System.out.println("sendAndSubscribe====" + message);
        });

        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(100);
                clientSession.send("/user/updated", new StringEntity("hi"));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        //休息下（发完，那边还得收）
        Thread.sleep(1000);

        System.out.println("counter: " + serverOnMessageCounter.get() + ", " + clientSubscribeReplyCounter.get());

        Assertions.assertNotNull(response, getSchema() + ":sendAndRequest 返回不对");
        Assertions.assertEquals("hi reply", response.dataAsString(), getSchema() + ":sendAndRequest 返回不对");
        Assertions.assertEquals(serverOnMessageCounter.get(), 6, getSchema() + ":server 收的消息数量对不上");
        Assertions.assertEquals(clientSubscribeReplyCounter.get(), 3, getSchema() + ":client 订阅回收数量对不上");
    }

    @Override
    public void stop() throws Exception {
        if (clientSession != null) {
            clientSession.close();
        }

        if (server != null) {
            server.stop();
        }

        super.stop();
    }

    public static void main(String[] args) throws Exception {
        IntegerServerProcessor serverProcessor = new IntegerServerProcessor();
        AioQuickServer sslQuickServer = new AioQuickServer(8080, new IntegerProtocol(), serverProcessor);
        ServerSSLContextFactory serverFactory = getServerSSLContext();
        SslPlugin<Integer> sslServerPlugin = new SslPlugin<>(serverFactory); //ClientAuth.REQUIRE
        serverProcessor.addPlugin(sslServerPlugin);
        sslQuickServer.start();

        IntegerClientProcessor clientProcessor = new IntegerClientProcessor();
        AioQuickClient sslQuickClient = new AioQuickClient("localhost", 8080, new IntegerProtocol(), clientProcessor);
        ClientSSLContextFactory clientFactory = getClientSSLContext();
        SslPlugin<Integer> sslPlugin = new SslPlugin<>(clientFactory);
        clientProcessor.addPlugin(sslPlugin);
        AioSession aioSession = sslQuickClient.start();
        while (true) {
            aioSession.writeBuffer().writeInt(1);
            aioSession.writeBuffer().flush();
        }
    }

    public static class IntegerClientProcessor extends AbstractMessageProcessor<Integer> {

        @Override
        public void process0(AioSession session, Integer msg) {
            System.out.println("receive data from server：" + msg);
        }

        @Override
        public void stateEvent0(AioSession session, StateMachineEnum stateMachineEnum, Throwable throwable) {
            System.out.println("other state:" + stateMachineEnum);
            if (stateMachineEnum == StateMachineEnum.INPUT_EXCEPTION) {
                throwable.printStackTrace();
            }
            if (stateMachineEnum == StateMachineEnum.OUTPUT_EXCEPTION) {
                throwable.printStackTrace();
            }
        }
    }

    public static class IntegerServerProcessor extends AbstractMessageProcessor<Integer> {
        @Override
        public void process0(AioSession session, Integer msg) {
            Integer respMsg = msg + 1;
            System.out.println("receive data from client: " + msg + " ,rsp:" + (respMsg));
            try {
                session.writeBuffer().writeInt(respMsg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void stateEvent0(AioSession session, StateMachineEnum stateMachineEnum, Throwable throwable) {
            if (stateMachineEnum == StateMachineEnum.INPUT_EXCEPTION) {
                throwable.printStackTrace();
            }
        }
    }

    public static class IntegerProtocol implements Protocol<Integer> {

        private static final int INT_LENGTH = 4;

        @Override
        public Integer decode(ByteBuffer data, AioSession session) {
            if (data.remaining() < INT_LENGTH)
                return null;
            return data.getInt();
        }

    }
}