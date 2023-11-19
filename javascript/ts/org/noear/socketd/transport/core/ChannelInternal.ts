import {Channel} from "./Channel";
import {Session} from "./Session";

/**
 * 通道内部扩展
 *
 * @author noear
 * @since 2.0
 * @class
 */
export interface ChannelInternal extends Channel {
    /**
     * 设置会话
     *
     * @param {*} session
     */
    setSession(session: Session);
}