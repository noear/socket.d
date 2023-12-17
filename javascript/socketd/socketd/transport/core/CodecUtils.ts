
export class CodecUtils{
    static strToBuf(str:string){
        const encoder = new TextEncoder(); // 使用 UTF-8 编码器进行编码
        const dataView = encoder.encode(str); // 将字符串编码成 Uint8Array 数组
        return dataView;
    }
}