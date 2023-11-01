package org.noear.socketd.solon.mvc;

import org.noear.socketd.core.SimpleListener;
import org.noear.socketd.core.Message;
import org.noear.socketd.core.Session;
import org.noear.solon.Solon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author noear
 * @since 2.0
 */
public class SocketMvcListener extends SimpleListener {
    static final Logger log = LoggerFactory.getLogger(SocketMvcListener.class);

    @Override
    public void onMessage(Session session, Message message) throws IOException {
        try {
            SocketMvcContext ctx = new SocketMvcContext(session, message);

            Solon.app().tryHandle(ctx);

            if (ctx.getHandled() || ctx.status() != 404) {
                ctx.commit();
            }
        } catch (Throwable e) {
            //context 初始化时，可能会出错
            //
            log.warn(e.getMessage(), e);
        }
    }
}
