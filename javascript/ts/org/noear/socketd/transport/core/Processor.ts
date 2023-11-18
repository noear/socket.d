/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core {
    /**
     * 处理器
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export interface Processor {
        /**
         * 设置监听
         * @param {*} listener
         */
        setListener(listener: org.noear.socketd.transport.core.Listener);

        /**
         * 接收处理
         * @param {*} channel
         * @param {org.noear.socketd.transport.core.Frame} frame
         */
        onReceive(channel: org.noear.socketd.transport.core.Channel, frame: org.noear.socketd.transport.core.Frame);

        /**
         * 打开时
         * 
         * @param {*} channel 通道
         */
        onOpen(channel: org.noear.socketd.transport.core.Channel);

        /**
         * 收到消息时
         * 
         * @param {*} channel 通道
         * @param {*} message 消息
         */
        onMessage(channel: org.noear.socketd.transport.core.Channel, message: org.noear.socketd.transport.core.Message);

        /**
         * 关闭时
         * 
         * @param {*} channel 通道
         */
        onClose(channel: org.noear.socketd.transport.core.Channel);

        /**
         * 出错时
         * 
         * @param {*} channel 通道
         * @param {Error} error   错误信息
         */
        onError(channel: org.noear.socketd.transport.core.Channel, error: Error);
    }
}

