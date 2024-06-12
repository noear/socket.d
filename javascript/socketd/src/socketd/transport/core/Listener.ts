import type {Session} from "./Session";
import type {Message} from "./Message";
import type {IoBiConsumer, IoConsumer} from "./Typealias";
import {RouteSelector, RouteSelectorDefault} from "./RouteSelector";

/**
 * 监听器
 *
 * @author noear
 * @since 2.0
 */
export interface Listener {
    /**
     * 打开时
     *
     * @param session 会话
     */
    onOpen(session: Session);

    /**
     * 收到消息时
     *
     * @param session 会话
     * @param message 消息
     */
    onMessage(session: Session, message: Message);

    /**
     * 关闭时
     *
     * @param session 会话
     */
    onClose(session: Session);

    /**
     * 出错时
     *
     * @param session 会话
     * @param error   错误信息
     */
    onError(session: Session, error: any);
}