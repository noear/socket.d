import {Frame} from "./Frame";

/**
 * 编解码器
 *
 * @author noear
 * @since 2.0
 * @class
 */
export interface Codec<In, Out> {
    /**
     * 编码
     * @param {*} buffer
     * @return {org.noear.socketd.transport.core.Frame}
     */
    read(buffer: In): Frame;
}