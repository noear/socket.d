package labs.cae01_bigupload;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.EntityMetas;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.fragment.FragmentHandlerTempfile;
import org.noear.socketd.transport.core.listener.SimpleListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.channels.FileChannel;

public class BigUploadServer {
    static final String[] schemas = new String[]{
            "sd:tcp-java",
            "sd:tcp-neta",
            "sd:tcp-netty",
            "sd:tcp-smartsocket",
            "sd:ws-java",
            "sd:udp-java",
            "sd:udp-neta",
    };

    static String getSchema() {
        return schemas[2];
    }

    static int getPort() {
        return 2100;
    }

    public static void main(String[] args) throws IOException {
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        String pid = rb.getName().split("@")[0];
        System.out.println("pid=" + pid);

        //server
        SocketD.createServer(getSchema())
                .config(c -> c.port(getPort()).fragmentHandler(new FragmentHandlerTempfile()))
                .listen(new SimpleListener() {
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        System.out.println("::" + message);

                        String fileName = message.meta(EntityMetas.META_DATA_DISPOSITION_FILENAME);

                        if (fileName != null) {
                            System.out.println(fileName);
                            File fileNew = new File("/Users/noear/Downloads/socketd-big-upload.mov");
                            fileNew.delete();

                            fileNew.createNewFile();

                            try {
                                try (FileChannel fileChannel = new FileOutputStream(fileNew).getChannel()) {
                                    fileChannel.write(message.data());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                message.release();
                            }
                        }

                        if (message.isRequest() || message.isSubscribe()) {
                            session.replyEnd(message, new StringEntity(""));
                        }
                    }
                })
                .start();
    }
}
