/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core.entity {
    /**
     * 实体默认实现
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export class EntityDefault implements org.noear.socketd.transport.core.Entity {
        /*private*/ __metaMap: any;

        /*private*/ __metaString: string;

        /*private*/ metaStringChanged: boolean;

        /*private*/ __data: { str: string, cursor: number }

        /*private*/ __dataSize: number;

        public metaString$java_lang_String(metaString: string): EntityDefault {
            this.__metaMap = null;
            this.__metaString = metaString;
            this.metaStringChanged = false;
            return this;
        }

        public metaString(metaString?: any): any {
            if (((typeof metaString === 'string') || metaString === null)) {
                return <any>this.metaString$java_lang_String(metaString);
            } else if (metaString === undefined) {
                return <any>this.metaString$();
            } else throw new Error('invalid overload');
        }

        public metaString$(): string {
            if (this.metaStringChanged){
                const buf: { str: string, toString: Function } = { str: "", toString: function() { return this.str; } };
                this.metaMap$().forEach(((buf) => {
                    return (name, val) => {
                        /* append */(sb => { sb.str += <any>"&"; return sb; })(/* append */(sb => { sb.str += <any>val; return sb; })(/* append */(sb => { sb.str += <any>"="; return sb; })(/* append */(sb => { sb.str += <any>name; return sb; })(buf))));
                    }
                })(buf));
                if (/* length */buf.str.length > 0){
                    /* setLength */((sb, length) => sb.str = sb.str.substring(0, length))(buf, /* length */buf.str.length - 1);
                }
                this.__metaString = /* toString */buf.str;
                this.metaStringChanged = false;
            }
            return this.__metaString;
        }

        public metaMap$java_util_Map(metaMap: any): EntityDefault {
            this.__metaMap = metaMap;
            this.__metaString = null;
            this.metaStringChanged = true;
            return this;
        }

        /**
         * 设置元信息字典
         * 
         * @param {*} metaMap 元信息字典
         * @return {org.noear.socketd.transport.core.entity.EntityDefault}
         */
        public metaMap(metaMap?: any): any {
            if (((metaMap != null && (metaMap instanceof Object)) || metaMap === null)) {
                return <any>this.metaMap$java_util_Map(metaMap);
            } else if (metaMap === undefined) {
                return <any>this.metaMap$();
            } else throw new Error('invalid overload');
        }

        public metaMap$(): any {
            if (this.__metaMap == null){
                this.__metaMap = <any>({});
                this.metaStringChanged = false;
                if (org.noear.socketd.utils.Utils.isNotEmpty$java_lang_String(this.__metaString)){
                    {
                        let array = this.__metaString.split("&");
                        for(let index = 0; index < array.length; index++) {
                            let kvStr = array[index];
                            {
                                const idx: number = kvStr.indexOf('=');
                                if (idx > 0){
                                    /* put */(this.__metaMap[kvStr.substring(0, idx)] = kvStr.substring(idx + 1));
                                }
                            }
                        }
                    }
                }
            }
            return this.__metaMap;
        }

        public meta$java_lang_String$java_lang_String(name: string, val: string): EntityDefault {
            /* put */(this.metaMap$()[name] = val);
            this.metaStringChanged = true;
            return this;
        }

        /**
         * 设置元信息
         * 
         * @param {string} name 名字
         * @param {string} val  值
         * @return {org.noear.socketd.transport.core.entity.EntityDefault}
         */
        public meta(name?: any, val?: any): any {
            if (((typeof name === 'string') || name === null) && ((typeof val === 'string') || val === null)) {
                return <any>this.meta$java_lang_String$java_lang_String(name, val);
            } else if (((typeof name === 'string') || name === null) && val === undefined) {
                return <any>this.meta$java_lang_String(name);
            } else throw new Error('invalid overload');
        }

        public meta$java_lang_String(name: string): string {
            return /* get */((m,k) => m[k]===undefined?null:m[k])(this.metaMap$(), name);
        }

        /**
         * 获取元信息或默认值
         * 
         * @param {string} name 名字
         * @param {string} def  默认值
         * @return {string}
         */
        public metaOrDefault(name: string, def: string): string {
            return this.metaMap$().getOrDefault(name, def);
        }

        public data$byte_A(data: number[]): EntityDefault {
            this.__data = new java.io.ByteArrayInputStream(data);
            this.__dataSize = data.length;
            return this;
        }

        /**
         * 设置数据
         * 
         * @param {byte[]} data 数据
         * @return {org.noear.socketd.transport.core.entity.EntityDefault}
         */
        public data(data?: any): any {
            if (((data != null && data instanceof <any>Array && (data.length == 0 || data[0] == null ||(typeof data[0] === 'number'))) || data === null)) {
                return <any>this.data$byte_A(data);
            } else if (((data != null && (data instanceof Object)) || data === null)) {
                return <any>this.data$java_io_InputStream(data);
            } else if (data === undefined) {
                return <any>this.data$();
            } else throw new Error('invalid overload');
        }

        public data$java_io_InputStream(data: { str: string, cursor: number }): EntityDefault {
            this.__data = data;
            this.__dataSize = data.available();
            this.meta$java_lang_String$java_lang_String(org.noear.socketd.transport.core.EntityMetas.META_DATA_LENGTH, /* valueOf */String(this.__dataSize).toString());
            return this;
        }

        public data$(): { str: string, cursor: number } {
            return this.__data;
        }

        /**
         * 获取数据并转成字符串
         * @return {string}
         */
        public dataAsString(): string {
            try {
                if (this.__dataAsString == null){
                    this.__dataAsString = org.noear.socketd.utils.IoUtils.transferToString$java_io_InputStream(this.data$());
                }
                return this.__dataAsString;
            } catch(e) {
                throw new org.noear.socketd.exception.SocketdCodecException(e);
            }
        }

        /*private*/ __dataAsString: string;

        /**
         * 
         * @return {byte[]}
         */
        public dataAsBytes(): number[] {
            try {
                return org.noear.socketd.utils.IoUtils.transferToBytes(this.data$());
            } catch(e) {
                throw new org.noear.socketd.exception.SocketdCodecException(e);
            }
        }

        /**
         * 获取数据长度
         * @return {number}
         */
        public dataSize(): number {
            return this.__dataSize;
        }

        /**
         * 
         * @return {string}
         */
        public toString(): string {
            return "Entity{meta=\'" + this.metaString$() + '\'' + ", data=byte[" + this.__dataSize + ']' + '}';
        }

        constructor() {
            if (this.__metaMap === undefined) { this.__metaMap = null; }
            this.__metaString = org.noear.socketd.transport.core.Constants.DEF_META_STRING;
            this.metaStringChanged = false;
            this.__data = org.noear.socketd.transport.core.Constants.DEF_DATA;
            this.__dataSize = 0;
            if (this.__dataAsString === undefined) { this.__dataAsString = null; }
        }
    }
    EntityDefault["__class"] = "org.noear.socketd.transport.core.entity.EntityDefault";
    EntityDefault["__interfaces"] = ["org.noear.socketd.transport.core.Entity"];


}

