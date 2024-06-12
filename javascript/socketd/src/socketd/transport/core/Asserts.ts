import type {Channel} from "./Channel";
import {Constants} from "./Constants";
import {SocketDChannelException, SocketDSizeLimitException} from "../../exception/SocketDException";

/**
 * 断言
 *
 * @author noear
 * @since 2.0
 */
export class Asserts {
    /**
     * 断言关闭
     */
    static assertClosed(channel: Channel | null) {
        if (channel != null && channel.closeCode() > 0) {
            throw new SocketDChannelException("This channel is closed, sessionId=" + channel.getSession().sessionId());
        }
    }

     static  isClosedAndEnd( channel:Channel) {
         return channel.closeCode() == Constants.CLOSE2009_USER
             || channel.closeCode() == Constants.CLOSE2008_OPEN_FAIL;
     }

    /**
     * 断言关闭
     */
    static assertClosedAndEnd(channel: Channel | null) {
        if (channel != null && Asserts.isClosedAndEnd(channel)) {
            throw new SocketDChannelException("This channel is closed, sessionId=" + channel.getSession().sessionId());
        }
    }

    /**
     * 断言 null
     */
    static assertNull(name: string, val: any) {
        if (val == null) {
            throw new Error("The argument cannot be null: " + name);
        }
    }

    /**
     * 断言 empty
     */
    static assertEmpty(name: string, val: string) {
        if (!val) {
            throw new Error("The argument cannot be empty: " + name);
        }
    }

    /**
     * 断言 size
     */
    static assertSize(name: string, size: number, limitSize: number) {
        if (size > limitSize) {
            const message = `This message ${name} size is out of limit ${limitSize} (${size})`;

            throw new SocketDSizeLimitException(message);
        }
    }
}