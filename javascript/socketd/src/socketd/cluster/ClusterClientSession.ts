import type {ClientSession} from "../transport/client/ClientSession";
import type { Entity } from "../transport/core/Entity";
import {RequestStream, SendStream, SubscribeStream} from "../transport/stream/Stream";
import {StrUtils} from "../utils/StrUtils";
import {SocketdException} from "../exception/SocketdException";
import {RunUtils} from "../utils/RunUtils";
import {IoConsumer} from "../transport/core/Typealias";

/**
 * 集群客户端会话
 *
 * @author noear
 * @since 2.1
 */
export class ClusterClientSession implements ClientSession {
    //会话集合
    private _sessionSet: Array<ClientSession>;
    //轮询计数
    private _sessionRoundCounter: number;
    //会话id
    private _sessionId: string;

    constructor(sessions: ClientSession[]) {
        this._sessionSet = sessions;
        this._sessionId = StrUtils.guid();
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
            const sessions = new Array<ClientSession>();
            for (const s of this._sessionSet) {
                if (s.isValid()) {
                    sessions.push(s);
                }
            }

            if (sessions.length == 0) {
                //没有可用的会话
                throw new SocketdException("No session is available!");
            }

            if (sessions.length == 1) {
                return sessions[0];
            }

            //论询处理
            const counter = this._sessionRoundCounter++;
            const idx = counter % sessions.length;
            if (counter > 999_999_999) {
                this._sessionRoundCounter = 0;
            }
            return sessions[idx];
        }
    }

    isValid(): boolean {
        for (const session of this._sessionSet) {
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
        for (const session of this._sessionSet) {
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
    send(event: string, content: Entity, consumer?: IoConsumer<SendStream>): SendStream {
        const sender = this.getSessionOne();

        return sender.send(event, content, consumer);
    }


    /**
     * 发送并请求（限为一次答复；指定回调）
     *
     * @param event    事件
     * @param content  内容
     * @param timeout  超时
     */
    sendAndRequest(event: string, content: Entity, timeout?: number, consumer?: IoConsumer<RequestStream>): RequestStream {
        const sender = this.getSessionOne();

        return sender.sendAndRequest(event, content, timeout, consumer);
    }

    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event    事件
     * @param content  内容
     * @param timeout  超时
     */
    sendAndSubscribe(event: string, content: Entity, timeout: number, consumer?: IoConsumer<SubscribeStream>): SubscribeStream {
        const sender = this.getSessionOne();

        return sender.sendAndSubscribe(event, content, timeout, consumer);
    }

    /**
     * 关闭
     */
    close() {
        for (const session of this._sessionSet) {
            //某个关闭出错，不影响别的关闭
            RunUtils.runAndTry(session.close);
        }
    }
}