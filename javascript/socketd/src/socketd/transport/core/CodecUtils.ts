
export class CodecUtils {
    static strToBuf(str: string): ArrayBuffer {
        const encoder = new TextEncoder(); // 使用 UTF-8 编码器进行编码
        return encoder.encode(str); // 将字符串编码成 Uint8Array 数组
    }

    static bufToStr(buf: ArrayBuffer, start: number, length: number): string {
        const decoder = new TextDecoder()
        return decoder.decode(buf);
    }
}