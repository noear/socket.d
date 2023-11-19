/**
 * 握手信息
 *
 * @author noear
 * @since 2.0
 * @class
 */
export interface Handshake {
    /**
     * 协议版本
     * @return {string}
     */
    version(): string;

    /**
     * 获请传输地址
     *
     * @return {java.net.URI} tcp://192.168.0.1/path?user=1&path=2
     */
    uri(): URL;

    /**
     * 获取参数集合
     * @return {*}
     */
    paramMap(): any;

    /**
     * 获取参数或默认值
     *
     * @param {string} name 参数名
     * @param {string} def  默认值
     * @return {string}
     */
    paramOrDefault(name: string, def: string): string;

    /**
     * 设置或修改参数
     * @param {string} name
     * @param {string} value
     */
    param(name?: any, value?: any);
}