package benchmark;

import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.event.AppBeanLoadEndEvent;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import reactor.core.publisher.Mono;

/**
 * 这个测试主要测会不会被压死！？
 *
 * @author noear
 * @since 2.4
 */
@ExtendWith(SolonJUnit5Extension.class)
@SolonTest
public class WrkTest {
    public static void main(String[] args) {
        Solon.start(WrkTest.class, args, app -> {
            app.onEvent(AppBeanLoadEndEvent.class, e -> {
                app.add("/", WrkTest.class);
            });
        });
    }

    private final ClientSession clientSession;

    public WrkTest() {
        String s = WrkServer.schemas[WrkServer.schemasIdx];
        clientSession = SocketD.createClient(s + "://127.0.0.1:8602").open();
    }

    @Mapping("/rx")
    public Mono<String> rx(String name) throws Exception {
        return Mono.create(sink -> {
            try {
                Entity entity = new StringEntity("hello")
                        .metaPut("name", name == null ? "noear" : name);

                clientSession.sendAndRequest("hello", entity).thenReply(reply -> {
                    sink.success(reply.dataAsString());
                }).thenError(e -> {
                    sink.error(e);
                });
            } catch (Throwable e) {
                sink.error(e);
            }
        });
    }

}
