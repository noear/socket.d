package demo.demo03;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.EntityMetas;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.FileEntity;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.SimpleListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class Demo03_File {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer("sd:tcp")
                .config(c -> c.port(8602))
                .listen(new SimpleListener(){
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        String fileName = message.meta(EntityMetas.META_DATA_DISPOSITION_FILENAME);

                        if (fileName != null) {
                            File fileNew = new File("/Users/noear/Downloads/socketd-upload_2.mov");
                            fileNew.createNewFile();

                            try (OutputStream outputStream = new FileOutputStream(fileNew)) {
                                outputStream.write(message.dataAsBytes());
                            }
                        }else{
                            System.out.println(message);
                        }
                    }
                })
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成

        //::打开客户端会话
        ClientSession clientSession  = SocketD.createClient("sd:tcp://127.0.0.1:8602/?u=a&p=2")
                .openOrThow();

        //发送 + 元信息
        clientSession.send("/demo", new StringEntity("{user:'noear'}").metaPut("Trace-Id", UUID.randomUUID().toString()));
        //发送文件
        clientSession.send("/demo2", new FileEntity(new File("/Users/noear/Downloads/socketd-upload.mov")));
    }
}
