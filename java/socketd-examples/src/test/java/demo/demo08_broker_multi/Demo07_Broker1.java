package demo.demo08_broker_multi;

import org.noear.socketd.SocketD;
import org.noear.socketd.broker.BrokerFragmentHandler;
import org.noear.socketd.broker.BrokerListener;

import java.io.IOException;

public class Demo07_Broker1 {
    //
    // 整个体系，就像是所有参与者在一个社交群聊天。给谁发就 at 谁
    //
    public static void main(String[] args) throws IOException {
        //主要是两处不同：1，专属分片处理；2，专属监听器
        SocketD.createServer("sd:tcp")
                .config(c -> c.port(8601).fragmentHandler(new BrokerFragmentHandler()))
                .listen(new BrokerListener())
                .start();

        //可以创建多个协议
//        SocketD.createServer("sd:udp")
//                .config(c -> c.port(8603).fragmentHandler(new BrokerFragmentHandler()))
//                .listen(new BrokerListener())
//                .start();
    }
}
