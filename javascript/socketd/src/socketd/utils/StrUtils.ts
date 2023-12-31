export class StrUtils {
    static guid(): string {
        let guid = "";
        for (let i = 1; i <= 32; i++) {
            const n = Math.floor(Math.random() * 16.0).toString(16);
            guid += n;
        }
        return guid;
    }

    static strToBuf(str: string, charet?: string): ArrayBuffer {
        if (!charet) {
            charet = 'utf-8';
        }

        const encoder = new TextEncoder(); // 使用 UTF-8 编码器进行编码
        return encoder.encode(str).buffer; // 将字符串编码成 ArrayBuffer,
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

        const decoder = new TextDecoder(charet)
        return decoder.decode(buf);
    }
}
