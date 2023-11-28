package org.noear.socketd.broker;

import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.EndEntity;
import org.noear.socketd.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 经纪人监听器
 *
 * @author noear
 * @since 2.1
 */
public class BrokerListener extends BrokerListenerBase implements Listener {
    private static final Logger log = LoggerFactory.getLogger(BrokerListener.class);

    @Override
    public void onOpen(Session session) throws IOException {
        String name = session.at();

        if (Utils.isNotEmpty(name)) {
            addServer(name, session);
        }
    }

    @Override
    public void onClose(Session session) {
        String name = session.at();

        if (Utils.isNotEmpty(name)) {
            removeServer(name, session);
        }
    }

    @Override
    public void onMessage(Session client, Message message) throws IOException {
        String atName = message.at();

        if (atName == null) {
            client.sendAlarm(message, "Broker service require '@' meta");
            return;
        }

        if (atName.endsWith("*")) {
            //广播模式
            atName = atName.substring(0, atName.length() - 1);

            Collection<Session> serverAll = getServerAll(atName);
            if (serverAll != null && serverAll.size() > 0) {
                for (Session server : new ArrayList<>(serverAll)) {
                    if (server != client) {
                        //如果不是自己，则发
                        onMessageOne(server, client, message);
                    }
                }
            } else {
                client.sendAlarm(message, "Broker don't have '@" + atName + "' session");
            }
        } else {
            //单发模式
            Session server = getServerOne(atName);
            if (server != null) {
                onMessageOne(server, client, message);
            } else {
                client.sendAlarm(message, "Broker don't have '@" + atName + "' session");
            }
        }
    }

    private void onMessageOne(Session server, Session client, Message message) throws IOException {
        if (message.isRequest()) {
            server.sendAndRequest(message.event(), message, reply -> {
                client.reply(message, reply);
            });
        } else if (message.isSubscribe()) {
            server.sendAndSubscribe(message.event(), message, reply -> {
                if (reply instanceof EndEntity) {
                    client.replyEnd(message, reply);
                } else {
                    client.reply(message, reply);
                }
            });
        } else {
            server.send(message.event(), message);
        }
    }

    @Override
    public void onError(Session session, Throwable error) {
        if (log.isWarnEnabled()) {
            log.warn("Broker error", error);
        }
    }
}