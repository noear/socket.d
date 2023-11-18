/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core {
    /**
     * 常量
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export interface Constants {    }

    export namespace Constants {

        export const DEF_SID: string = "";

        export const DEF_TOPIC: string = "";

        export const DEF_META_STRING: string = "";

        export const DEF_DATA: { str: string, cursor: number } = new java.io.ByteArrayInputStream([]);

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
    }

}

