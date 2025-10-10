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
import org.noear.socketd.utils.SslContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URL;
import java.security.KeyStore;
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

    private Server        server;
    private ClientSession clientSession;

    private AtomicInteger serverOnMessageCounter      = new AtomicInteger();
    private AtomicInteger clientSubscribeReplyCounter = new AtomicInteger();

    private SSLContext getSSLContext() throws Exception {
        URL url = TestCase22_ssl.class.getClassLoader().getResource("ssl/demo_store.pfx");

        return new SslContextBuilder().keyManager(url.openStream(), "PKCS12", "1234").build();
    }

    public static SSLContext getSSLContext(String protocol) throws Exception {
        char[] password = "123456".toCharArray();
        KeyStore jsk = KeyStore.getInstance("JKS");
        jsk.load(ClassLoader.getSystemResourceAsStream("ssl/jks/keystore.jks"), password);

        // Set up key manager factory to use our key store
        String keyAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(keyAlgorithm);
        kmf.init(jsk, password);

        // SSL Server
        SSLContext sslContext = SSLContext.getInstance(protocol);
        sslContext.init(kmf.getKeyManagers(), new TrustManager[] { new MyTrustManager() }, null);

        return sslContext;
    }

    static class MyTrustManager implements TrustManager, X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) {
            return;
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) {
            return;
        }
    }

    @Override
    public void start() throws Exception {
        log.trace("...");

        //SSLContext sslContext = getSSLContext();
        SSLContext sslContext = getSSLContext("TLSv1.2"); // SslProtocol see net.hasor.neta.handler.codec.ssl.SslProtocol

        super.start();
        //server
        server = SocketD.createServer(getSchema()).config(c -> c.port(getPort()).sslContext(sslContext)).listen(new SimpleListener() {
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
        clientSession = SocketD.createClient(serverUrl).config(c -> c.sslContext(sslContext)).openOrThow();
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
}
