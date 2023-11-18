/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core {
    /**
     * 通道
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export interface Channel {
        /**
         * 获取附件
         * @param {string} name
         * @return {*}
         */
        getAttachment<T>(name: string): T;

        /**
         * 设置附件
         * @param {string} name
         * @param {*} val
         */
        setAttachment(name: string, val: any);

        /**
         * 移除接收器（答复接收器）
         * @param {string} sid
         */
        removeAcceptor(sid: string);

        /**
         * 是否有效
         * @return {boolean}
         */
        isValid(): boolean;

        /**
         * 是否已关闭
         * @return {number}
         */
        isClosed(): number;

        /**
         * 关闭（1协议关，2用户关）
         * 
         * @param {number} code
         */
        close(code: number);

        /**
         * 获取配置
         * @return {*}
         */
        getConfig(): org.noear.socketd.transport.core.Config;

        /**
         * 获取请求计数（用于背压控制）
         * @return {java.util.concurrent.atomic.AtomicInteger}
         */
        getRequests(): java.util.concurrent.atomic.AtomicInteger;

        /**
         * 设置握手信息
         * 
         * @param {org.noear.socketd.transport.core.internal.HandshakeInternal} handshake 握手信息
         */
        setHandshake(handshake: org.noear.socketd.transport.core.internal.HandshakeInternal);

        /**
         * 获取握手信息
         * @return {org.noear.socketd.transport.core.internal.HandshakeInternal}
         */
        getHandshake(): org.noear.socketd.transport.core.internal.HandshakeInternal;

        /**
         * 获取远程地址
         * @return {java.net.InetSocketAddress}
         */
        getRemoteAddress(): java.net.InetSocketAddress;

        /**
         * 获取本地地址
         * @return {java.net.InetSocketAddress}
         */
        getLocalAddress(): java.net.InetSocketAddress;

        /**
         * 发送连接（握手）
         * 
         * @param {string} url 连接地址
         */
        sendConnect(url: string);

        /**
         * 发送连接确认（握手）
         * 
         * @param {*} connectMessage 连接消息
         */
        sendConnack(connectMessage: org.noear.socketd.transport.core.Message);

        /**
         * 发送 Ping（心跳）
         */
        sendPing();

        /**
         * 发送 Pong（心跳）
         */
        sendPong();

        /**
         * 发送 Close
         */
        sendClose();

        /**
         * 发送
         * 
         * @param {org.noear.socketd.transport.core.Frame} frame    帧
         * @param {*} acceptor 答复接收器（没有则为 null）
         */
        send(frame: org.noear.socketd.transport.core.Frame, acceptor: org.noear.socketd.transport.core.Acceptor);

        /**
         * 接收（接收答复帧）
         * 
         * @param {org.noear.socketd.transport.core.Frame} frame 帧
         * @param {*} onError
         */
        retrieve(frame: org.noear.socketd.transport.core.Frame, onError: (p1: Error) => void);

        /**
         * 手动重连（一般是自动）
         */
        reconnect();

        /**
         * 获取会话
         * @return {*}
         */
        getSession(): org.noear.socketd.transport.core.Session;
    }
}

