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
    protected static final Logger log = LoggerFactory.getLogger(BrokerListener.class);

    @Override
    public void onOpen(Session session) throws IOException {
        String name = session.at();

        if (Utils.isNotEmpty(name)) {
            //注册服务
            addService(name, session);
        }
    }

    @Override
    public void onClose(Session session) {
        String name = session.at();

        if (Utils.isNotEmpty(name)) {
            //注销服务
            removeService(name, session);
        }
    }

    @Override
    public void onMessage(Session requester, Message message) throws IOException {
        String atName = message.at();

        if (atName == null) {
            requester.sendAlarm(message, "Broker service require '@' meta");
            return;
        }

        if (atName.endsWith("*")) {
            //广播模式
            atName = atName.substring(0, atName.length() - 1);

            Collection<Session> serviceAll = getServiceAll(atName);
            if (serviceAll != null && serviceAll.size() > 0) {
                for (Session service : new ArrayList<>(serviceAll)) {
                    if (service != requester) {
                        //如果不是自己，则发
                        onMessageOne(service, requester, message);
                    }
                }
            } else {
                requester.sendAlarm(message, "Broker don't have '@" + atName + "' session");
            }
        } else {
            //单发模式
            Session service = getServiceOne(atName);
            if (service != null) {
                onMessageOne(service, requester, message);
            } else {
                requester.sendAlarm(message, "Broker don't have '@" + atName + "' session");
            }
        }
    }

    private void onMessageOne(Session service, Session requester, Message message) throws IOException {
        if (message.isRequest()) {
            service.sendAndRequest(message.event(), message, reply -> {
                requester.reply(message, reply);
            });
        } else if (message.isSubscribe()) {
            service.sendAndSubscribe(message.event(), message, reply -> {
                if (reply instanceof EndEntity) {
                    requester.replyEnd(message, reply);
                } else {
                    requester.reply(message, reply);
                }
            });
        } else {
            service.send(message.event(), message);
        }
    }

    @Override
    public void onError(Session session, Throwable error) {
        if (log.isWarnEnabled()) {
            log.warn("Broker error", error);
        }
    }
}