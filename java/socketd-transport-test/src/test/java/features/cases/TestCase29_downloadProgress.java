package features.cases;

import org.junit.jupiter.api.Assertions;
import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.entity.FileEntity;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.EventListener;
import org.noear.socketd.transport.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试：文件下载进度
 *
 * @author noear
 * @since 2.0
 */
public class TestCase29_downloadProgress extends BaseTestCase {
    private static Logger log = LoggerFactory.getLogger(TestCase29_downloadProgress.class);

    public TestCase29_downloadProgress(String schema, int port) {
        super(schema, port);
    }

    private Server server;
    private ClientSession clientSession;

    private AtomicInteger messageCounter = new AtomicInteger();

    @Override
    public void start() throws Exception {
        log.trace("...");

        super.start();
        //server
        server = SocketD.createServer(getSchema())
                .config(c -> c.port(getPort()).fragmentSize(1024 * 1024))
                .listen(new EventListener().doOn("/download", (s, m) -> {
                    messageCounter.incrementAndGet();
                    if (m.isRequest()) {
                        FileEntity fileEntity = new FileEntity(new File("/Users/noear/Movies/snack3-rce-poc.mov"));
                        s.reply(m, fileEntity);
                    }
                }))
                .start();

        //休息下，启动可能要等会儿
        Thread.sleep(1000);


        //client
        String serverUrl = getSchema() + "://127.0.0.1:" + getPort() + "/path?u=a&p=2";
        clientSession = SocketD.createClient(serverUrl)
                .openOrThow();

        AtomicInteger fileCount = new AtomicInteger();
        AtomicInteger fileTotal = new AtomicInteger();
        clientSession.sendAndRequest("/download", new StringEntity("")).thenProgress((isSend, val, max) -> {
            if (isSend == false) {
                fileCount.incrementAndGet();
                fileTotal.set(max);
            }
        });


        Thread.sleep(4000);

        System.out.println("counter: " + messageCounter.get());
        System.out.println("fileCount: " + fileCount.get() + ", fileTotal: " + fileTotal.get());
        Assertions.assertEquals(messageCounter.get(), 1, getSchema() + ":server 收的消息数量对不上");

        assert fileCount.get() > 0;
        assert fileCount.get() == fileTotal.get();
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