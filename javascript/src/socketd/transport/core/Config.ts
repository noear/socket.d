/**
 * 配置类
 *
 * @author noear
 * @since 2.0
 */
export interface Config {
    /**
     * 流ID大小限制
     */
    MAX_SIZE_SID: 64
    /**
     * 主题大小限制
     */
    MAX_SIZE_TOPIC: 512
    /**
     * 元信息串大小限制
     */
    MAX_SIZE_META_STRING: 4096
    /**
     * 分片大小限制
     */
    MAX_SIZE_FRAGMENT: 16777216 //16m

    /**
     * 是否客户端模式
     */
    clientMode(): boolean

    /**
     * 获取读缓冲大小
     */
    getReadBufferSize(): number

    /**
     * 配置读缓冲大小
     */
    getWriteBufferSize(): number

    /**
     * 获取连接空闲超时
     * */
    getIdleTimeout(): bigint

    /**
     * 请求超时（单位：毫秒）
     */
    getRequestTimeout(): bigint;

    /**
     * 允许最大请求数
     */
    getMaxRequests(): number
}