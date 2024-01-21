package labs.cae01_bigupload;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.entity.FileEntity;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class BigUploadClient {

    static final String[] schemas = new String[]{
            "sd:tcp-java", "sd:tcp-netty", "sd:tcp-smartsocket",
            "sd:ws-java",
            "sd:udp-java"};

    static String getSchema() {
        return schemas[2];
    }

    static int getPort() {
        return 2100;
    }

    public static void main(String[] args) throws Exception {
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        String pid = rb.getName().split("@")[0];
        System.out.println("pid=" + pid);


        String serverUrl = getSchema() + "://127.0.0.1:" + getPort() + "/path?u=a&p=2";

        ClientSession clientSession = SocketD.createClient(serverUrl)
                .config(c -> c.fragmentSize(1024 * 1024)) //1m
                .openOrThow();
        int count = 0;
        while (true) {
            FileEntity fileEntity = new FileEntity(new File("/Users/noear/Movies/[Socket.D 实战] 直播手写 FolkMQ (4).mov"));
            clientSession.sendAndRequest("/user/upload", fileEntity);
            fileEntity.release();
            System.out.println("count=" + (count++));
        }
    }
}
