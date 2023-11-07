package demo.demo04;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.BuilderListener;
import org.noear.socketd.transport.server.ServerConfig;

public class Demo04_Builder {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer(new ServerConfig("tcp").port(8602))
                .listen(new BuilderListener().onMessage((s,m)->{
                    System.out.println(m);
                    s.send("/demo", new StringEntity("Me too!"));
                }))
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成

        //::打开客户端会话
        Session session = SocketD.createClient("tcp://127.0.0.1:8602/hello?u=a&p=2")
                .listen(new BuilderListener().onMessage((s, m) -> {
                    System.out.println(m);
                }).on("/demo", (s, m) -> { //带了主题路由的功能
                    System.out.println(m);
                }))
                .open();
        session.send("/order", new StringEntity("Hi"));
        session.send("/user", new StringEntity("Hi"));
    }
}
