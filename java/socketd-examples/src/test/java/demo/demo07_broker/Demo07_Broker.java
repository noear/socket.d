package demo.demo07_broker;

import org.noear.socketd.SocketD;
import org.noear.socketd.broker.BrokerFragmentHandler;
import org.noear.socketd.broker.BrokerListener;

import java.io.IOException;

public class Demo07_Broker {
    public static void main(String[] args) throws IOException {
        SocketD.createServer("sd:tcp")
                .config(c -> c.port(8602).fragmentHandler(new BrokerFragmentHandler()))
                .listen(new BrokerListener())
                .start();
    }
}
