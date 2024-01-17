package features.flux;

import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Reply;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * @author noear 2024/1/17 created
 */
public class SocketdRx {
    private final ClientSession clientSession;

    public SocketdRx(ClientSession clientSession) {
        this.clientSession = clientSession;
    }

    public Mono<Void> send(String event, Entity entity) {
        return Mono.create(sink -> {
            try {
                clientSession.send(event, entity);
                sink.success();
            } catch (Throwable e) {
                sink.error(e);
            }
        });
    }

    public Mono<Reply> sendAndRequest(String event, Entity entity) {
        return Mono.create(sink -> {
            try {
                clientSession.sendAndRequest(event, entity).thenReply(reply -> {
                    sink.success(reply);
                }).thenError(e -> {
                    sink.error(e);
                });
            } catch (Throwable e) {
                sink.error(e);
            }
        });
    }

    public Flux<Reply> sendAndSubscribe(String event, Entity entity) {
        return Flux.create(sink -> {
            try {
                clientSession.sendAndSubscribe(event, entity).thenReply(reply -> {
                    sink.next(reply);

                    if (reply.isEnd()) {
                        sink.complete();
                    }
                }).thenError(e -> {
                    sink.error(e);
                });
            } catch (Throwable e) {
                sink.error(e);
            }
        });
    }
}
