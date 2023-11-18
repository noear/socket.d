/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core.listener {
    /**
     * 路由监听器（根据握手地址路由，一般用于服务端）
     * 
     * @author noear
     * @since 2.0
     * @param {*} router
     * @class
     */
    export class RouterListener implements org.noear.socketd.transport.core.Listener {
        router: org.noear.socketd.transport.core.listener.Router;

        public constructor(router?: any) {
            if (((router != null && (router.constructor != null && router.constructor["__interfaces"] != null && router.constructor["__interfaces"].indexOf("org.noear.socketd.transport.core.listener.Router") >= 0)) || router === null)) {
                let __args = arguments;
                if (this.router === undefined) { this.router = null; } 
                this.router = router;
            } else if (router === undefined) {
                let __args = arguments;
                if (this.router === undefined) { this.router = null; } 
                this.router = new org.noear.socketd.transport.core.listener.RouterHashMap();
            } else throw new Error('invalid overload');
        }

        public of$java_lang_String$org_noear_socketd_transport_core_Listener(path: string, listener: org.noear.socketd.transport.core.Listener): RouterListener {
            this.router.add(path, listener);
            return this;
        }

        /**
         * 路由
         * @param {string} path
         * @param {*} listener
         * @return {org.noear.socketd.transport.core.listener.RouterListener}
         */
        public of(path?: any, listener?: any): any {
            if (((typeof path === 'string') || path === null) && ((listener != null && (listener.constructor != null && listener.constructor["__interfaces"] != null && listener.constructor["__interfaces"].indexOf("org.noear.socketd.transport.core.Listener") >= 0)) || listener === null)) {
                return <any>this.of$java_lang_String$org_noear_socketd_transport_core_Listener(path, listener);
            } else if (((typeof path === 'string') || path === null) && listener === undefined) {
                return <any>this.of$java_lang_String(path);
            } else throw new Error('invalid overload');
        }

        public of$java_lang_String(path: string): org.noear.socketd.transport.core.listener.BuilderListener {
            const l1: org.noear.socketd.transport.core.listener.BuilderListener = new org.noear.socketd.transport.core.listener.BuilderListener();
            this.router.add(path, l1);
            return l1;
        }

        /**
         * 数量
         * @return {number}
         */
        public count(): number {
            return this.router.count();
        }

        /**
         * 
         * @param {*} session
         */
        public onOpen(session: org.noear.socketd.transport.core.Session) {
            const l1: org.noear.socketd.transport.core.Listener = this.router.matching(session.path());
            if (l1 != null){
                l1.onOpen(session);
            }
        }

        /**
         * 
         * @param {*} session
         * @param {*} message
         */
        public onMessage(session: org.noear.socketd.transport.core.Session, message: org.noear.socketd.transport.core.Message) {
            const l1: org.noear.socketd.transport.core.Listener = this.router.matching(session.path());
            if (l1 != null){
                l1.onMessage(session, message);
            }
        }

        /**
         * 
         * @param {*} session
         */
        public onClose(session: org.noear.socketd.transport.core.Session) {
            const l1: org.noear.socketd.transport.core.Listener = this.router.matching(session.path());
            if (l1 != null){
                l1.onClose(session);
            }
        }

        /**
         * 
         * @param {*} session
         * @param {Error} error
         */
        public onError(session: org.noear.socketd.transport.core.Session, error: Error) {
            const l1: org.noear.socketd.transport.core.Listener = this.router.matching(session.path());
            if (l1 != null){
                l1.onError(session, error);
            }
        }
    }
    RouterListener["__class"] = "org.noear.socketd.transport.core.listener.RouterListener";
    RouterListener["__interfaces"] = ["org.noear.socketd.transport.core.Listener"];


}

