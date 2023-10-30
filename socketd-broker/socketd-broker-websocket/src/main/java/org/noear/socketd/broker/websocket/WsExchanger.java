package org.noear.socketd.broker.websocket;

import org.noear.socketd.protocol.Frame;
import org.noear.socketd.protocol.OutputTarget;

import java.io.IOException;

/**
 * @author noear
 * @since 2.0
 */
public class WsExchanger implements OutputTarget {
    @Override
    public void write(Object source, Frame frame) throws IOException {

    }
}
