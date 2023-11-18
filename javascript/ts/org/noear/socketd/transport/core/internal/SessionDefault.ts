/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core.internal {
    /**
     * 会话默认实现
     * 
     * @author noear
     * @since 2.0
     * @param {*} channel
     * @class
     * @extends org.noear.socketd.transport.core.SessionBase
     */
    export class SessionDefault extends org.noear.socketd.transport.core.SessionBase {
        static log: org.slf4j.Logger; public static log_$LI$(): org.slf4j.Logger { if (SessionDefault.log == null) { SessionDefault.log = org.slf4j.LoggerFactory.getLogger(SessionDefault); }  return SessionDefault.log; }

        /*private*/ __pathNew: string;

        public constructor(channel: org.noear.socketd.transport.core.Channel) {
            super(channel);
            if (this.__pathNew === undefined) { this.__pathNew = null; }
        }

        /**
         * 是否有效
         * @return {boolean}
         */
        public isValid(): boolean {
            return this.channel.isValid();
        }

        /**
         * 获取远程地址
         * @return {java.net.InetSocketAddress}
         */
        public remoteAddress(): java.net.InetSocketAddress {
            return this.channel.getRemoteAddress();
        }

        /**
         * 获取本地地址
         * @return {java.net.InetSocketAddress}
         */
        public localAddress(): java.net.InetSocketAddress {
            return this.channel.getLocalAddress();
        }

        /**
         * 获取握手信息
         * @return {*}
         */
        public handshake(): org.noear.socketd.transport.core.Handshake {
            return this.channel.getHandshake();
        }

        /**
         * 获取握手参数
         * 
         * @param {string} name 名字
         * @return {string}
         */
        public param(name: string): string {
            return this.handshake()['param$java_lang_String'](name);
        }

        /**
         * 获取握手参数或默认值
         * 
         * @param {string} name 名字
         * @param {string} def  默认值
         * @return {string}
         */
        public paramOrDefault(name: string, def: string): string {
            return this.handshake().paramOrDefault(name, def);
        }

        /**
         * 获取路径
         * @return {string}
         */
        public path(): string {
            if (this.__pathNew == null){
                return this.handshake().uri().getPath();
            } else {
                return this.__pathNew;
            }
        }

        /**
         * 设置新路径
         * @param {string} pathNew
         */
        public pathNew(pathNew: string) {
            this.__pathNew = pathNew;
        }

        /**
         * 手动重连（一般是自动）
         */
        public reconnect() {
            this.channel.reconnect();
        }

        /**
         * 手动发送 Ping（一般是自动）
         */
        public sendPing() {
            this.channel.sendPing();
        }

        /**
         * 发送
         * @param {string} topic
         * @param {*} content
         */
        public send(topic: string, content: org.noear.socketd.transport.core.Entity) {
            const message: org.noear.socketd.transport.core.Message = new org.noear.socketd.transport.core.internal.MessageDefault().sid$java_lang_String(this.generateId()).topic$java_lang_String(topic).entity$org_noear_socketd_transport_core_Entity(content);
            this.channel.send(new org.noear.socketd.transport.core.Frame(org.noear.socketd.transport.core.Flag.Message, message), null);
        }

        public sendAndRequest$java_lang_String$org_noear_socketd_transport_core_Entity(topic: string, content: org.noear.socketd.transport.core.Entity): org.noear.socketd.transport.core.Entity {
            return this.sendAndRequest$java_lang_String$org_noear_socketd_transport_core_Entity$long(topic, content, this.channel.getConfig().getRequestTimeout());
        }

        public sendAndRequest$java_lang_String$org_noear_socketd_transport_core_Entity$long(topic: string, content: org.noear.socketd.transport.core.Entity, timeout: number): org.noear.socketd.transport.core.Entity {
            if (timeout < 100){
                timeout = this.channel.getConfig().getRequestTimeout();
            }
            if (this.channel.getRequests().get() > this.channel.getConfig().getMaxRequests()){
                throw new org.noear.socketd.exception.SocketdException("Sending too many requests: " + this.channel.getRequests().get());
            } else {
                this.channel.getRequests().incrementAndGet();
            }
            const message: org.noear.socketd.transport.core.Message = new org.noear.socketd.transport.core.internal.MessageDefault().sid$java_lang_String(this.generateId()).topic$java_lang_String(topic).entity$org_noear_socketd_transport_core_Entity(content);
            try {
                const future: java.util.concurrent.CompletableFuture<org.noear.socketd.transport.core.Entity> = <any>(new java.util.concurrent.CompletableFuture<any>());
                this.channel.send(new org.noear.socketd.transport.core.Frame(org.noear.socketd.transport.core.Flag.Request, message), new org.noear.socketd.transport.core.internal.AcceptorRequest(future, timeout));
                try {
                    return future.get(timeout, java.util.concurrent.TimeUnit.MILLISECONDS);
                } catch(__e) {
                    if(__e != null && (__e["__classes"] && __e["__classes"].indexOf("java.util.concurrent.TimeoutException") >= 0)) {
                        const e: Error = <Error>__e;
                        const hint: { str: string, toString: Function } = { str: "", toString: function() { return this.str; } };
                        /* append */(sb => { sb.str += <any>this.channel.getSession().sessionId(); return sb; })(/* append */(sb => { sb.str += <any>", sessionId="; return sb; })(hint));
                        /* append */(sb => { sb.str += <any>topic; return sb; })(/* append */(sb => { sb.str += <any>", topic="; return sb; })(hint));
                        /* append */(sb => { sb.str += <any>message.sid(); return sb; })(/* append */(sb => { sb.str += <any>", sid="; return sb; })(hint));
                        if (this.channel.isValid()){
                            throw new org.noear.socketd.exception.SocketdTimeoutException("Request reply timeout>" + timeout + hint);
                        } else {
                            throw new org.noear.socketd.exception.SocketdChannelException("This channel is closed" + hint);
                        }

                    }
                    if(__e != null && (__e["__classes"] && __e["__classes"].indexOf("java.lang.Throwable") >= 0) || __e != null && __e instanceof <any>Error) {
                        const e: Error = <Error>__e;
                        throw new org.noear.socketd.exception.SocketdException(e);

                    }
                }
            } finally {
                this.channel.removeAcceptor(message.sid());
                this.channel.getRequests().decrementAndGet();
            }
        }

        /**
         * 发送并请求（限为一次答复；指定超时）
         * 
         * @param {string} topic   主题
         * @param {*} content 内容
         * @param {number} timeout 超时（毫秒）
         * @return {*}
         */
        public sendAndRequest(topic?: any, content?: any, timeout?: any): org.noear.socketd.transport.core.Entity {
            if (((typeof topic === 'string') || topic === null) && ((content != null && (content.constructor != null && content.constructor["__interfaces"] != null && content.constructor["__interfaces"].indexOf("org.noear.socketd.transport.core.Entity") >= 0)) || content === null) && ((typeof timeout === 'number') || timeout === null)) {
                return <any>this.sendAndRequest$java_lang_String$org_noear_socketd_transport_core_Entity$long(topic, content, timeout);
            } else if (((typeof topic === 'string') || topic === null) && ((content != null && (content.constructor != null && content.constructor["__interfaces"] != null && content.constructor["__interfaces"].indexOf("org.noear.socketd.transport.core.Entity") >= 0)) || content === null) && timeout === undefined) {
                return <any>this.sendAndRequest$java_lang_String$org_noear_socketd_transport_core_Entity(topic, content);
            } else throw new Error('invalid overload');
        }

        /**
         * 发送并订阅（答复结束之前，不限答复次数）
         * 
         * @param {string} topic    主题
         * @param {*} content  内容
         * @param {*} consumer 回调消费者
         */
        public sendAndSubscribe(topic: string, content: org.noear.socketd.transport.core.Entity, consumer: org.noear.socketd.utils.IoConsumer<org.noear.socketd.transport.core.Entity>) {
            const message: org.noear.socketd.transport.core.Message = new org.noear.socketd.transport.core.internal.MessageDefault().sid$java_lang_String(this.generateId()).topic$java_lang_String(topic).entity$org_noear_socketd_transport_core_Entity(content);
            this.channel.send(new org.noear.socketd.transport.core.Frame(org.noear.socketd.transport.core.Flag.Subscribe, message), new org.noear.socketd.transport.core.internal.AcceptorSubscribe(<any>(((funcInst: any) => { if (typeof funcInst == 'function') { return funcInst } return (t) =>  (funcInst['accept'] ? funcInst['accept'] : funcInst) .call(funcInst, t)})(consumer))));
        }

        /**
         * 答复
         * 
         * @param {*} from    来源消息
         * @param {*} content 内容
         */
        public reply(from: org.noear.socketd.transport.core.Message, content: org.noear.socketd.transport.core.Entity) {
            this.channel.send(new org.noear.socketd.transport.core.Frame(org.noear.socketd.transport.core.Flag.Reply, new org.noear.socketd.transport.core.internal.MessageDefault().sid$java_lang_String(from.sid()).entity$org_noear_socketd_transport_core_Entity(content)), null);
        }

        /**
         * 答复并结束（即最后一次答复）
         * 
         * @param {*} from    来源消息
         * @param {*} content 内容
         */
        public replyEnd(from: org.noear.socketd.transport.core.Message, content: org.noear.socketd.transport.core.Entity) {
            this.channel.send(new org.noear.socketd.transport.core.Frame(org.noear.socketd.transport.core.Flag.ReplyEnd, new org.noear.socketd.transport.core.internal.MessageDefault().sid$java_lang_String(from.sid()).entity$org_noear_socketd_transport_core_Entity(content)), null);
        }

        /**
         * 关闭
         */
        public close() {
            if (SessionDefault.log_$LI$().isDebugEnabled()){
                SessionDefault.log_$LI$().debug("The session will be closed, sessionId={}", this.sessionId());
            }
            if (this.channel.isValid()){
                try {
                    this.channel.sendClose();
                } catch(e) {
                    if (SessionDefault.log_$LI$().isDebugEnabled()){
                        SessionDefault.log_$LI$().debug("{}", e);
                    }
                }
            }
            this.channel.close(org.noear.socketd.transport.core.Constants.CLOSE3_USER);
        }
    }
    SessionDefault["__class"] = "org.noear.socketd.transport.core.internal.SessionDefault";
    SessionDefault["__interfaces"] = ["org.noear.socketd.transport.core.Session"];


}

