package org.noear.socketd.transport.java_tcp_nio.impl;

import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.Config;

import java.nio.ByteBuffer;

/**
 * @author noear
 * @since 2.4
 */
public class NioAttachment {
    public ByteBuffer buffer;
    public NioFixedLengthFrameDecoder decoder;
    public ChannelInternal channelInternal;

    public NioAttachment(Config config){
        buffer = ByteBuffer.allocate(config.getReadBufferSize());
    }
}
