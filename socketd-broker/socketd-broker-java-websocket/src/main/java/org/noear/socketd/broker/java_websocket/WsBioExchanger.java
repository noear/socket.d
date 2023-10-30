package org.noear.socketd.broker.java_websocket;

import org.java_websocket.WebSocket;
import org.noear.socketd.protocol.CodecByteBuffer;
import org.noear.socketd.protocol.Frame;
import org.noear.socketd.protocol.OutputTarget;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Ws-Bio 交换机实现
 *
 * @author noear
 * @since 2.0
 */
public class WsBioExchanger implements OutputTarget<WebSocket> {
    private CodecByteBuffer codec = new CodecByteBuffer();

    @Override
    public void write(WebSocket source, Frame frame) throws IOException {
        source.send(codec.encode(frame));
    }

    public Frame read(ByteBuffer buffer){
        return codec.decode(buffer);
    }
}
