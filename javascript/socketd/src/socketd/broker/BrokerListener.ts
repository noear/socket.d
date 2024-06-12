import {BrokerListenerBase} from "./BrokerListenerBase";
import {Listener} from "../transport/core/Listener";
import {Session} from "../transport/core/Session";
import {Message, MessageBuilder} from "../transport/core/Message";
import {RunUtils} from "../utils/RunUtils";
import {BroadcastBroker} from "./BroadcastBroker";
import {Entity} from "../transport/core/Entity";
import {Flags} from "../transport/core/Flags";
import {SocketDException} from "../exception/SocketDException";
import {SessionUtils} from "../utils/SessionUtils";

export class BrokerListener extends BrokerListenerBase implements Listener, BroadcastBroker {
    onOpen(session: Session) {
        let name = session.name();
        this.addPlayer(name, session);
    }


    onClose(session: Session) {
        let name = session.name();
        this.removePlayer(name, session);
    }

    onMessage(requester: Session|null, message: Message) {
        this.onMessageDo(requester, message);
    }

    onReply(session: Session, message: Message) {

    }

    onSend(session: Session, message: Message) {

    }

    onMessageDo(requester: Session|null, message: Message){
        let atName = message.atName();

        if (!atName) {
            if(requester!=null) {
                requester.sendAlarm(message, "Broker message require '@' meta");
            }else{
                throw new SocketDException("Broker message require '@' meta");
            }
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
                if (requester != null) {
                    requester.sendAlarm(message, "Broker don't have '@" + atName + "' player");
                } else {
                    throw new SocketDException("Broker don't have '@" + atName + "' player");
                }
            }
        } else {
            //单发模式（给同名的某个玩家，轮询负截均衡）
            let responder = this.getPlayerAny(atName, requester, message);

            if (responder != null) {
                //转发消息
                this.forwardToSession(requester, message, responder);
            } else {
                if (requester != null) {
                    requester.sendAlarm(message, "Broker don't have '@" + atName + "' session");
                } else {
                    throw new SocketDException("Broker don't have '@" + atName + "' session");
                }
            }
        }
    }

    /**
     * 广播
     *
     * @param event  事件
     * @param entity 实体（转发方式 https://socketd.noear.org/article/737 ）
     */
    broadcast(event: string, entity: Entity) {
        this.onMessageDo(null, new MessageBuilder()
            .flag(Flags.Message)
            .event(event)
            .entity(entity).build());
    }

    /**
     * 批量转发消息
     *
     * @param requester 请求玩家
     * @param message   消息
     * @param name      目标玩家名字
     */
    forwardToName(requester: Session|null, message: Message, name: string | null): boolean {
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
    forwardToSession(requester: Session|null, message: Message, responder: Session) {
        if (message.isRequest()) {
            responder.sendAndRequest(message.event(), message, -1).thenReply(reply => {
                if (SessionUtils.isValid(requester)) {
                    requester!.reply(message, reply);
                }
            }).thenError(err => {
                //传递异常
                if (SessionUtils.isValid(requester)) {
                    RunUtils.runAndTry(() => requester!.sendAlarm(message, err.message));
                }
            });
        } else if (message.isSubscribe()) {
            responder.sendAndSubscribe(message.event(), message).thenReply(reply => {
                if (SessionUtils.isValid(requester)) {
                    if (reply.isEnd()) {
                        requester!.replyEnd(message, reply);
                    } else {
                        requester!.reply(message, reply);
                    }
                }
            }).thenError(err => {
                //传递异常
                if (SessionUtils.isValid(requester)) {
                    RunUtils.runAndTry(() => requester!.sendAlarm(message, err.message));
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