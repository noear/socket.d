import type {Session} from "../Session";
import type {Message} from "../Message";
import {Listener} from "../Listener";

/**
 * 管道监听器
 *
 * @author noear
 * @since 2.0
 */
export class PipelineListener implements Listener {
    protected _deque = new Array<Listener>();

    /**
     * 前一个
     */
    prev(listener: Listener): PipelineListener {
        this._deque.unshift(listener);
        return this;
    }

    /**
     * 后一个
     */
    next(listener: Listener): PipelineListener {
        this._deque.push(listener);
        return this;
    }

    /**
     * 数量（二级监听器的数据）
     * */
    size(): number {
        return this._deque.length;
    }

    /**
     * 打开时
     *
     * @param session 会话
     */
    onOpen(session: Session) {
        for (const listener of this._deque) {
            listener.onOpen(session);
        }
    }

    /**
     * 收到消息时
     *
     * @param session 会话
     * @param message 消息
     */
    onMessage(session: Session, message: Message) {
        for (const listener of this._deque) {
            listener.onMessage(session, message);
        }
    }

    /**
     * 收到答复时
     *
     * @param session 会话
     * @param message 消息
     */
    onReply(session: Session, message: Message) {
        for (const listener of this._deque) {
            listener.onReply(session, message);
        }
    }

    /**
     * 发送消息时
     *
     * @param session 会话
     * @param message 消息
     */
    onSend(session: Session, message: Message) {
        for (const listener of this._deque) {
            listener.onSend(session, message);
        }
    }

    /**
     * 关闭时
     *
     * @param session 会话
     */
    onClose(session: Session) {
        for (const listener of this._deque) {
            listener.onClose(session);
        }
    }

    /**
     * 出错时
     *
     * @param session 会话
     * @param error   错误信息
     */
    onError(session: Session, error: Error) {
        for (const listener of this._deque) {
            listener.onError(session, error);
        }
    }
}