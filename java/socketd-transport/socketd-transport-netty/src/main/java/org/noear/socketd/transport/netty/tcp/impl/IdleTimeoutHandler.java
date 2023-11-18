package org.noear.socketd.transport.netty.tcp.impl;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 连接空闲超时处理
 *
 * @author noear
 * @since 2.0
 */
public class IdleTimeoutHandler extends ChannelDuplexHandler {
    private static final Logger log = LoggerFactory.getLogger(IdleTimeoutHandler.class);
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //支持 idleTimeout
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                if(log.isDebugEnabled()) {
                    log.debug("Channel idle timeout");
                }

                ctx.close();
            }
        }
    }
}
