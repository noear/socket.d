import type {MessageInternal} from "./Message";

/**
 * 握手信息
 *
 * @author noear
 * @since 2.0
 */
export interface Handshake {
    /**
     * 协议版本
     */
    version(): string | null;

    /**
     * 获请传输地址
     *
     * @return tcp://192.168.0.1/path?user=1&path=2
     */
    uri(): string;

    /**
     * 获取路径
     * */
    path(): string;

    /**
     * 获取参数集合
     */
    paramMap(): Map<string, string>

    /**
     * 获取参数
     *
     * @param name 参数名
     */
    param(name: string): string | null;

    /**
     * 获取参数或默认值
     *
     * @param name 参数名
     * @param def  默认值
     */
    paramOrDefault(name: string, def: string): string;

    /**
     * 设置或修改参数
     */
    paramPut(name: string, value: string);

    /**
     * 输出元信息
     */
    outMeta(name: string, value: string);
}


/**
 * @author noear
 * @since 2.0
 */
export interface HandshakeInternal extends Handshake {
    /**
     * 获取消息源
     */
    getSource(): MessageInternal;

    /**
     * 获取输出元信息
     */
    getOutMetaMap(): Map<string, string>;
}
