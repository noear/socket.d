package demo.demo02_base;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.SimpleListener;

import java.io.IOException;

public class Demo02_Send {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer("sd:ws")
                .config(c -> c.port(8602))
                .listen(new SimpleListener(){
                    @Override
                    public void onMessage(Session session, Message message) throws IOException {
                        System.out.println(message);
                    }
                })
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成

        //::打开客户端会话
        ClientSession clientSession  = SocketD.createClient("sd:ws://127.0.0.1:8602/?u=a&p=2")
                .openOrThow();

        //发送（单线程约为 200万/秒 的速率，2020年的 macbook。新电脑估计会更快）
        clientSession.send("/demo", new StringEntity("hello wrold!"));
    }
}
