package org.noear.socketd.solon.integration;

import org.noear.socketd.SocketD;
import org.noear.socketd.client.Client;
import org.noear.socketd.protocol.Listener;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerConfig;
import org.noear.socketd.solon.annotation.SocketdClient;
import org.noear.socketd.solon.annotation.SocketdServer;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.bean.LifecycleBean;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.RunUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noear
 * @since 2.0
 */
public class XPluginImpl implements Plugin {
    List<Server> serverList = new ArrayList<>();
    List<Client> clientList = new ArrayList<>();

    @Override
    public void start(AppContext context) throws Throwable {
        context.beanBuilderAdd(SocketdServer.class, (clz, bw, anno) -> {
            if (bw.raw() instanceof Listener) {
                for (String s : anno.schema()) {
                    Server server = SocketD.createServer(new ServerConfig(s));
                    server.listen(bw.raw());
                    serverList.add(server);

                    //通过事件扩展
                    EventBus.publish(server);
                }
            }
        });

        context.beanBuilderAdd(SocketdClient.class, (clz, bw, anno) -> {
            if (bw.raw() instanceof Listener) {
                Client client = SocketD.createClient(anno.url());
                client.config(options -> options
                                .autoReconnect(anno.autoReconnect())
                                .heartbeatInterval(anno.heartbeatRate() * 1000))
                        .listen(bw.raw());

                clientList.add(client);
            }
        });

        context.lifecycle(-99, new LifecycleBean() {
            @Override
            public void start() throws Throwable {
                for (Server server : serverList) {
                    server.start();
                }
            }

            @Override
            public void stop() throws Throwable {
                for (Server server : serverList) {
                    RunUtil.runAndTry(server::stop);
                }
            }
        });

        context.lifecycle(99, () -> {
            for (Client client : clientList) {
                client.open();
            }
        });
    }
}
