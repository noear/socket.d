package labs;

import io.github.smartboot.socket.AbstractMessageProcessor;
import io.github.smartboot.socket.Protocol;
import io.github.smartboot.socket.StateMachineEnum;
import io.github.smartboot.socket.extension.plugins.SslPlugin;
import io.github.smartboot.socket.extension.ssl.factory.ClientSSLContextFactory;
import io.github.smartboot.socket.extension.ssl.factory.ServerSSLContextFactory;
import io.github.smartboot.socket.transport.AioQuickClient;
import io.github.smartboot.socket.transport.AioQuickServer;
import io.github.smartboot.socket.transport.AioSession;
import org.noear.solon.core.util.RunUtil;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 * @author noear 2025/11/2 created
 *
 */
public class SmartsocketSslTest {
    public static ClientSSLContextFactory getClientSSLContext() throws Exception {
        //return new ClientSSLContextFactory().create();
        return new ClientSSLContextFactory(
                ClassLoader.getSystemResourceAsStream("ssl/jks/trustKeystore.jks"),
                "123456",
                ClassLoader.getSystemResourceAsStream("ssl/jks/keystore.jks"),
                "123456"
        );
    }

    public static ServerSSLContextFactory getServerSSLContext() throws Exception {
        //return new AutoServerSSLContextFactory().create();
        return new ServerSSLContextFactory(
                ClassLoader.getSystemResourceAsStream("ssl/jks/keystore.jks"),
                "123456",
                "123456",
                ClassLoader.getSystemResourceAsStream("ssl/jks/trustKeystore.jks"),
                "123456");
    }


    public static void main(String[] args) throws Exception {
        IntegerServerProcessor serverProcessor = new IntegerServerProcessor();
        AioQuickServer sslQuickServer = new AioQuickServer(8080, new IntegerProtocol(), serverProcessor);
        ServerSSLContextFactory serverFactory = getServerSSLContext();
        SslPlugin<Integer> sslServerPlugin = new SslPlugin<>(serverFactory); //ClientAuth.REQUIRE
        serverProcessor.addPlugin(sslServerPlugin);
        sslQuickServer.start();

        IntegerClientProcessor clientProcessor = new IntegerClientProcessor();
        AioQuickClient sslQuickClient = new AioQuickClient("localhost", 8080, new IntegerProtocol(), clientProcessor);
        ClientSSLContextFactory clientFactory = getClientSSLContext();
        SslPlugin<Integer> sslPlugin = new SslPlugin<>(clientFactory);
        clientProcessor.addPlugin(sslPlugin);
        sslQuickClient.start();
    }

    public static class IntegerClientProcessor extends AbstractMessageProcessor<Integer> {

        @Override
        public void process0(AioSession session, Integer msg) {
            System.out.println("receive data from server：" + msg);
        }

        @Override
        public void stateEvent0(AioSession session, StateMachineEnum stateMachineEnum, Throwable throwable) {
            System.out.println("other state:" + stateMachineEnum);

            if (stateMachineEnum == StateMachineEnum.NEW_SESSION) {
                RunUtil.async(()->{
                    try {
                        session.writeBuffer().writeInt(1);
                        session.writeBuffer().flush();
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    }
                });

                return;
            }

            if (stateMachineEnum == StateMachineEnum.INPUT_EXCEPTION) {
                throwable.printStackTrace();
            }
            if (stateMachineEnum == StateMachineEnum.OUTPUT_EXCEPTION) {
                throwable.printStackTrace();
            }
        }
    }

    public static class IntegerServerProcessor extends AbstractMessageProcessor<Integer> {
        @Override
        public void process0(AioSession session, Integer msg) {
            Integer respMsg = msg + 1;
            System.out.println("receive data from client: " + msg + " ,rsp:" + (respMsg));
            try {
                session.writeBuffer().writeInt(respMsg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void stateEvent0(AioSession session, StateMachineEnum stateMachineEnum, Throwable throwable) {
            if (stateMachineEnum == StateMachineEnum.INPUT_EXCEPTION) {
                throwable.printStackTrace();
            }
        }
    }

    public static class IntegerProtocol implements Protocol<Integer> {

        private static final int INT_LENGTH = 4;

        @Override
        public Integer decode(ByteBuffer data, AioSession session) {
            if (data.remaining() < INT_LENGTH)
                return null;
            return data.getInt();
        }
    }
}