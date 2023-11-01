package labs.demo82_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Header;
import org.noear.solon.annotation.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author noear 2023/11/1 created
 */
@Controller
public class DemoController {
    static final Logger log = LoggerFactory.getLogger(DemoController.class);
    @Mapping("/demo")
    public String demo(@Header String user, Long order){
        log.info("user={}, order={}", user, order);
        return user;
    }
}
