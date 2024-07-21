import type {Codec} from "./Codec";
import {FragmentHandler} from "./FragmentHandler";
import {StreamManger} from "../stream/StreamManger";

/**
 * 配置接口
 *
 * @author noear
 * @since 2.0
 */
export interface Config {

    /**
     * 是否客户端模式
     */
    clientMode(): boolean;

    /**
     * 获取流管理器
     */
    getStreamManger(): StreamManger;

    /**
     * 获取角色名
     */
    getRoleName(): string;

    /**
     * 获取字符集
     */
    getCharset(): string;

    /**
     * 获取编解码器
     */
    getCodec(): Codec;

    /**
     * 获取Id生成器
     */
    genId(): string;

    /**
     * 获取分片处理器
     */
    getFragmentHandler(): FragmentHandler;

    /**
     * 获取分片大小
     */
    getFragmentSize(): number;

    /**
     * Io线程数
     */
    getIoThreads(): number;

    /**
     * 解码线程数
     */
    getCodecThreads(): number;

    /**
     * 交换线程数
     */
    getExchangeThreads(): number;

    /**
     * 获取读缓冲大小
     */
    getReadBufferSize(): number;

    /**
     * 配置读缓冲大小
     */
    getWriteBufferSize(): number;

    /**
     * 获取连接空闲超时（单位：毫秒）
     */
    getIdleTimeout(): number;

    /**
     * 获取请求超时（单位：毫秒）
     */
    getRequestTimeout(): number;

    /**
     * 获取消息流超时（单位：毫秒）
     */
    getStreamTimeout(): number;

    /**
     * 允许最大UDP包大小
     */
    getMaxUdpSize(): number;

    /**
     * 是否使用子协议
     * */
    isUseSubprotocols(): boolean;
}