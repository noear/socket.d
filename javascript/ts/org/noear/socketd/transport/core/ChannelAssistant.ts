import {Frame} from "./Frame";

/**
 * 通道助理
 *
 * @author noear
 * @since 2.0
 * @class
 */
export interface ChannelAssistant<T> {
    /**
     * 写入
     *
     * @param {*} target 目标
     * @param {org.noear.socketd.transport.core.Frame} frame  帧
     */
    write(target: T, frame: Frame);

    /**
     * 是否有效
     * @param {*} target
     * @return {boolean}
     */
    isValid(target: T): boolean;

    /**
     * 关闭
     * @param {*} target
     */
    close(target: T);

    /**
     * 获取远程地址
     * @param {*} target
     * @return {java.net.InetSocketAddress}
     */
    getRemoteAddress(target: T): string;

    /**
     * 获取本地地址
     * @param {*} target
     * @return {java.net.InetSocketAddress}
     */
    getLocalAddress(target: T): string;
}