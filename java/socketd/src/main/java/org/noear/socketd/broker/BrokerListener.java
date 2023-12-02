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
 * 经纪人监听器（为不同的玩家转发消息）
 *
 * @author noear
 * @since 2.1
 */
public class BrokerListener extends BrokerListenerBase implements Listener {
    protected static final Logger log = LoggerFactory.getLogger(BrokerListener.class);

    @Override
    public void onOpen(Session session) throws IOException {
        String name = session.name();

        if (Utils.isNotEmpty(name)) {
            //注册玩家会话
            addPlayer(name, session);
        } else {
            //否则算游客（别人只能被动回它消息）
        }
    }

    @Override
    public void onClose(Session session) {
        String name = session.name();

        if (Utils.isNotEmpty(name)) {
            //注销玩家会话
            removePlayer(name, session);
        } else {
            //否则算游客（别人只能被动回它消息）
        }
    }

    @Override
    public void onMessage(Session requester, Message message) throws IOException {
        String atName = message.at();

        if (atName == null) {
            requester.sendAlarm(message, "Broker message require '@' meta");
            return;
        }

        if (atName.endsWith("*")) {
            //群发模式（给所有同名的玩家都发）
            atName = atName.substring(0, atName.length() - 1);

            Collection<Session> playerAll = getPlayerAll(atName);
            if (playerAll != null && playerAll.size() > 0) {
                for (Session responder : new ArrayList<>(playerAll)) {
                    if (responder != requester) {
                        //转发消息（过滤自己）
                        forwardDo(requester, message, responder);
                    }
                }
            } else {
                requester.sendAlarm(message, "Broker don't have '@" + atName + "' player");
            }
        } else {
            //单发模式（轮询负截均衡，给同名的某个玩家发）
            Session responder = getPlayerOne(atName);
            if (responder != null) {
                //转发消息
                forwardDo(requester, message, responder);
            } else {
                requester.sendAlarm(message, "Broker don't have '@" + atName + "' session");
            }
        }
    }

    /**
     * 转发消息
     *
     * @param requester 请求玩家
     * @param message   消息
     * @param responder 响应玩家
     */
    private void forwardDo(Session requester, Message message, Session responder) throws IOException {
        if (message.isRequest()) {
            responder.sendAndRequest(message.event(), message, reply -> {
                requester.reply(message, reply);
            });
        } else if (message.isSubscribe()) {
            responder.sendAndSubscribe(message.event(), message, reply -> {
                if (reply instanceof EndEntity) {
                    requester.replyEnd(message, reply);
                } else {
                    requester.reply(message, reply);
                }
            });
        } else {
            responder.send(message.event(), message);
        }
    }

    @Override
    public void onError(Session session, Throwable error) {
        if (log.isWarnEnabled()) {
            log.warn("Broker error", error);
        }
    }
}