package org.noear.socketd.broker;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.utils.RunUtils;
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
        addPlayer(name, session);
    }

    @Override
    public void onClose(Session session) {
        String name = session.name();
        removePlayer(name, session);
    }

    @Override
    public void onMessage(Session requester, Message message) throws IOException {
        String atName = message.atName();

        if (atName == null) {
            requester.sendAlarm(message, "Broker message require '@' meta");
            return;
        }

        if (atName.equals("*")) {
            //广播模式（给所有玩家）
            Collection<String> nameAll = getNameAll();
            if (nameAll != null && nameAll.size() > 0) {
                for (String name : new ArrayList<>(nameAll)) {
                    forwardToName(requester, message, name);
                }
            }
        } else if (atName.endsWith("*")) {
            //群发模式（给同名的所有玩家）
            atName = atName.substring(0, atName.length() - 1);

            if (forwardToName(requester, message, atName) == false) {
                requester.sendAlarm(message, "Broker don't have '@" + atName + "' player");
            }
        } else {
            //单发模式（给同名的某个玩家，轮询负截均衡）
            Session responder = getPlayerAny(atName, requester, message);

            if (responder != null) {
                //转发消息
                forwardToSession(requester, message, responder);
            } else {
                requester.sendAlarm(message, "Broker don't have '@" + atName + "' session");
            }
        }
    }

    /**
     * 批量转发消息
     *
     * @param requester 请求玩家
     * @param message   消息
     * @param name      目标玩家名字
     */
    public boolean forwardToName(Session requester, Message message, String name) throws IOException {
        Collection<Session> playerAll = getPlayerAll(name);
        if (playerAll != null && playerAll.size() > 0) {
            for (Session responder : new ArrayList<>(playerAll)) {
                //转发消息（过滤自己）
                if (responder != requester) {
                    if (responder.isValid()) {
                        forwardToSession(requester, message, responder);
                    } else {
                        //如果无效，做关闭处理
                        onClose(responder);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * 转发消息
     *
     * @param requester 请求玩家
     * @param message   消息
     * @param responder 目标玩家会话
     */
    public void forwardToSession(Session requester, Message message, Session responder) throws IOException {
        if (message.isRequest()) {
            responder.sendAndRequest(message.event(), message, -1).thenReply(reply -> {
                if (requester.isValid()) {
                    requester.reply(message, reply);
                }
            }).thenError(err -> {
                //传递异常
                if (requester.isValid()) {
                    RunUtils.runAndTry(() -> requester.sendAlarm(message, err.getMessage()));
                }
            });
        } else if (message.isSubscribe()) {
            responder.sendAndSubscribe(message.event(), message).thenReply(reply -> {
                if (requester.isValid()) {
                    if (reply.isEnd()) {
                        requester.replyEnd(message, reply);
                    } else {
                        requester.reply(message, reply);
                    }
                }
            }).thenError(err -> {
                //传递异常
                if (requester.isValid()) {
                    RunUtils.runAndTry(() -> requester.sendAlarm(message, err.getMessage()));
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