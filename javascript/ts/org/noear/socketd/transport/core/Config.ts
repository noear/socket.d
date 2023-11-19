/**
 * 配置类
 *
 * @author noear
 * @since 2.0
 * @class
 */
export interface Config {
    /**
     * 是否客户端模式
     * @return {boolean}
     */
    clientMode(): boolean;

    /**
     * 获取字符集
     * @return {string}
     */
    getCharset(): string;

    /**
     * 获取编解码器
     * @return {*}
     */
    //getCodec(): org.noear.socketd.transport.core.Codec<org.noear.socketd.transport.core.buffer.BufferReader, org.noear.socketd.transport.core.buffer.BufferWriter>;

    /**
     * 获取Id生成器
     * @return {*}
     */
    //getIdGenerator(): org.noear.socketd.transport.core.IdGenerator;

    /**
     * 获取分片处理器
     * @return {*}
     */
    //getFragmentHandler(): org.noear.socketd.transport.core.FragmentHandler;

    /**
     * 获取 ssl 上下文
     * @return {javax.net.ssl.SSLContext}
     */
    //getSslContext(): javax.net.ssl.SSLContext;

    /**
     * 通道执行器
     *
     * @return {*}
     */
    //getChannelExecutor(): java.util.concurrent.ExecutorService;

    /**
     * 核心线程数（第二优先）
     * @return {number}
     */
    getCoreThreads(): number;

    /**
     * 最大线程数
     * @return {number}
     */
    getMaxThreads(): number;

    /**
     * 获取读缓冲大小
     * @return {number}
     */
    getReadBufferSize(): number;

    /**
     * 配置读缓冲大小
     * @return {number}
     */
    getWriteBufferSize(): number;

    /**
     * 获取连接空闲超时
     *
     * @return {number}
     */
    getIdleTimeout(): number;

    /**
     * 请求超时（单位：毫秒）
     * @return {number}
     */
    getRequestTimeout(): number;

    /**
     * 允许最大请求数
     * @return {number}
     */
    getMaxRequests(): number;

    /**
     * 允许最大UDP包大小
     * @return {number}
     */
    getMaxUdpSize(): number;
}