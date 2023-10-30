package org.noear.socketd.broker.smartsocket;

import org.noear.socketd.protocol.Channel;
import org.noear.socketd.protocol.Frame;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerBase;
import org.noear.socketd.server.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioQuickServer;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;

/**
 * Aio 服务端实现
 *
 * @author noear
 * @since 2.0
 */
public class TcpAioServer extends ServerBase implements Server, MessageProcessor<Frame> {
    private static final Logger log = LoggerFactory.getLogger(TcpAioServer.class);

    private AioQuickServer server;
    private ServerConfig serverConfig;
    private TcpAioExchanger exchanger;

    public TcpAioServer(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        this.exchanger = new TcpAioExchanger();
    }

    @Override
    public void start() throws IOException {
        if (serverConfig.getHost() != null) {
            server = new AioQuickServer(serverConfig.getPort(),
                    exchanger, this);
        } else {
            server = new AioQuickServer(serverConfig.getHost(), serverConfig.getPort(),
                    exchanger, this);
        }

        server.setThreadNum(serverConfig.getCoreThreads());
        server.setBannerEnabled(false);
        if (serverConfig.getReadBufferSize() > 0) {
            server.setReadBufferSize(serverConfig.getReadBufferSize());
        }
        if (serverConfig.getWriteBufferSize() > 0) {
            server.setWriteBuffer(serverConfig.getWriteBufferSize(), 16);
        }
        server.start();

        log.info("Server started: {server=tcp://127.0.0.1:" + serverConfig.getPort() + "}");
    }

    @Override
    public void stop() throws IOException {
        server.shutdown();
    }


    @Override
    public void process(AioSession s, Frame frame) {
        Channel channel = Attachment.getChannel(s, exchanger);

        try {
            processor.onReceive(channel, frame);
        } catch (Throwable e) {
            if (channel == null) {
                log.warn(e.getMessage(), e);
            } else {
                processor.onError(channel.getSession(), e);
            }
        }
    }

    @Override
    public void stateEvent(AioSession s, StateMachineEnum state, Throwable e) {
        switch (state) {
            case NEW_SESSION:
                //processor.onOpen(AioAttachment.getChannel(s, exchanger).getSession());
                break;

            case SESSION_CLOSED:
                processor.onClose(Attachment.getChannel(s, exchanger).getSession());
                break;

            case PROCESS_EXCEPTION:
            case DECODE_EXCEPTION:
            case INPUT_EXCEPTION:
            case ACCEPT_EXCEPTION:
            case OUTPUT_EXCEPTION:
                processor.onError(Attachment.getChannel(s, exchanger).getSession(), e);
                break;
        }
    }
}