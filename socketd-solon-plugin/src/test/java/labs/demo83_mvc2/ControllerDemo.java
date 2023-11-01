package labs.demo83_mvc2;

import org.noear.socketd.core.Message;
import org.noear.socketd.core.Session;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Header;
import org.noear.solon.annotation.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class ControllerDemo {
    static final Logger log = LoggerFactory.getLogger(ControllerDemo.class);

    @Mapping("/demo")
    public String demo(@Header String user, Long order) {
        log.info("user={}, order={}", user, order);
        return user;
    }

    //仍可以注入：会话与消息
    @Mapping("/demo2")
    public String demo2(@Header String user, Long order, Session session, Message message) {
        log.info("sessonId={}, message={}", session.getSessionId(), message);
        log.info("user={}, order={}", user, order);
        return user;
    }
}
