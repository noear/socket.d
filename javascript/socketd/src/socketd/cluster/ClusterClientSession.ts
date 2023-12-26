import {ClientSession} from "../transport/client/ClientSession";
import { Entity, Reply } from "../transport/core/Message";
import { Stream } from "../transport/core/Stream";
import { IoConsumer } from "../transport/core/Types";
import {Utils} from "../utils/Utils";
import {SocketdException} from "../exception/SocketdException";
import {ClientChannel} from "../transport/client/ClientChannel";
import {RunUtils} from "../utils/RunUtils";

/**
 * 集群客户端会话
 *
 * @author noear
 * @since 2.1
 */
export class ClusterClientSession implements ClientSession {
    //会话集合
    _sessionSet: ClientSession[];
    //轮询计数
    _sessionRoundCounter: number;
    //会话id
    _sessionId: string;

    constructor(sessions: ClientSession[]) {
        this._sessionSet = sessions;
        this._sessionId = Utils.guid();
        this._sessionRoundCounter = 0;
    }

    /**
     * 获取所有会话
     */
    getSessionAll(): ClientSession[] {
        return this._sessionSet;
    }

    /**
     * 获取一个会话（轮询负栽均衡）
     */
    getSessionOne(): ClientSession {
        if (this._sessionSet.length == 0) {
            //没有会话
            throw new SocketdException("No session!");
        } else if (this._sessionSet.length == 1) {
            //只有一个就不管了
            return this._sessionSet[0];
        } else {
            //查找可用的会话
            let sessions = new ClientChannel[this._sessionSet.length];
            let sessionsSize = 0;
            for (let s of this._sessionSet) {
                if (s.isValid()) {
                    sessions[sessionsSize] = s;
                    sessionsSize++;
                }
            }

            if (sessionsSize == 0) {
                //没有可用的会话
                throw new SocketdException("No session is available!");
            }

            if (sessionsSize == 1) {
                return sessions[0];
            }

            //论询处理
            let counter = this._sessionRoundCounter++;
            let idx = counter % sessionsSize;
            if (counter > 999_999_999) {
                this._sessionRoundCounter = 0;
            }
            return sessions[idx];
        }
    }

    isValid(): boolean {
        for (let session of this._sessionSet) {
            if (session.isValid()) {
                return true;
            }
        }

        return false;
    }

    sessionId(): string {
        return this._sessionId;
    }

    reconnect() {
        for (let session of this._sessionSet) {
            if (session.isValid() == false) {
                session.reconnect();
            }
        }
    }

    /**
     * 发送
     *
     * @param event   事件
     * @param content 内容
     */
    send(event: string, content: Entity) {
        let sender = this.getSessionOne();

        sender.send(event, content);
    }


    /**
     * 发送并请求（限为一次答复；指定回调）
     *
     * @param event    事件
     * @param content  内容
     * @param consumer 回调消费者
     * @param timeout  超时
     */
    sendAndRequest(event: string, content: Entity, consumer: IoConsumer<Reply>, timeout: number): Stream {
        let sender = this.getSessionOne();

        return sender.sendAndRequest(event, content, consumer, timeout);
    }

    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event    事件
     * @param content  内容
     * @param consumer 回调消费者
     * @param timeout  超时
     */
    sendAndSubscribe(event: string, content: Entity, consumer: IoConsumer<Reply>, timeout: number): Stream {
        let sender = this.getSessionOne();

        return sender.sendAndSubscribe(event, content, consumer, timeout);
    }

    /**
     * 关闭
     */
    close() {
        for (let session of this._sessionSet) {
            //某个关闭出错，不影响别的关闭
            RunUtils.runAndTry(session.close);
        }
    }
}