import {SocketdChannelException} from "../../Exceptions";
import {Channel} from "./Channel";
import {Constants} from "./Constants";

/**
 * 断言
 *
 * @author noear
 * @since 2.0
 * @class
 */
export class Asserts {
    /**
     * 断言关闭
     * @param {*} channel
     */
    public static assertClosed(channel: Channel) {
        if (channel != null && channel.isClosed() > 0) {
            throw new SocketdChannelException({message: "This channel is closed, sessionId=" + channel.getSession().sessionId()});
        }
    }

    /**
     * 断言关闭
     * @param {*} channel
     */
    public static assertClosedByUser(channel: Channel) {
        if (channel != null && channel.isClosed() === Constants.CLOSE3_USER) {
            throw new SocketdChannelException({message: "This channel is closed, sessionId=" + channel.getSession().sessionId()});
        }
    }

    /**
     * 断言null
     *
     * @param {*} val
     * @param {string} name
     */
    public static assertNull(val: any, name: string) {
        if (val == undefined || val == null) {
            throw new Error("The argument cannot be null: " + name);
        }
    }
}