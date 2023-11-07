package demo.demo04;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.BuilderListener;
import org.noear.socketd.transport.core.listener.PipelineListener;
import org.noear.socketd.transport.core.listener.SimpleListener;
import org.noear.socketd.transport.server.ServerConfig;

import java.io.IOException;

public class Demo04_Pipline {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer(new ServerConfig("sd:udp").port(8602))
                .listen(new PipelineListener().next(new BuilderListener().onMessage((s, m) -> {
                    //这里可以做拦截
                    System.out.println("拦截打印::" + m);
                })).next(new BuilderListener().onMessage((s, m) -> {
                    //这里可以做业务处理
                    System.out.println(m);
                })))
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成

        //::打开客户端会话
        Session session = SocketD.createClient("sd:udp://127.0.0.1:8602/hello?u=a&p=2")
                .open();

        session.send("/demo", new StringEntity("Hi"));
    }
}
