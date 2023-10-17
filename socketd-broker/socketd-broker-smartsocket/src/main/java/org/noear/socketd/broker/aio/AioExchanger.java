package org.noear.socketd.broker.aio;

import org.noear.socketd.protocol.Exchanger;
import org.noear.socketd.protocol.Frame;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;

/**
 * @author noear
 * @since 2.0
 */
public class AioExchanger implements Exchanger<AioSession> {
    @Override
    public void write(AioSession source, Frame frame) throws IOException {

    }

    @Override
    public Frame read(AioSession source) throws IOException {
        return null;
    }
}
