package demo.demo04;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.EventListener;
import org.noear.socketd.transport.core.listener.PathListener;

public class Demo04_PathListener {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer("sd:tcp")
                .config(c -> c.port(8602))
                .listen(new PathListener()
                        .doOf("/", new EventListener().doOnMessage((s, m) -> {
                            //用户频道
                            System.out.println("user::" + m);
                        }))
                        .doOf("/admin", new EventListener().doOnOpen(s -> {
                            //管理员频道
                            if ("admin".equals(s.param("u")) == false) {
                                s.close();
                            }
                        }).doOnMessage((s, m) -> {
                            System.out.println("admin::" + m);
                        })))
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成

        //::打开客户端会话
        //用户频道（链接地址的 path ，算为频道）
        ClientSession clientSession1 = SocketD.createClient("sd:tcp://127.0.0.1:8602/?u=a&p=2").openOrThow();
        clientSession1.send("/demo", new StringEntity("Hi"));

        //管理员频道（链接地址的 path ，算为频道）
        ClientSession clientSession2 = SocketD.createClient("sd:tcp://127.0.0.1:8602/admin?u=a&p=2").openOrThow();
        clientSession2.send("/demo", new StringEntity("Hi"));
    }
}
