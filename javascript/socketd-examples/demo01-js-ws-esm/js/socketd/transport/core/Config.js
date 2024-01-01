import { StreamMangerDefault } from "./Stream";
import { GuidGenerator } from "./IdGenerator";
import { FragmentHandlerDefault } from "./FragmentHandler";
import { Constants } from "./Constants";
import { Asserts } from "./Asserts";
import { CodecByteBuffer } from "./CodecByteBuffer";
export class ConfigBase {
    constructor(clientMode) {
        this._clientMode = clientMode;
        this._streamManger = new StreamMangerDefault(this);
        this._codec = new CodecByteBuffer(this);
        this._charset = "utf-8";
        this._idGenerator = new GuidGenerator();
        this._fragmentHandler = new FragmentHandlerDefault();
        this._fragmentSize = Constants.MAX_SIZE_DATA;
        this._coreThreads = 2;
        this._maxThreads = this._coreThreads * 4;
        this._readBufferSize = 512;
        this._writeBufferSize = 512;
        this._idleTimeout = 0; //默认不关（提供用户特殊场景选择）
        this._requestTimeout = 10000; //10秒（默认与连接超时同）
        this._streamTimeout = 1000 * 60 * 60 * 2; //2小时 //避免永不回调时，不能释放
        this._maxUdpSize = 2048; //2k //与 netty 保持一致 //实际可用 1464
    }
    /**
     * 是否客户端模式
     */
    clientMode() {
        return this._clientMode;
    }
    /**
     * 获取流管理器
     */
    getStreamManger() {
        return this._streamManger;
    }
    /**
     * 获取角色名
     * */
    getRoleName() {
        return this.clientMode() ? "Client" : "Server";
    }
    /**
     * 获取字符集
     */
    getCharset() {
        return this._charset;
    }
    /**
     * 配置字符集
     */
    charset(charset) {
        this._charset = charset;
        return this;
    }
    /**
     * 获取编解码器
     */
    getCodec() {
        return this._codec;
    }
    /**
     * 获取标识生成器
     */
    getIdGenerator() {
        return this._idGenerator;
    }
    /**
     * 配置标识生成器
     */
    idGenerator(idGenerator) {
        Asserts.assertNull("idGenerator", idGenerator);
        this._idGenerator = idGenerator;
        return this;
    }
    /**
     * 获取分片处理
     */
    getFragmentHandler() {
        return this._fragmentHandler;
    }
    /**
     * 配置分片处理
     */
    fragmentHandler(fragmentHandler) {
        Asserts.assertNull("fragmentHandler", fragmentHandler);
        this._fragmentHandler = fragmentHandler;
        return this;
    }
    /**
     * 获取分片大小
     */
    getFragmentSize() {
        return this._fragmentSize;
    }
    /**
     * 配置分片大小
     */
    fragmentSize(fragmentSize) {
        if (fragmentSize > Constants.MAX_SIZE_DATA) {
            throw new Error("The parameter fragmentSize cannot > 16m");
        }
        if (fragmentSize < Constants.MIN_FRAGMENT_SIZE) {
            throw new Error("The parameter fragmentSize cannot < 1k");
        }
        this._fragmentSize = fragmentSize;
        return this;
    }
    /**
     * 获取核心线程数
     */
    getCoreThreads() {
        return this._coreThreads;
    }
    /**
     * 配置核心线程数
     */
    coreThreads(coreThreads) {
        this._coreThreads = coreThreads;
        this._maxThreads = coreThreads * 4;
        return this;
    }
    /**
     * 获取最大线程数
     */
    getMaxThreads() {
        return this._maxThreads;
    }
    /**
     * 配置最大线程数
     */
    maxThreads(maxThreads) {
        this._maxThreads = maxThreads;
        return this;
    }
    /**
     * 获取读缓冲大小
     */
    getReadBufferSize() {
        return this._readBufferSize;
    }
    /**
     * 配置读缓冲大小
     */
    readBufferSize(readBufferSize) {
        this._readBufferSize = readBufferSize;
        return this;
    }
    /**
     * 获取写缓冲大小
     */
    getWriteBufferSize() {
        return this._writeBufferSize;
    }
    /**
     * 配置写缓冲大小
     */
    writeBufferSize(writeBufferSize) {
        this._writeBufferSize = writeBufferSize;
        return this;
    }
    /**
     * 配置连接空闲超时
     */
    getIdleTimeout() {
        return this._idleTimeout;
    }
    /**
     * 配置连接空闲超时
     */
    idleTimeout(idleTimeout) {
        this._idleTimeout = idleTimeout;
        return this;
    }
    /**
     * 配置请求默认超时
     */
    getRequestTimeout() {
        return this._requestTimeout;
    }
    /**
     * 配置请求默认超时
     */
    requestTimeout(requestTimeout) {
        this._requestTimeout = requestTimeout;
        return this;
    }
    /**
     * 获取消息流超时（单位：毫秒）
     * */
    getStreamTimeout() {
        return this._streamTimeout;
    }
    /**
     * 配置消息流超时（单位：毫秒）
     * */
    streamTimeout(streamTimeout) {
        this._streamTimeout = streamTimeout;
        return this;
    }
    /**
     * 获取允许最大UDP包大小
     */
    getMaxUdpSize() {
        return this._maxUdpSize;
    }
    /**
     * 配置允许最大UDP包大小
     */
    maxUdpSize(maxUdpSize) {
        this._maxUdpSize = maxUdpSize;
        return this;
    }
    /**
     * 生成 id
     * */
    generateId() {
        return this._idGenerator.generate();
    }
}
