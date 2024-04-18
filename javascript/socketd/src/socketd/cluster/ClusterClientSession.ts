import type {ClientSession} from "../transport/client/ClientSession";
import type { Entity } from "../transport/core/Entity";
import {RequestStream, SendStream, SubscribeStream} from "../transport/stream/Stream";
import {StrUtils} from "../utils/StrUtils";
import {SocketDException} from "../exception/SocketDException";
import {RunUtils} from "../utils/RunUtils";
import {LoadBalancer} from "./LoadBalancer";

/**
 * 集群客户端会话
 *
 * @author noear
 * @since 2.1
 */
export class ClusterClientSession implements ClientSession {
    //会话集合
    private _sessionSet: Set<ClientSession>;
    //会话id
    private _sessionId: string;

    constructor(sessions: ClientSession[]) {
        this._sessionSet = new Set<ClientSession>(sessions);
        this._sessionId = StrUtils.guid();
    }

    /**
     * 获取所有会话
     */
    getSessionAll(): Set<ClientSession> {
        return this._sessionSet;
    }

    /**
     * 获取任一个会话（轮询负栽均衡）
     */
    getSessionAny(diversionOrNull: string | null): ClientSession {
        let session: ClientSession | null = null;

        if (diversionOrNull) {
            session = LoadBalancer.getAnyByHash(this._sessionSet, diversionOrNull);
        } else {
            session = LoadBalancer.getAnyByPoll(this._sessionSet);
        }

        if (session == null) {
            throw new SocketDException("No session is available!");
        } else {
            return session;
        }
    }

    /**
     * 获取任一个会话（轮询负栽均衡）
     *
     * @deprecated 2.3
     */
    getSessionOne(): ClientSession {
        return this.getSessionAny(null);
    }

    /**
     * 是否有效
     * */
    isValid(): boolean {
        for (const session of this._sessionSet) {
            if (session.isValid()) {
                return true;
            }
        }

        return false;
    }

    /**
     * 是否关闭中
     * */
    isClosing(): boolean {
        for (const session of this._sessionSet) {
            if (session.isClosing()) {
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
    send(event: string, content: Entity): SendStream {
        const sender = this.getSessionAny(null);

        return sender.send(event, content);
    }


    /**
     * 发送并请求（限为一次答复；指定回调）
     *
     * @param event    事件
     * @param content  内容
     * @param timeout  超时
     */
    sendAndRequest(event: string, content: Entity, timeout?: number): RequestStream {
        const sender = this.getSessionAny(null);

        return sender.sendAndRequest(event, content, timeout);
    }

    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event    事件
     * @param content  内容
     * @param timeout  超时
     */
    sendAndSubscribe(event: string, content: Entity, timeout: number): SubscribeStream {
        const sender = this.getSessionAny(null);

        return sender.sendAndSubscribe(event, content, timeout);
    }

    closeStarting() {
        this.preclose();
    }

    /**
     * 预关闭
     * */
    preclose() {
        for (const s of this._sessionSet) {
            //某个关闭出错，不影响别的关闭
            RunUtils.runAndTry(s.preclose.bind(s));
        }
    }

    /**
     * 关闭
     */
    close() {
        for (const s of this._sessionSet) {
            //某个关闭出错，不影响别的关闭
            RunUtils.runAndTry(s.close.bind(s));
        }
    }
}