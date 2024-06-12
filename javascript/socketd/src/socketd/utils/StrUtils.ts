
/**
 * 字会串工具
 *
 * @author noear
 * @since 2.0
 */
export class StrUtils {
    static guid(): string {
        let guid = "";
        for (let i = 1; i <= 32; i++) {
            const n = Math.floor(Math.random() * 16.0).toString(16);
            guid += n;
        }
        return guid;
    }

    private static parseUriOptions = {
        strictMode: false,
        key: ["source", "protocol", "authority", "userInfo", "user", "password", "host", "port", "relative", "path", "directory", "file", "query", "anchor"],
        q: {
            name: "queryKey",
            parser: /(?:^|&)([^&=]*)=?([^&]*)/g
        },
        parser: {
            strict: /^(?:([^:\/?#]+):)?(?:\/\/((?:(([^:@]*)(?::([^:@]*))?)?@)?([^:\/?#]*)(?::(\d*))?))?((((?:[^?#\/]*\/)*)([^?#]*))(?:\?([^#]*))?(?:#(.*))?)/,
            loose: /^(?:(?![^:@]+:[^:@\/]*@)([^:\/?#.]+):)?(?:\/\/)?((?:(([^:@]*)(?::([^:@]*))?)?@)?([^:\/?#]*)(?::(\d*))?)(((\/(?:[^?#](?![^?#\/]*\.[^?#\/.]+(?:[?#]|$)))*\/?)?([^?#\/]*))(?:\?([^#]*))?(?:#(.*))?)/
        }
    };

    static parseUri(str): any {
        if (!str) {
            return '';
        }

        let idx = str.indexOf("?");
        if (idx > 0) {
            let uri0Str = str.substring(0, idx);
            let uri1Str = str.substring(idx, str.length);

            let uri0 = StrUtils.parseUriDo(uri0Str);

            uri0.source = str;
            uri0.query = uri1Str.substring(1, uri1Str.length);
            uri0.relative = uri1Str;

            return uri0;
        } else {
            return StrUtils.parseUriDo(str);
        }
    }

    static parseUriDo(str): any {
        if (!str) {
            return '';
        }

        let o = StrUtils.parseUriOptions,
            m = o.parser[o.strictMode ? "strict" : "loose"].exec(str),
            uri = {},
            i = 14;

        while (i--) { // @ts-ignore
            uri[o.key[i]] = m[i] || "";
        }

        uri[o.q.name] = {};
        uri[o.key[12]].replace(o.q.parser, function ($0, $1, $2) {
            if ($1) uri[o.q.name][$1] = $2;
        });

        return uri;
    }

    static strToBuf(str: string, charet?: string): ArrayBuffer {
        if (!charet) {
            charet = 'utf-8';
        }

        if (typeof TextEncoder === "undefined") {
            //能兼容没有 TextEncoder 接口的环境
            let data = unescape(encodeURIComponent(str))
                .split('')
                .map(val => val.charCodeAt(0));
            return new Uint8Array(data).buffer;
        } else {
            //能处理更大的字符串
            const encoder = new TextEncoder(); // 使用 UTF-8 编码器进行编码
            return encoder.encode(str).buffer; // 将字符串编码成 ArrayBuffer,
        }
    }

    static bufToStr(buf: ArrayBuffer, start: number, length: number, charet?: string): string {


        if (buf.byteLength != length) {
            //取出子集
            const bufView = new DataView(buf);

            const tmp = new ArrayBuffer(length);
            const tmpView = new DataView(tmp);

            for (let i = 0; i < length; i++) {
                tmpView.setInt8(i, bufView.getInt8(start + i));
            }

            buf = tmp;
        }

        return StrUtils.bufToStrDo(buf, charet);
    }

    static bufToStrDo(buf: ArrayBuffer, charet?: string): string {
        if (!charet) {
            charet = 'utf-8';
        }

        if (typeof TextDecoder === "undefined") {
            //能兼容没有 TextDecoder 接口的环境
            // @ts-ignore
            return decodeURIComponent(escape(String.fromCharCode.apply(null, new Uint8Array(buf))));
        } else {
            //能处理更大的字符串
            const decoder = new TextDecoder(charet)
            return decoder.decode(buf);
        }
    }
}