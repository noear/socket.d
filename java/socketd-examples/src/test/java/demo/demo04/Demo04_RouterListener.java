package demo.demo04;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.BuilderListener;
import org.noear.socketd.transport.core.listener.PipelineListener;
import org.noear.socketd.transport.core.listener.RouterListener;
import org.noear.socketd.transport.core.listener.SimpleListener;
import org.noear.socketd.transport.server.ServerConfig;

import java.io.IOException;
public class Demo04_RouterListener {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer(new ServerConfig("sd:tcp").port(8602))
                .listen(new RouterListener()
                        .of("/", new BuilderListener().onMessage((s, m) -> {
                            //用户频道
                            System.out.println("user::" + m);
                        }))
                        .of("/admin", new BuilderListener().onOpen(s -> {
                            //管理员频道
                            if ("admin".equals(s.getParam("u")) == false) {
                                s.close();
                            }
                        }).onMessage((s, m) -> {
                            System.out.println("admin::" + m);
                        })))
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成

        //::打开客户端会话
        //用户频道（链接地址的 path ，算为频道）
        Session session1 = SocketD.createClient("sd:tcp://127.0.0.1:8602/?u=a&p=2").open();
        session1.send("/demo", new StringEntity("Hi"));

        //管理员频道（链接地址的 path ，算为频道）
        Session session2 = SocketD.createClient("sd:tcp://127.0.0.1:8602/admin?u=a&p=2").open();
        session2.send("/demo", new StringEntity("Hi"));
    }
}
