/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core.internal {
    /**
     * 消息默认实现（帧[消息[实体]]）
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export class MessageDefault implements org.noear.socketd.transport.core.Message {
        /*private*/ __sid: string;

        /*private*/ __topic: string;

        /*private*/ __entity: org.noear.socketd.transport.core.Entity;

        /*private*/ __flag: org.noear.socketd.transport.core.Flag;

        /**
         * 获取标记
         * @return {org.noear.socketd.transport.core.Flag}
         */
        public getFlag(): org.noear.socketd.transport.core.Flag {
            return this.__flag;
        }

        /**
         * 设置标记
         * @param {org.noear.socketd.transport.core.Flag} flag
         * @return {org.noear.socketd.transport.core.internal.MessageDefault}
         */
        public flag(flag: org.noear.socketd.transport.core.Flag): MessageDefault {
            this.__flag = flag;
            return this;
        }

        public sid$java_lang_String(sid: string): MessageDefault {
            this.__sid = sid;
            return this;
        }

        /**
         * 设置流id
         * @param {string} sid
         * @return {org.noear.socketd.transport.core.internal.MessageDefault}
         */
        public sid(sid?: any): any {
            if (((typeof sid === 'string') || sid === null)) {
                return <any>this.sid$java_lang_String(sid);
            } else if (sid === undefined) {
                return <any>this.sid$();
            } else throw new Error('invalid overload');
        }

        public topic$java_lang_String(topic: string): MessageDefault {
            this.__topic = topic;
            return this;
        }

        /**
         * 设置主题
         * @param {string} topic
         * @return {org.noear.socketd.transport.core.internal.MessageDefault}
         */
        public topic(topic?: any): any {
            if (((typeof topic === 'string') || topic === null)) {
                return <any>this.topic$java_lang_String(topic);
            } else if (topic === undefined) {
                return <any>this.topic$();
            } else throw new Error('invalid overload');
        }

        public entity$org_noear_socketd_transport_core_Entity(entity: org.noear.socketd.transport.core.Entity): MessageDefault {
            this.__entity = entity;
            return this;
        }

        /**
         * 设置实体
         * @param {*} entity
         * @return {org.noear.socketd.transport.core.internal.MessageDefault}
         */
        public entity(entity?: any): any {
            if (((entity != null && (entity.constructor != null && entity.constructor["__interfaces"] != null && entity.constructor["__interfaces"].indexOf("org.noear.socketd.transport.core.Entity") >= 0)) || entity === null)) {
                return <any>this.entity$org_noear_socketd_transport_core_Entity(entity);
            } else if (entity === undefined) {
                return <any>this.entity$();
            } else throw new Error('invalid overload');
        }

        /**
         * 是否为请求
         * @return {boolean}
         */
        public isRequest(): boolean {
            return this.__flag === org.noear.socketd.transport.core.Flag.Request;
        }

        /**
         * 是否为订阅
         * @return {boolean}
         */
        public isSubscribe(): boolean {
            return this.__flag === org.noear.socketd.transport.core.Flag.Subscribe;
        }

        public sid$(): string {
            return this.__sid;
        }

        public topic$(): string {
            return this.__topic;
        }

        public entity$(): org.noear.socketd.transport.core.Entity {
            return this.__entity;
        }

        /**
         * 
         * @return {string}
         */
        public toString(): string {
            return "Message{sid=\'" + this.__sid + '\'' + ", topic=\'" + this.__topic + '\'' + ", entity=" + this.__entity + '}';
        }

        /**
         * 
         * @return {string}
         */
        public metaString(): string {
            return this.__entity.metaString();
        }

        /**
         * 
         * @return {*}
         */
        public metaMap(): any {
            return this.__entity.metaMap();
        }

        /**
         * 
         * @param {string} name
         * @return {string}
         */
        public meta(name: string): string {
            return this.__entity.meta(name);
        }

        /**
         * 
         * @param {string} name
         * @param {string} def
         * @return {string}
         */
        public metaOrDefault(name: string, def: string): string {
            return this.__entity.metaOrDefault(name, def);
        }

        /**
         * 
         * @return {{ str: string, cursor: number }}
         */
        public data(): { str: string, cursor: number } {
            return this.__entity.data();
        }

        /**
         * 
         * @return {string}
         */
        public dataAsString(): string {
            return this.__entity.dataAsString();
        }

        /**
         * 
         * @return {byte[]}
         */
        public dataAsBytes(): number[] {
            return this.__entity.dataAsBytes();
        }

        /**
         * 
         * @return {number}
         */
        public dataSize(): number {
            return this.__entity.dataSize();
        }

        constructor() {
            this.__sid = org.noear.socketd.transport.core.Constants.DEF_SID;
            this.__topic = org.noear.socketd.transport.core.Constants.DEF_TOPIC;
            this.__entity = null;
            this.__flag = org.noear.socketd.transport.core.Flag.Unknown;
        }
    }
    MessageDefault["__class"] = "org.noear.socketd.transport.core.internal.MessageDefault";
    MessageDefault["__interfaces"] = ["org.noear.socketd.transport.core.Message","org.noear.socketd.transport.core.Entity"];


}

