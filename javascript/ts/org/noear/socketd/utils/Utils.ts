/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.utils {
    /**
     * 工具
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export class Utils {
        public static isEmpty$java_lang_String(s: string): boolean {
            return s == null || s.length === 0;
        }

        /**
         * 检查字符串是否为空
         * 
         * @param {string} s 字符串
         * @return {boolean}
         */
        public static isEmpty(s?: any): boolean {
            if (((typeof s === 'string') || s === null)) {
                return <any>org.noear.socketd.utils.Utils.isEmpty$java_lang_String(s);
            } else if (((s != null && (s instanceof Array)) || s === null)) {
                return <any>org.noear.socketd.utils.Utils.isEmpty$java_util_Collection(s);
            } else if (((s != null && (s instanceof Object)) || s === null)) {
                return <any>org.noear.socketd.utils.Utils.isEmpty$java_util_Map(s);
            } else throw new Error('invalid overload');
        }

        public static isEmpty$java_util_Collection(s: Array<any>): boolean {
            return s == null || /* size */(<number>s.length) === 0;
        }

        public static isEmpty$java_util_Map(s: any): boolean {
            return s == null || /* size */((m) => { if(m.entries==null) m.entries=[]; return m.entries.length; })(<any>s) === 0;
        }

        public static isNotEmpty$java_lang_String(s: string): boolean {
            return !Utils.isEmpty$java_lang_String(s);
        }

        /**
         * 检查字符串是否为非空
         * 
         * @param {string} s 字符串
         * @return {boolean}
         */
        public static isNotEmpty(s?: any): boolean {
            if (((typeof s === 'string') || s === null)) {
                return <any>org.noear.socketd.utils.Utils.isNotEmpty$java_lang_String(s);
            } else if (((s != null && (s instanceof Array)) || s === null)) {
                return <any>org.noear.socketd.utils.Utils.isNotEmpty$java_util_Collection(s);
            } else throw new Error('invalid overload');
        }

        public static isNotEmpty$java_util_Collection(s: Array<any>): boolean {
            return !Utils.isEmpty$java_util_Collection(s);
        }

        /**
         * 解包异常
         * 
         * @param {Error} ex 异常
         * @return {Error}
         */
        public static throwableUnwrap(ex: Error): Error {
            let th: Error = ex;
            while((true)) {{
                if (th != null && (th["__classes"] && th["__classes"].indexOf("java.lang.reflect.InvocationTargetException") >= 0)){
                    th = (<Error>th).getTargetException();
                } else if (th != null && (th["__classes"] && th["__classes"].indexOf("java.lang.reflect.UndeclaredThrowableException") >= 0)){
                    th = (<Error>th).getUndeclaredThrowable();
                } else if ((<any>th.constructor) === "java.lang.RuntimeException"){
                    if ((<Error>null) != null){
                        th = (<Error>null);
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }};
            return th;
        }
    }
    Utils["__class"] = "org.noear.socketd.utils.Utils";

}

