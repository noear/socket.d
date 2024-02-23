package demo.demo03;


import org.noear.socketd.SocketD;

public class Demo03_Config {
    public static void main(String[] args) throws Throwable {
        //::启动服务端
        SocketD.createServer("sd:ws")
                .config(c -> c.port(8602))
                .config(sc->sc.exchangeThreads(2).sslContext(null))
                .start();

        Thread.sleep(1000); //等会儿，确保服务端启动完成

        //::打开客户端会话
        SocketD.createClient("sd:ws://127.0.0.1:8602/?u=a&p=2")
                .config(cc->cc.sslContext(null))
                .openOrThow();
    }
}
