package labs;

import org.noear.socketd.exception.SocketDSizeLimitException;
import org.noear.socketd.transport.core.Constants;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.codec.ByteBufferCodecReader;
import org.noear.socketd.transport.core.codec.CodecDefault;
import org.noear.socketd.transport.core.impl.ConfigDefault;
import org.noear.socketd.transport.core.impl.Frames;
import org.noear.socketd.transport.smartsocket.tcp.TcpAioBufferWriter;
import org.noear.socketd.transport.smartsocket.tcp.impl.ChannelDefaultEx;
import org.smartboot.socket.Protocol;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.extension.decoder.FixedLengthFrameDecoder;
import org.smartboot.socket.extension.plugins.SslPlugin;
import org.smartboot.socket.extension.processor.AbstractMessageProcessor;
import org.smartboot.socket.extension.ssl.factory.ClientSSLContextFactory;
import org.smartboot.socket.extension.ssl.factory.ServerSSLContextFactory;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioQuickServer;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;

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
        SslPlugin<Frame> sslServerPlugin = new SslPlugin<>(serverFactory); //ClientAuth.REQUIRE
        serverProcessor.addPlugin(sslServerPlugin);
        sslQuickServer.start();

        IntegerClientProcessor clientProcessor = new IntegerClientProcessor();
        AioQuickClient sslQuickClient = new AioQuickClient("localhost", 8080, new IntegerProtocol(), clientProcessor);
        ClientSSLContextFactory clientFactory = getClientSSLContext();
        SslPlugin<Frame> sslPlugin = new SslPlugin<>(clientFactory);
        clientProcessor.addPlugin(sslPlugin);
        AioSession aioSession = sslQuickClient.start();
        while (true) {
            Frame frame = Frames.connectFrame("xxx", "yyy", Collections.singletonMap("a", "1"));
            codecDefault.write(frame, (i) -> new TcpAioBufferWriter(aioSession.writeBuffer()));
        }
    }

    public static class IntegerClientProcessor extends AbstractMessageProcessor<Frame> {

        @Override
        public void process0(AioSession session, Frame msg) {
            System.out.println("receive data from server：" + msg);
        }

        @Override
        public void stateEvent0(AioSession session, StateMachineEnum stateMachineEnum, Throwable throwable) {
            System.out.println("other state:" + stateMachineEnum);
            if (stateMachineEnum == StateMachineEnum.INPUT_EXCEPTION) {
                throwable.printStackTrace();
            }
            if (stateMachineEnum == StateMachineEnum.OUTPUT_EXCEPTION) {
                throwable.printStackTrace();
            }
        }
    }

    private static CodecDefault codecDefault = new CodecDefault(new ConfigDefault(false));

    public static class IntegerServerProcessor extends AbstractMessageProcessor<Frame> {

        @Override
        public void process0(AioSession session, Frame msg) {
            try {
                codecDefault.write(msg, (i) -> new TcpAioBufferWriter(session.writeBuffer()));
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

    public static class IntegerProtocol implements Protocol<Frame> {

        @Override
        public Frame decode(ByteBuffer buffer, AioSession session) {
            FixedLengthFrameDecoder decoder = session.getAttachment();

            if (decoder == null) {
                if (buffer.remaining() < Integer.BYTES) {
                    return null;
                } else {
                    buffer.mark();
                    int frameLength = buffer.getInt();

                    if (frameLength > Constants.MAX_SIZE_FRAME) {
                        //超过限制大小
                        throw new SocketDSizeLimitException("Adjusted frame length exceeds " + Constants.MAX_SIZE_FRAME + ": " + frameLength + " - discarded");
                    }

                    decoder = new FixedLengthFrameDecoder(frameLength);
                    buffer.reset();
                    session.setAttachment(decoder);
                }
            }

            if (decoder.decode(buffer) == false) {
                return null;
            } else {
                session.setAttachment(null);
                buffer = decoder.getBuffer();
            }

            return codecDefault.read(new ByteBufferCodecReader(buffer));
        }
    }
}