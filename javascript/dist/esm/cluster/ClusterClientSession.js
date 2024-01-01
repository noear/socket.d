import { StrUtils } from "../utils/StrUtils";
import { SocketdException } from "../exception/SocketdException";
import "../transport/client/ClientChannel";
import { RunUtils } from "../utils/RunUtils";
/**
 * 集群客户端会话
 *
 * @author noear
 * @since 2.1
 */
export class ClusterClientSession {
    constructor(sessions) {
        this._sessionSet = sessions;
        this._sessionId = StrUtils.guid();
        this._sessionRoundCounter = 0;
    }
    /**
     * 获取所有会话
     */
    getSessionAll() {
        return this._sessionSet;
    }
    /**
     * 获取一个会话（轮询负栽均衡）
     */
    getSessionOne() {
        if (this._sessionSet.length == 0) {
            //没有会话
            throw new SocketdException("No session!");
        }
        else if (this._sessionSet.length == 1) {
            //只有一个就不管了
            return this._sessionSet[0];
        }
        else {
            //查找可用的会话
            const sessions = new Array();
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
            if (counter > 999999999) {
                this._sessionRoundCounter = 0;
            }
            return sessions[idx];
        }
    }
    isValid() {
        for (const session of this._sessionSet) {
            if (session.isValid()) {
                return true;
            }
        }
        return false;
    }
    sessionId() {
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
    send(event, content) {
        const sender = this.getSessionOne();
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
    sendAndRequest(event, content, consumer, timeout) {
        const sender = this.getSessionOne();
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
    sendAndSubscribe(event, content, consumer, timeout) {
        const sender = this.getSessionOne();
        return sender.sendAndSubscribe(event, content, consumer, timeout);
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
