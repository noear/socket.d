/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core.internal {
    /**
     * 通道默认实现（每个连接都会建立一个或多个通道）
     * 
     * @author noear
     * @since 2.0
     * @param {*} source
     * @param {*} config
     * @param {*} assistant
     * @class
     * @extends org.noear.socketd.transport.core.ChannelBase
     */
    export class ChannelDefault<S> extends org.noear.socketd.transport.core.ChannelBase implements org.noear.socketd.transport.core.ChannelInternal {
        static log: org.slf4j.Logger; public static log_$LI$(): org.slf4j.Logger { if (ChannelDefault.log == null) { ChannelDefault.log = org.slf4j.LoggerFactory.getLogger(ChannelDefault); }  return ChannelDefault.log; }

        /*private*/ source: S;

        /*private*/ acceptorMap: any;

        /*private*/ assistant: org.noear.socketd.transport.core.ChannelAssistant<S>;

        /*private*/ session: org.noear.socketd.transport.core.Session;

        public constructor(source: S, config: org.noear.socketd.transport.core.Config, assistant: org.noear.socketd.transport.core.ChannelAssistant<S>) {
            super(config);
            if (this.source === undefined) { this.source = null; }
            if (this.acceptorMap === undefined) { this.acceptorMap = null; }
            if (this.assistant === undefined) { this.assistant = null; }
            if (this.session === undefined) { this.session = null; }
            this.source = source;
            this.assistant = assistant;
            this.acceptorMap = <any>(new java.util.concurrent.ConcurrentHashMap<any, any>());
        }

        /**
         * 移除接收器（答复接收器）
         * @param {string} sid
         */
        public removeAcceptor(sid: string) {
            const acceptor: org.noear.socketd.transport.core.Acceptor = /* remove */(map => { let deleted = this.acceptorMap[sid];delete this.acceptorMap[sid];return deleted;})(this.acceptorMap);
            if (acceptor != null && ChannelDefault.log_$LI$().isDebugEnabled()){
                ChannelDefault.log_$LI$().debug("The acceptor is actively removed, sid={}", sid);
            }
        }

        /**
         * 是否有效
         * @return {boolean}
         */
        public isValid(): boolean {
            return this.isClosed() === 0 && this.assistant.isValid(this.source);
        }

        /**
         * 获取远程地址
         * @return {java.net.InetSocketAddress}
         */
        public getRemoteAddress(): java.net.InetSocketAddress {
            return this.assistant.getRemoteAddress(this.source);
        }

        /**
         * 获取本地地址
         * @return {java.net.InetSocketAddress}
         */
        public getLocalAddress(): java.net.InetSocketAddress {
            return this.assistant.getLocalAddress(this.source);
        }

        /**
         * 发送
         * @param {org.noear.socketd.transport.core.Frame} frame
         * @param {*} acceptor
         */
        public send(frame: org.noear.socketd.transport.core.Frame, acceptor: org.noear.socketd.transport.core.Acceptor) {
            org.noear.socketd.transport.core.Asserts.assertClosed(this);
            if (ChannelDefault.log_$LI$().isDebugEnabled()){
                if (this.getConfig().clientMode()){
                    ChannelDefault.log_$LI$().debug("C-SEN:{}", frame);
                } else {
                    ChannelDefault.log_$LI$().debug("S-SEN:{}", frame);
                }
            }
            if (frame.getMessage() != null){
                const message: org.noear.socketd.transport.core.Message = frame.getMessage();
                if (acceptor != null){
                    /* put */(this.acceptorMap[message.sid()] = acceptor);
                }
                if (message.entity() != null){
                    const ins: { str: string, cursor: number } = message.data();
                    try {
                        if (message.dataSize() > org.noear.socketd.transport.core.Config.MAX_SIZE_FRAGMENT){
                            const fragmentIndex: java.util.concurrent.atomic.AtomicReference<number> = <any>(new java.util.concurrent.atomic.AtomicReference<any>(0));
                            while((true)) {{
                                const fragmentEntity: org.noear.socketd.transport.core.Entity = this.getConfig().getFragmentHandler().nextFragment(this.getConfig(), fragmentIndex, message.entity());
                                if (fragmentEntity != null){
                                    const fragmentFrame: org.noear.socketd.transport.core.Frame = new org.noear.socketd.transport.core.Frame(frame.getFlag(), new org.noear.socketd.transport.core.internal.MessageDefault().flag(frame.getFlag()).sid$java_lang_String(message.sid()).entity$org_noear_socketd_transport_core_Entity(fragmentEntity));
                                    this.assistant.write(this.source, fragmentFrame);
                                } else {
                                    return;
                                }
                            }};
                        } else {
                            this.assistant.write(this.source, frame);
                            return;
                        }
                    } finally {
                        ins.close();
                    }
                }
            }
            this.assistant.write(this.source, frame);
        }

        /**
         * 接收（接收答复帧）
         * 
         * @param {org.noear.socketd.transport.core.Frame} frame 帧
         * @param {*} onError
         */
        public retrieve(frame: org.noear.socketd.transport.core.Frame, onError: (p1: Error) => void) {
            const acceptor: org.noear.socketd.transport.core.Acceptor = /* get */((m,k) => m[k]===undefined?null:m[k])(this.acceptorMap, frame.getMessage().sid());
            if (acceptor != null){
                if (acceptor.isSingle() || frame.getFlag() === org.noear.socketd.transport.core.Flag.ReplyEnd){
                    /* remove */(map => { let deleted = this.acceptorMap[frame.getMessage().sid()];delete this.acceptorMap[frame.getMessage().sid()];return deleted;})(this.acceptorMap);
                }
                if (acceptor.isSingle()){
                    acceptor.accept(frame.getMessage(), <any>(((funcInst: any) => { if (typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['accept'] ? funcInst['accept'] : funcInst) .call(funcInst, arg0)})(onError)));
                } else {
                    this.getConfig().getChannelExecutor().submit(((acceptor) => {
                        return () => {
                            acceptor.accept(frame.getMessage(), <any>(((funcInst: any) => { if (typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['accept'] ? funcInst['accept'] : funcInst) .call(funcInst, arg0)})(onError)));
                        }
                    })(acceptor));
                }
            } else {
                if (ChannelDefault.log_$LI$().isDebugEnabled()){
                    ChannelDefault.log_$LI$().debug("Acceptor not found, sid={}", frame.getMessage().sid());
                }
            }
        }

        /**
         * 手动重连（一般是自动）
         */
        public reconnect() {
        }

        /**
         * 获取会话
         * @return {*}
         */
        public getSession(): org.noear.socketd.transport.core.Session {
            if (this.session == null){
                this.session = new org.noear.socketd.transport.core.internal.SessionDefault(this);
            }
            return this.session;
        }

        /**
         * 
         * @param {*} session
         */
        public setSession(session: org.noear.socketd.transport.core.Session) {
            this.session = session;
        }

        /**
         * 关闭
         * @param {number} code
         */
        public close(code: number) {
            if (ChannelDefault.log_$LI$().isDebugEnabled()){
                ChannelDefault.log_$LI$().debug("The channel will be closed, sessionId={}", this.getSession().sessionId());
            }
            super.close(code);
            /* clear */(obj => { for (let member in obj) delete obj[member]; })(this.acceptorMap);
            try {
                this.assistant.close(this.source);
            } catch(e) {
                if (ChannelDefault.log_$LI$().isDebugEnabled()){
                    ChannelDefault.log_$LI$().debug("{}", e);
                }
            }
        }
    }
    ChannelDefault["__class"] = "org.noear.socketd.transport.core.internal.ChannelDefault";
    ChannelDefault["__interfaces"] = ["org.noear.socketd.transport.core.Channel","org.noear.socketd.transport.core.ChannelInternal"];


}

