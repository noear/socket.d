/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core {
    /**
     * 会话基类
     * 
     * @author noear
     * @param {*} channel
     * @class
     */
    export abstract class SessionBase implements org.noear.socketd.transport.core.Session {
        channel: org.noear.socketd.transport.core.Channel;

        /*private*/ __sessionId: string;

        public constructor(channel: org.noear.socketd.transport.core.Channel) {
            if (this.channel === undefined) { this.channel = null; }
            if (this.__sessionId === undefined) { this.__sessionId = null; }
            if (this.__attrMap === undefined) { this.__attrMap = null; }
            this.channel = channel;
            this.__sessionId = this.generateId();
        }

        /**
         * 会话的附件与通道的各自独立
         */
        /*private*/ __attrMap: any;

        /**
         * 
         * @return {*}
         */
        public attrMap(): any {
            if (this.__attrMap == null){
                this.__attrMap = <any>({});
            }
            return this.__attrMap;
        }

        public attr$java_lang_String<T>(name: string): T {
            if (this.__attrMap == null){
                return null;
            }
            return <T><any>/* get */((m,k) => m[k]===undefined?null:m[k])(this.__attrMap, name);
        }

        /**
         * 获取属性或默认值
         * 
         * @param {string} name 名字
         * @param {*} def  默认值
         * @return {*}
         */
        public attrOrDefault<T>(name: string, def: T): T {
            const tmp: T = <any>(this.attr$java_lang_String(name));
            if (tmp == null){
                return def;
            } else {
                return tmp;
            }
        }

        public attr$java_lang_String$java_lang_Object<T>(name: string, value: T) {
            if (this.__attrMap == null){
                this.__attrMap = <any>({});
            }
            /* put */(this.__attrMap[name] = value);
        }

        /**
         * 设置附件
         * @param {string} name
         * @param {*} value
         */
        public attr<T0 = any>(name?: any, value?: any) {
            if (((typeof name === 'string') || name === null) && ((value != null) || value === null)) {
                return <any>this.attr$java_lang_String$java_lang_Object(name, value);
            } else if (((typeof name === 'string') || name === null) && value === undefined) {
                return <any>this.attr$java_lang_String(name);
            } else throw new Error('invalid overload');
        }

        /**
         * 
         * @return {string}
         */
        public sessionId(): string {
            return this.__sessionId;
        }

        /**
         * 
         * @param {*} o
         * @return {boolean}
         */
        public equals(o: any): boolean {
            if (this === o)return true;
            if (!(o != null && (o.constructor != null && o.constructor["__interfaces"] != null && o.constructor["__interfaces"].indexOf("org.noear.socketd.transport.core.Session") >= 0)))return false;
            const that: org.noear.socketd.transport.core.Session = <org.noear.socketd.transport.core.Session><any>o;
            return java.util.Objects.equals(this.sessionId(), that.sessionId());
        }

        /**
         * 
         * @return {number}
         */
        public hashCode(): number {
            return /* hash */0;
        }

        generateId(): string {
            return this.channel.getConfig().getIdGenerator().generate();
        }

        public abstract close(): any;
        public abstract handshake(): any;
        public abstract isValid(): any;
        public abstract localAddress(): any;
        public abstract param(name?: any): any;
        public abstract paramOrDefault(name?: any, def?: any): any;
        public abstract path(): any;
        public abstract pathNew(pathNew?: any): any;
        public abstract reconnect(): any;
        public abstract remoteAddress(): any;
        public abstract reply(from?: any, content?: any): any;
        public abstract replyEnd(from?: any, content?: any): any;
        public abstract send(topic?: any, content?: any): any;
        public abstract sendAndRequest(topic?: any, content?: any): any;
        public abstract sendAndRequest(topic?: any, content?: any, timeout?: any): any;
        public abstract sendAndSubscribe(topic?: any, content?: any, consumer?: any): any;
        public abstract sendPing(): any;    }
    SessionBase["__class"] = "org.noear.socketd.transport.core.SessionBase";
    SessionBase["__interfaces"] = ["org.noear.socketd.transport.core.Session"];


}

