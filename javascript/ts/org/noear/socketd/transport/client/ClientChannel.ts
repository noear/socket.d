/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.client {
    /**
     * 客户端通道
     * 
     * @author noear
     * @since 2.0
     * @param {*} real
     * @param {*} connector
     * @class
     * @extends org.noear.socketd.transport.core.ChannelBase
     */
    export class ClientChannel extends org.noear.socketd.transport.core.ChannelBase implements org.noear.socketd.transport.core.Channel {
        static log: org.slf4j.Logger; public static log_$LI$(): org.slf4j.Logger { if (ClientChannel.log == null) { ClientChannel.log = org.slf4j.LoggerFactory.getLogger(ClientChannel); }  return ClientChannel.log; }

        /*private*/ connector: org.noear.socketd.transport.client.ClientConnector;

        /*private*/ real: org.noear.socketd.transport.core.Channel;

        /*private*/ heartbeatHandler: org.noear.socketd.transport.core.HeartbeatHandler;

        /*private*/ heartbeatScheduledFuture: java.util.concurrent.ScheduledFuture<any>;

        public constructor(real: org.noear.socketd.transport.core.Channel, connector: org.noear.socketd.transport.client.ClientConnector) {
            super(real.getConfig());
            if (this.connector === undefined) { this.connector = null; }
            if (this.real === undefined) { this.real = null; }
            if (this.heartbeatHandler === undefined) { this.heartbeatHandler = null; }
            if (this.heartbeatScheduledFuture === undefined) { this.heartbeatScheduledFuture = null; }
            this.real = real;
            this.connector = connector;
            this.heartbeatHandler = <any>(((funcInst: any) => { if (typeof funcInst == 'function') { return funcInst } return (session) =>  (funcInst['heartbeat'] ? funcInst['heartbeat'] : funcInst) .call(funcInst, session)})(connector.heartbeatHandler()));
            if (this.heartbeatHandler == null){
                this.heartbeatHandler = ((funcInst: any) => { if (typeof funcInst == 'function') { return funcInst } return (session) =>  (funcInst['heartbeat'] ? funcInst['heartbeat'] : funcInst) .call(funcInst, session)})(new org.noear.socketd.transport.core.internal.HeartbeatHandlerDefault());
            }
            this.initHeartbeat();
        }

        /**
         * 初始化心跳（关闭后，手动重链时也会用到）
         * @private
         */
        /*private*/ initHeartbeat() {
            if (this.connector.autoReconnect()){
                if (this.heartbeatScheduledFuture == null || this.heartbeatScheduledFuture.isCancelled()){
                    this.heartbeatScheduledFuture = org.noear.socketd.utils.RunUtils.delayAndRepeat(() => {
                        try {
                            this.heartbeatHandle();
                        } catch(e) {
                            if (ClientChannel.log_$LI$().isDebugEnabled()){
                                ClientChannel.log_$LI$().debug("{}", e);
                            }
                        }
                    }, this.connector.heartbeatInterval());
                }
            }
        }

        /**
         * 移除接收器（答复接收器）
         * @param {string} sid
         */
        public removeAcceptor(sid: string) {
            if (this.real != null){
                this.real.removeAcceptor(sid);
            }
        }

        /**
         * 是否有效
         * @return {boolean}
         */
        public isValid(): boolean {
            if (this.real == null){
                return false;
            } else {
                return this.real.isValid();
            }
        }

        /**
         * 是否已关闭
         * @return {number}
         */
        public isClosed(): number {
            if (this.real == null){
                return 0;
            } else {
                return this.real.isClosed();
            }
        }

        /**
         * 获取远程地址
         * @return {java.net.InetSocketAddress}
         */
        public getRemoteAddress(): java.net.InetSocketAddress {
            if (this.real == null){
                return null;
            } else {
                return this.real.getRemoteAddress();
            }
        }

        /**
         * 获取本地地址
         * @return {java.net.InetSocketAddress}
         */
        public getLocalAddress(): java.net.InetSocketAddress {
            if (this.real == null){
                return null;
            } else {
                return this.real.getLocalAddress();
            }
        }

        /**
         * 心跳处理
         * @private
         */
        /*private*/ heartbeatHandle() {
            if (this.real != null){
                if (this.real.getHandshake() == null){
                    return;
                }
                if (this.real.isClosed() > 0){
                    if (ClientChannel.log_$LI$().isDebugEnabled()){
                        ClientChannel.log_$LI$().debug("The channel is closed (pause heartbeat), sessionId={}", this.getSession().sessionId());
                    }
                    return;
                }
            }
            {
                try {
                    this.prepareCheck();
                    this.heartbeatHandler(this.getSession());
                } catch(__e) {
                    if(__e != null && __e instanceof <any>org.noear.socketd.exception.SocketdException) {
                        const e: org.noear.socketd.exception.SocketdException = <org.noear.socketd.exception.SocketdException>__e;
                        throw e;

                    }
                    if(__e != null && (__e["__classes"] && __e["__classes"].indexOf("java.lang.Throwable") >= 0) || __e != null && __e instanceof <any>Error) {
                        const e: Error = <Error>__e;
                        if (this.connector.autoReconnect()){
                            this.real.close(org.noear.socketd.transport.core.Constants.CLOSE2_ERROR);
                            this.real = null;
                        }
                        throw new org.noear.socketd.exception.SocketdChannelException(e);

                    }
                }
            };
        }

        /**
         * 发送
         * 
         * @param {org.noear.socketd.transport.core.Frame} frame    帧
         * @param {*} acceptor 答复接收器（没有则为 null）
         */
        public send(frame: org.noear.socketd.transport.core.Frame, acceptor: org.noear.socketd.transport.core.Acceptor) {
            org.noear.socketd.transport.core.Asserts.assertClosedByUser(this.real);
            {
                try {
                    this.prepareCheck();
                    this.real.send(frame, acceptor);
                } catch(__e) {
                    if(__e != null && __e instanceof <any>org.noear.socketd.exception.SocketdException) {
                        const e: org.noear.socketd.exception.SocketdException = <org.noear.socketd.exception.SocketdException>__e;
                        throw e;

                    }
                    if(__e != null && (__e["__classes"] && __e["__classes"].indexOf("java.lang.Throwable") >= 0) || __e != null && __e instanceof <any>Error) {
                        const e: Error = <Error>__e;
                        if (this.connector.autoReconnect()){
                            this.real.close(org.noear.socketd.transport.core.Constants.CLOSE2_ERROR);
                            this.real = null;
                        }
                        throw new org.noear.socketd.exception.SocketdChannelException(e);

                    }
                }
            };
        }

        /**
         * 接收（接收答复帧）
         * 
         * @param {org.noear.socketd.transport.core.Frame} frame 帧
         * @param {*} onError
         */
        public retrieve(frame: org.noear.socketd.transport.core.Frame, onError: (p1: Error) => void) {
            this.real.retrieve(frame, <any>(((funcInst: any) => { if (typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['accept'] ? funcInst['accept'] : funcInst) .call(funcInst, arg0)})(onError)));
        }

        /**
         * 获取会话
         * @return {*}
         */
        public getSession(): org.noear.socketd.transport.core.Session {
            return this.real.getSession();
        }

        /**
         * 
         */
        public reconnect() {
            this.initHeartbeat();
            this.prepareCheck();
        }

        /**
         * 关闭
         * @param {number} code
         */
        public close(code: number) {
            org.noear.socketd.utils.RunUtils.runAndTry({ run: () => this.heartbeatScheduledFuture.cancel(true) });
            org.noear.socketd.utils.RunUtils.runAndTry({ run: () => this.connector.close() });
            org.noear.socketd.utils.RunUtils.runAndTry({ run: () => this.real.close(code) });
        }

        /**
         * 预备检
         * 
         * @return {boolean} 是否为新链接
         * @private
         */
        /*private*/ prepareCheck(): boolean {
            if (this.real == null || this.real.isValid() === false){
                this.real = this.connector.connect();
                return true;
            } else {
                return false;
            }
        }
    }
    ClientChannel["__class"] = "org.noear.socketd.transport.client.ClientChannel";
    ClientChannel["__interfaces"] = ["org.noear.socketd.transport.core.Channel"];


}

