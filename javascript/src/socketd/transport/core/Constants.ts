/**
 * 常量
 *
 * @author noear
 * @since 2.0
 */
export class Constants {
    static DEF_SID: string = ""
    static DEF_TOPIC: string = ""
    static DEF_META_STRING: string = ""
    static DEF_DATA: ArrayBuffer = new ArrayBuffer(0)

    /**
     * 因协议关闭
     */
    static CLOSE1_PROTOCOL: number = 1;
    /**
     * 因异常关闭
     */
    static CLOSE2_ERROR: number = 2;
    /**
     * 因用户主动关闭
     */
    static CLOSE3_USER: number = 3;
}