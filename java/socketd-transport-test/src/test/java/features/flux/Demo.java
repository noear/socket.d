package features.flux;

import org.noear.socketd.transport.core.Entity;
import reactor.core.publisher.Mono;

/**
 * @author noear 2024/1/17 created
 */
public class Demo {
    SocketdRx socketdRx;

    public Mono<String> hello(String name) {
        return socketdRx.sendAndRequest("/hello", Entity.of().metaPut("name", name))
                .map(r -> r.dataAsString());
    }
}
