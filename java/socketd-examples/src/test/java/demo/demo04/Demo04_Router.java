package demo.demo04;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.RouterListener;
import org.noear.socketd.transport.server.ServerConfig;

public class Demo04_Router {
    public static void main(String[] args) throws Throwable {
        //::启动服务端

        RouterListener router = new RouterListener();

        //用户频道
        router.of("/").onMessage((s,m)->{
            System.out.println("user::"+m);
        });

        //管理员频道
        router.of("/admin").onOpen(s->{
            if("admin".equals(s.getHandshake().getParam("u")) == false){
                s.close();
            }
        }).onMessage((s,m)->{
            System.out.println("admin::"+m);
        });


        SocketD.createServer(new ServerConfig("tcp").port(8602).coreThreads(20))
                .listen(router)
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成

        //::打开客户端会话

        //用户频道
        Session session1 = SocketD.createClient("tcp://127.0.0.1:8602/?u=a&p=2").open();
        session1.send("/demo", new StringEntity("Hi"));

        //管理员频道
        Session session2 = SocketD.createClient("tcp://127.0.0.1:8602/admin?u=a&p=2").open();
        session2.send("/demo", new StringEntity("Hi"));
    }
}
