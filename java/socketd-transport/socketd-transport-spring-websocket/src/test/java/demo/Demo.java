package demo;

import org.noear.socketd.transport.core.impl.ConfigDefault;
import org.noear.socketd.transport.core.listener.EventListener;
import org.noear.socketd.transport.spring.websocket.ToSocketdWebSocketListener;

public class Demo extends ToSocketdWebSocketListener {
    public Demo() {
        super(new ConfigDefault(false));

        setListener(new EventListener().doOnOpen(s -> {

        }).doOn("/demo", (s, m) -> {

        }));
    }
}
