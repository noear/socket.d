package demo.demo03;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.SimpleListener;

import java.io.IOException;

public class Demo03_UrlAuth {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer("sd:tcp")
                .config(c -> c.port(8602))
                .listen(new SimpleListener() {
                    @Override
                    public void onOpen(Session session) throws IOException {
                        String user = session.param("u");
                        if ("noear".equals(user) == false) { //如果不是 noear，关闭会话
                            session.close();
                        }
                    }

                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        System.out.println(message);
                    }
                })
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成

        //::打开客户端会话
        //会成功
        ClientSession clientSession1 = SocketD.createClient("sd:tcp://127.0.0.1:8602/?u=noear&p=2").openOrThow();
        clientSession1.send("/demo", new StringEntity("hi"));

        //会失败
        ClientSession clientSession2 = SocketD.createClient("sd:tcp://127.0.0.1:8602/?u=solon&p=1").openOrThow();
        clientSession2.send("/demo2", new StringEntity("hi"));
    }
}
