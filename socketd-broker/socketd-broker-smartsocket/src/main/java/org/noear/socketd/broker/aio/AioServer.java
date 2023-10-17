package org.noear.socketd.broker.aio;

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
 * @author noear
 * @since 2.0
 */
public class AioServer extends ServerBase implements Server, MessageProcessor<Frame> {
    private static final Logger log = LoggerFactory.getLogger(AioServer.class);

    private AioQuickServer server;
    private ServerConfig serverConfig;
    private AioExchanger exchanger;

    public AioServer(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        this.exchanger = new AioExchanger();
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
    }

    @Override
    public void stop() throws IOException {
        server.shutdown();
    }


    @Override
    public void process(AioSession aioSession, Frame frame) {
        Channel channel = AioAttachment.getChannel(aioSession, exchanger);
        try {
            processor.onReceive(AioAttachment.getChannel(aioSession, exchanger), frame);
        } catch (Throwable e) {
            if (channel == null) {
                log.warn(e.getMessage(), e);
            } else {
                processor.onError(channel.getSession(), e);
            }
        }
    }

    @Override
    public void stateEvent(AioSession aioSession, StateMachineEnum state, Throwable e) {
        switch (state) {
            case NEW_SESSION:
                processor.onOpen(AioAttachment.getChannel(aioSession, exchanger).getSession());
                break;

            case SESSION_CLOSED:
                processor.onClose(AioAttachment.getChannel(aioSession, exchanger).getSession());
                break;

            case PROCESS_EXCEPTION:
            case DECODE_EXCEPTION:
            case INPUT_EXCEPTION:
            case ACCEPT_EXCEPTION:
            case OUTPUT_EXCEPTION:
                processor.onError(AioAttachment.getChannel(aioSession, exchanger).getSession(), e);
                break;
        }
    }
}