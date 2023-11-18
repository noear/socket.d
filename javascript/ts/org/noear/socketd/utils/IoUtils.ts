/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.utils {
    /**
     * 输入输出工具
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export class IoUtils {
        public static transferToString$java_io_InputStream(ins: { str: string, cursor: number }): string {
            return IoUtils.transferToString$java_io_InputStream$java_lang_String(ins, "UTF-8");
        }

        public static transferToString$java_io_InputStream$java_lang_String(ins: { str: string, cursor: number }, charset: string): string {
            if (ins == null){
                return null;
            }
            const outs: java.io.ByteArrayOutputStream = <any>(IoUtils.transferTo(ins, new java.io.ByteArrayOutputStream()));
            if (org.noear.socketd.utils.Utils.isEmpty$java_lang_String(charset)){
                return outs.toString();
            } else {
                return outs.toString(charset);
            }
        }

        /**
         * 将输入流转换为字符串
         * 
         * @param {{ str: string, cursor: number }} ins     输入流
         * @param {string} charset 字符集
         * @return {string}
         */
        public static transferToString(ins?: any, charset?: any): string {
            if (((ins != null && (ins instanceof Object)) || ins === null) && ((typeof charset === 'string') || charset === null)) {
                return <any>org.noear.socketd.utils.IoUtils.transferToString$java_io_InputStream$java_lang_String(ins, charset);
            } else if (((ins != null && (ins instanceof Object)) || ins === null) && charset === undefined) {
                return <any>org.noear.socketd.utils.IoUtils.transferToString$java_io_InputStream(ins);
            } else throw new Error('invalid overload');
        }

        /**
         * 将输入流转换为byte数组
         * 
         * @param {{ str: string, cursor: number }} ins 输入流
         * @return {byte[]}
         */
        public static transferToBytes(ins: { str: string, cursor: number }): number[] {
            if (ins == null){
                return null;
            }
            return IoUtils.transferTo(ins, new java.io.ByteArrayOutputStream()).toByteArray();
        }

        public static transferTo$java_io_InputStream$java_io_OutputStream<T extends java.io.OutputStream>(ins: { str: string, cursor: number }, out: T): T {
            if (ins == null || out == null){
                return null;
            }
            let len: number = 0;
            const buf: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(512);
            while(((len = /* read */(r => r.str.charCodeAt(r.cursor++))(ins)) !== -1)) {{
                out.write(buf, 0, len);
            }};
            return out;
        }

        public static transferTo$java_io_InputStream$java_io_OutputStream$long$long<T extends java.io.OutputStream>(ins: { str: string, cursor: number }, out: T, start: number, length: number): T {
            let len: number = 0;
            const buf: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(512);
            let bufMax: number = buf.length;
            if (length < bufMax){
                bufMax = (<number>length|0);
            }
            if (start > 0){
                /* skip */ins.cursor+=start;
            }
            while(((len = /* read */(r => r.str.charCodeAt(r.cursor++))(ins)) !== -1)) {{
                out.write(buf, 0, len);
                length -= len;
                if (bufMax > length){
                    bufMax = (<number>length|0);
                    if (bufMax === 0){
                        break;
                    }
                }
            }};
            return out;
        }

        /**
         * 将输入流转换为输出流
         * 
         * @param {{ str: string, cursor: number }} ins    输入流
         * @param {java.io.OutputStream} out    输出流
         * @param {number} start  开始位
         * @param {number} length 长度
         * @return {java.io.OutputStream}
         */
        public static transferTo<T0 = any>(ins?: any, out?: any, start?: any, length?: any): any {
            if (((ins != null && (ins instanceof Object)) || ins === null) && ((out != null) || out === null) && ((typeof start === 'number') || start === null) && ((typeof length === 'number') || length === null)) {
                return <any>org.noear.socketd.utils.IoUtils.transferTo$java_io_InputStream$java_io_OutputStream$long$long(ins, out, start, length);
            } else if (((ins != null && (ins instanceof Object)) || ins === null) && ((out != null) || out === null) && start === undefined && length === undefined) {
                return <any>org.noear.socketd.utils.IoUtils.transferTo$java_io_InputStream$java_io_OutputStream(ins, out);
            } else throw new Error('invalid overload');
        }

        /**
         * 将输入流转换为输出流
         * 
         * @param {{ str: string, cursor: number }} ins 输入流
         * @param {*} out 输出流
         */
        public static writeTo(ins: { str: string, cursor: number }, out: org.noear.socketd.transport.core.buffer.BufferWriter) {
            if (ins == null || out == null){
                return;
            }
            let len: number = 0;
            const buf: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(512);
            while(((len = /* read */(r => r.str.charCodeAt(r.cursor++))(ins)) !== -1)) {{
                out['putBytes$byte_A$int$int'](buf, 0, len);
            }};
        }
    }
    IoUtils["__class"] = "org.noear.socketd.utils.IoUtils";

}

