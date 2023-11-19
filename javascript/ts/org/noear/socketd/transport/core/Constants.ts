
/**
 * 常量
 *
 * @author noear
 * @since 2.0
 * @class
 */

export namespace Constants {
    /**
     * 默认流id（占位）
     */
    export const DEF_SID: string = "";
    /**
     * 默认主题（占位）
     */
    export const DEF_TOPIC: string = "";
    /**
     * 默认元信息字符串（占位）
     */
    export const DEF_META_STRING: string = "";
    /**
     * 默认数据（占位）
     */
    export const DEF_DATA: ArrayBuffer = new ArrayBuffer(0);

    /**
     * 因协议关闭
     */
    export const CLOSE1_PROTOCOL: number = 1;

    /**
     * 因异常关闭
     */
    export const CLOSE2_ERROR: number = 2;

    /**
     * 因用户主动关闭
     */
    export const CLOSE3_USER: number = 3;


    /**
     * 流ID大小限制
     */
    export const MAX_SIZE_SID: number = 64;

    /**
     * 主题大小限制
     */
    export const MAX_SIZE_TOPIC: number = 512;

    /**
     * 元信息串大小限制
     */
    export const MAX_SIZE_META_STRING: number = 4096;

    /**
     * 分片大小限制
     */
    export const MAX_SIZE_FRAGMENT: number = 1024 * 1024 * 16;
}
