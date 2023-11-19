import {Session} from "./Session";

/**
 * 心跳处理器
 *
 * @author noear
 * @since 2.0
 * @class
 */
export interface HeartbeatHandler {
    /**
     * 心跳处理
     * @param {*} session
     */
    (session: Session);
}