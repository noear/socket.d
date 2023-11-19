import {Handshake} from "./Handshake";
import {MessageInternal} from "./MessageInternal";

export interface HandshakeInternal extends Handshake {
    /**
     * 获取消息源
     */
    getSource(): MessageInternal;
}