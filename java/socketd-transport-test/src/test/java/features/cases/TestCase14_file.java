package features.cases;

import org.junit.jupiter.api.Assertions;
import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.entity.FileEntity;
import org.noear.socketd.transport.core.listener.EventListener;
import org.noear.socketd.transport.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * sendAndRequest() 超时
 *
 * @author noear
 * @since 2.0
 */
public class TestCase14_file extends BaseTestCase {
    private static Logger log = LoggerFactory.getLogger(TestCase14_file.class);

    public TestCase14_file(String schema, int port) {
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
                .config(c -> c.port(getPort()))
                .listen(new EventListener().doOn("/user/upload", (s, m) -> {
                    System.out.println("::" + m);
                    messageCounter.incrementAndGet();

                    String fileName = m.meta(EntityMetas.META_DATA_DISPOSITION_FILENAME);

                    if (fileName != null) {
                        System.out.println(fileName);
                        File fileNew = new File("/Users/noear/Downloads/socketd-upload.mov");
                        fileNew.delete();

                        fileNew.createNewFile();

                        try {
                            try (OutputStream outputStream = new FileOutputStream(fileNew)) {
                                outputStream.write(m.dataAsBytes());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }))
                .start();

        //休息下，启动可能要等会儿
        Thread.sleep(1000);


        //client
        String serverUrl = getSchema() + "://127.0.0.1:" + getPort() + "/path?u=a&p=2";
        clientSession = SocketD.createClient(serverUrl)
                .config(c -> c.fragmentSize(1024 * 1024))
                .openOrThow();

        AtomicInteger fileCount = new AtomicInteger();
        AtomicInteger fileTotal = new AtomicInteger();
        FileEntity fileEntity = new FileEntity(new File("/Users/noear/Movies/snack3-rce-poc.mov"));
        clientSession.send("/user/upload", fileEntity).thenProgress((isSend, val, max) -> {
            if (isSend) {
                fileCount.incrementAndGet();
                fileTotal.set(max.intValue());
            }
        });
        fileEntity.release();


        Thread.sleep(10000);

        System.out.println("counter: " + messageCounter.get());
        System.out.println("fileCount: " + fileCount.get() + ", fileTotal: " + fileTotal.get());
        Assertions.assertEquals(messageCounter.get(), 1, getSchema() + ":server 收的消息数量对不上");

        File file = new File("/Users/noear/Downloads/socketd-upload.mov");
        assert file.length() > 1024 * 1024 * 10;
        //assert fileCount.get() > 0;
        //assert fileCount.get() == fileTotal.get();
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