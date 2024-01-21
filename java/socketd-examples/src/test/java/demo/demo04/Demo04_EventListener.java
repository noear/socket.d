package demo.demo04;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.EventListener;

public class Demo04_EventListener {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer("sd:tcp")
                .config(c -> c.port(8602))
                .listen(new EventListener().doOnMessage((session, message) -> {
                    System.out.println("server::" + message);
                    session.send("/demo", new StringEntity("Me too!"));
                    session.send("/demo2", new StringEntity("Me too!"));
                }))
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成

        //::打开客户端会话
        ClientSession clientSession  = SocketD.createClient("sd:tcp://127.0.0.1:8602/?u=a&p=2")
                .listen(new EventListener().doOnMessage((s, m) -> {
                    System.out.println("client::" + m);
                }).doOn("/demo", (s, m) -> { //带了事件路由的功能
                    System.out.println("on::" + m.event() + "::" + m);
                }).doOn("/demo2", (s, m)->{

                }))
                .openOrThow();

        clientSession.send("/order", new StringEntity("Hi"));
        clientSession.send("/user", new StringEntity("Hi"));
    }
}
