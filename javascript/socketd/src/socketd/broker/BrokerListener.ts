import {BrokerListenerBase} from "./BrokerListenerBase";
import {Listener} from "../transport/core/Listener";
import {Session} from "../transport/core/Session";
import {Message} from "../transport/core/Message";
import {RunUtils} from "../utils/RunUtils";

export class BrokerListener extends BrokerListenerBase implements Listener {
    onOpen(session: Session) {
        let name = session.name();
        this.addPlayer(name, session);
    }


    onClose(session: Session) {
        let name = session.name();
        this.removePlayer(name, session);
    }

    onMessage(requester: Session, message: Message) {
        let atName = message.atName();

        if (!atName) {
            requester.sendAlarm(message, "Broker message require '@' meta");
            return;
        }

        if (atName == "*") {
            //广播模式（给所有玩家）
            let nameAll = this.getNameAll();
            if (nameAll != null && nameAll.size > 0) {
                for (let name of nameAll) {
                    this.forwardToName(requester, message, name);
                }
            }
        } else if (atName.endsWith("*")) {
            //群发模式（给同名的所有玩家）
            atName = atName.substring(0, atName.length() - 1);

            if (this.forwardToName(requester, message, atName) == false) {
                requester.sendAlarm(message, "Broker don't have '@" + atName + "' player");
            }
        } else {
            //单发模式（给同名的某个玩家，轮询负截均衡）
            let responder = this.getPlayerAny(atName, requester, message);

            if (responder != null) {
                //转发消息
                this.forwardToSession(requester, message, responder);
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
    forwardToName(requester: Session, message: Message, name: string | null): boolean {
        let playerAll = this.getPlayerAll(name);
        if (playerAll != null && playerAll.size > 0) {
            for (let responder of playerAll) {
                //转发消息（过滤自己）
                if (responder != requester) {
                    if (responder.isValid()) {
                        this.forwardToSession(requester, message, responder);
                    } else {
                        //如果无效，做关闭处理
                        this.onClose(responder);
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
    forwardToSession(requester: Session, message: Message, responder: Session) {
        if (message.isRequest()) {
            responder.sendAndRequest(message.event(), message, -1).thenReply(reply => {
                if (requester.isValid()) {
                    requester.reply(message, reply);
                }
            }).thenError(err => {
                //传递异常
                if (requester.isValid()) {
                    RunUtils.runAndTry(() => requester.sendAlarm(message, err.message));
                }
            });
        } else if (message.isSubscribe()) {
            responder.sendAndSubscribe(message.event(), message).thenReply(reply => {
                if (requester.isValid()) {
                    if (reply.isEnd()) {
                        requester.replyEnd(message, reply);
                    } else {
                        requester.reply(message, reply);
                    }
                }
            }).thenError(err => {
                //传递异常
                if (requester.isValid()) {
                    RunUtils.runAndTry(() => requester.sendAlarm(message, err.message));
                }
            });
        } else {
            responder.send(message.event(), message);
        }
    }

    onError(session: Session, error: Error) {
        console.warn("Broker error", error);
    }
}