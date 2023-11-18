/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core {
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
        write(target: T, frame: org.noear.socketd.transport.core.Frame);

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
        getRemoteAddress(target: T): java.net.InetSocketAddress;

        /**
         * 获取本地地址
         * @param {*} target
         * @return {java.net.InetSocketAddress}
         */
        getLocalAddress(target: T): java.net.InetSocketAddress;
    }
}

