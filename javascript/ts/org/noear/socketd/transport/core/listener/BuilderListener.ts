/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core.listener {
    /**
     * 构建监听器
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export class BuilderListener implements org.noear.socketd.transport.core.Listener {
        /*private*/ onOpenHandler: org.noear.socketd.utils.IoConsumer<org.noear.socketd.transport.core.Session>;

        /*private*/ onMessageHandler: org.noear.socketd.utils.IoBiConsumer<org.noear.socketd.transport.core.Session, org.noear.socketd.transport.core.Message>;

        /*private*/ onCloseHandler: (p1: org.noear.socketd.transport.core.Session) => void;

        /*private*/ onErrorHandler: (p1: org.noear.socketd.transport.core.Session, p2: Error) => void;

        /*private*/ onMessageRouting: any;

        public onOpen$org_noear_socketd_utils_IoConsumer(onOpen: org.noear.socketd.utils.IoConsumer<org.noear.socketd.transport.core.Session>): BuilderListener {
            this.onOpenHandler = <any>(((funcInst: any) => { if (typeof funcInst == 'function') { return funcInst } return (t) =>  (funcInst['accept'] ? funcInst['accept'] : funcInst) .call(funcInst, t)})(onOpen));
            return this;
        }

        public onOpen(onOpen?: any): any {
            if (((typeof onOpen === 'function' && (<any>onOpen).length === 1) || onOpen === null)) {
                return <any>this.onOpen$org_noear_socketd_utils_IoConsumer(onOpen);
            } else if (((onOpen != null && (onOpen.constructor != null && onOpen.constructor["__interfaces"] != null && onOpen.constructor["__interfaces"].indexOf("org.noear.socketd.transport.core.Session") >= 0)) || onOpen === null)) {
                return <any>this.onOpen$org_noear_socketd_transport_core_Session(onOpen);
            } else throw new Error('invalid overload');
        }

        public onMessage$org_noear_socketd_utils_IoBiConsumer(onMessage: org.noear.socketd.utils.IoBiConsumer<org.noear.socketd.transport.core.Session, org.noear.socketd.transport.core.Message>): BuilderListener {
            this.onMessageHandler = <any>(((funcInst: any) => { if (typeof funcInst == 'function') { return funcInst } return (t, u) =>  (funcInst['accept'] ? funcInst['accept'] : funcInst) .call(funcInst, t, u)})(onMessage));
            return this;
        }

        public onClose$java_util_function_Consumer(onClose: (p1: org.noear.socketd.transport.core.Session) => void): BuilderListener {
            this.onCloseHandler = <any>(((funcInst: any) => { if (typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['accept'] ? funcInst['accept'] : funcInst) .call(funcInst, arg0)})(onClose));
            return this;
        }

        public onClose(onClose?: any): any {
            if (((typeof onClose === 'function' && (<any>onClose).length === 1) || onClose === null)) {
                return <any>this.onClose$java_util_function_Consumer(onClose);
            } else if (((onClose != null && (onClose.constructor != null && onClose.constructor["__interfaces"] != null && onClose.constructor["__interfaces"].indexOf("org.noear.socketd.transport.core.Session") >= 0)) || onClose === null)) {
                return <any>this.onClose$org_noear_socketd_transport_core_Session(onClose);
            } else throw new Error('invalid overload');
        }

        public onError$java_util_function_BiConsumer(onError: (p1: org.noear.socketd.transport.core.Session, p2: Error) => void): BuilderListener {
            this.onErrorHandler = <any>(((funcInst: any) => { if (typeof funcInst == 'function') { return funcInst } return (arg0, arg1) =>  (funcInst['accept'] ? funcInst['accept'] : funcInst) .call(funcInst, arg0, arg1)})(onError));
            return this;
        }

        public on(topic: string, handler: org.noear.socketd.utils.IoBiConsumer<org.noear.socketd.transport.core.Session, org.noear.socketd.transport.core.Message>): BuilderListener {
            /* put */(this.onMessageRouting[topic] = handler);
            return this;
        }

        public onOpen$org_noear_socketd_transport_core_Session(session: org.noear.socketd.transport.core.Session) {
            if (this.onOpenHandler != null){
                this.onOpenHandler(session);
            }
        }

        public onMessage$org_noear_socketd_transport_core_Session$org_noear_socketd_transport_core_Message(session: org.noear.socketd.transport.core.Session, message: org.noear.socketd.transport.core.Message) {
            if (this.onMessageHandler != null){
                this.onMessageHandler(session, message);
            }
            const messageHandler: org.noear.socketd.utils.IoBiConsumer<org.noear.socketd.transport.core.Session, org.noear.socketd.transport.core.Message> = <any>(((funcInst: any) => { if (typeof funcInst == 'function') { return funcInst } return (t, u) =>  (funcInst['accept'] ? funcInst['accept'] : funcInst) .call(funcInst, t, u)})(/* get */((m,k) => m[k]===undefined?null:m[k])(this.onMessageRouting, message.topic())));
            if (messageHandler != null){
                messageHandler(session, message);
            }
        }

        /**
         * 
         * @param {*} session
         * @param {*} message
         */
        public onMessage(session?: any, message?: any) {
            if (((session != null && (session.constructor != null && session.constructor["__interfaces"] != null && session.constructor["__interfaces"].indexOf("org.noear.socketd.transport.core.Session") >= 0)) || session === null) && ((message != null && (message.constructor != null && message.constructor["__interfaces"] != null && message.constructor["__interfaces"].indexOf("org.noear.socketd.transport.core.Message") >= 0)) || message === null)) {
                return <any>this.onMessage$org_noear_socketd_transport_core_Session$org_noear_socketd_transport_core_Message(session, message);
            } else if (((typeof session === 'function' && (<any>session).length === 2) || session === null) && message === undefined) {
                return <any>this.onMessage$org_noear_socketd_utils_IoBiConsumer(session);
            } else throw new Error('invalid overload');
        }

        public onClose$org_noear_socketd_transport_core_Session(session: org.noear.socketd.transport.core.Session) {
            if (this.onCloseHandler != null){
                (target => (typeof target === 'function') ? target(session) : (<any>target).accept(session))(this.onCloseHandler);
            }
        }

        public onError$org_noear_socketd_transport_core_Session$java_lang_Throwable(session: org.noear.socketd.transport.core.Session, error: Error) {
            if (this.onErrorHandler != null){
                (target => (typeof target === 'function') ? target(session, error) : (<any>target).accept(session, error))(this.onErrorHandler);
            }
        }

        /**
         * 
         * @param {*} session
         * @param {Error} error
         */
        public onError(session?: any, error?: any) {
            if (((session != null && (session.constructor != null && session.constructor["__interfaces"] != null && session.constructor["__interfaces"].indexOf("org.noear.socketd.transport.core.Session") >= 0)) || session === null) && ((error != null && (error["__classes"] && error["__classes"].indexOf("java.lang.Throwable") >= 0) || error != null && error instanceof <any>Error) || error === null)) {
                return <any>this.onError$org_noear_socketd_transport_core_Session$java_lang_Throwable(session, error);
            } else if (((typeof session === 'function' && (<any>session).length === 2) || session === null) && error === undefined) {
                return <any>this.onError$java_util_function_BiConsumer(session);
            } else throw new Error('invalid overload');
        }

        constructor() {
            if (this.onOpenHandler === undefined) { this.onOpenHandler = null; }
            if (this.onMessageHandler === undefined) { this.onMessageHandler = null; }
            if (this.onCloseHandler === undefined) { this.onCloseHandler = null; }
            if (this.onErrorHandler === undefined) { this.onErrorHandler = null; }
            this.onMessageRouting = <any>(new java.util.concurrent.ConcurrentHashMap<any, any>());
        }
    }
    BuilderListener["__class"] = "org.noear.socketd.transport.core.listener.BuilderListener";
    BuilderListener["__interfaces"] = ["org.noear.socketd.transport.core.Listener"];


}

