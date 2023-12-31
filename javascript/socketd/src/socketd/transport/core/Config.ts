import type {Codec} from "./Codec";
import type {StreamManger} from "./Stream";
import {StreamMangerDefault} from "./Stream";
import {GuidGenerator, IdGenerator} from "./IdGenerator";
import {FragmentHandler, FragmentHandlerDefault} from "./FragmentHandler";
import {Constants} from "./Constants";
import {Asserts} from "./Asserts";
import {CodecByteBuffer} from "./CodecByteBuffer";

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
    getIdGenerator(): IdGenerator;

    /**
     * 获取分片处理器
     */
    getFragmentHandler(): FragmentHandler;

    /**
     * 获取分片大小
     */
    getFragmentSize(): number;

    /**
     * 核心线程数（第二优先）
     */
    getCoreThreads(): number;

    /**
     * 最大线程数
     */
    getMaxThreads(): number;

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
}

export abstract class ConfigBase implements Config {
    //是否客户端模式
    private _clientMode: boolean;
    //流管理器
    private _streamManger: StreamManger;
    //编解码器
    private _codec: Codec;

    //id生成器
    private _idGenerator: IdGenerator;
    //分片处理
    private _fragmentHandler: FragmentHandler;
    //分片大小
    private _fragmentSize: number;
    //字符集
    protected _charset: string
    //内核线程数
    protected _coreThreads: number;
    //最大线程数
    protected _maxThreads: number;
    //读缓冲大小
    protected _readBufferSize: number;
    //写缓冲大小
    protected _writeBufferSize: number;

    //连接空闲超时
    protected _idleTimeout: number;
    //请求默认超时
    protected _requestTimeout: number;
    //消息流超时（从发起到应答结束）
    protected _streamTimeout: number;
    //最大udp包大小
    protected _maxUdpSize: number;

    constructor(clientMode: boolean) {
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
        this._requestTimeout = 10_000; //10秒（默认与连接超时同）
        this._streamTimeout = 1000 * 60 * 60 * 2;//2小时 //避免永不回调时，不能释放
        this._maxUdpSize = 2048; //2k //与 netty 保持一致 //实际可用 1464
    }

    /**
     * 是否客户端模式
     */
    clientMode(): boolean {
        return this._clientMode;
    }

    /**
     * 获取流管理器
     */
    getStreamManger(): StreamManger {
        return this._streamManger;
    }

    /**
     * 获取角色名
     * */
    getRoleName(): string {
        return this.clientMode() ? "Client" : "Server";
    }


    /**
     * 获取字符集
     */
    getCharset(): string {
        return this._charset;
    }

    /**
     * 配置字符集
     */
    charset(charset: string): this {
        this._charset = charset;
        return this;
    }

    /**
     * 获取编解码器
     */
    getCodec(): Codec {
        return this._codec;
    }

    /**
     * 获取标识生成器
     */
    getIdGenerator(): IdGenerator {
        return this._idGenerator;
    }

    /**
     * 配置标识生成器
     */
    idGenerator(idGenerator: IdGenerator): this {
        Asserts.assertNull("idGenerator", idGenerator);

        this._idGenerator = idGenerator;
        return this;
    }

    /**
     * 获取分片处理
     */
    getFragmentHandler(): FragmentHandler {
        return this._fragmentHandler;
    }

    /**
     * 配置分片处理
     */
    fragmentHandler(fragmentHandler: FragmentHandler): this {
        Asserts.assertNull("fragmentHandler", fragmentHandler);

        this._fragmentHandler = fragmentHandler;
        return this;
    }

    /**
     * 获取分片大小
     */
    getFragmentSize(): number {
        return this._fragmentSize;
    }

    /**
     * 配置分片大小
     */
    fragmentSize(fragmentSize: number): this {
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
    getCoreThreads(): number {
        return this._coreThreads;
    }

    /**
     * 配置核心线程数
     */
    coreThreads(coreThreads: number): this {
        this._coreThreads = coreThreads;
        this._maxThreads = coreThreads * 4;
        return this;
    }

    /**
     * 获取最大线程数
     */
    getMaxThreads(): number {
        return this._maxThreads;
    }

    /**
     * 配置最大线程数
     */
    maxThreads(maxThreads: number): this {
        this._maxThreads = maxThreads;
        return this;
    }

    /**
     * 获取读缓冲大小
     */
    getReadBufferSize(): number {
        return this._readBufferSize;
    }

    /**
     * 配置读缓冲大小
     */
    readBufferSize(readBufferSize: number): this {
        this._readBufferSize = readBufferSize;
        return this;
    }

    /**
     * 获取写缓冲大小
     */
    getWriteBufferSize(): number {
        return this._writeBufferSize;
    }

    /**
     * 配置写缓冲大小
     */
    writeBufferSize(writeBufferSize: number): this {
        this._writeBufferSize = writeBufferSize;
        return this;
    }

    /**
     * 配置连接空闲超时
     */
    getIdleTimeout(): number {
        return this._idleTimeout;
    }

    /**
     * 配置连接空闲超时
     */
    idleTimeout(idleTimeout: number): this {
        this._idleTimeout = idleTimeout;
        return this;
    }

    /**
     * 配置请求默认超时
     */
    getRequestTimeout(): number {
        return this._requestTimeout;
    }

    /**
     * 配置请求默认超时
     */
    requestTimeout(requestTimeout: number): this {
        this._requestTimeout = requestTimeout;
        return this;
    }

    /**
     * 获取消息流超时（单位：毫秒）
     * */
    getStreamTimeout(): number {
        return this._streamTimeout;
    }

    /**
     * 配置消息流超时（单位：毫秒）
     * */
    streamTimeout(streamTimeout: number) {
        this._streamTimeout = streamTimeout;
        return this;
    }

    /**
     * 获取允许最大UDP包大小
     */
    getMaxUdpSize(): number {
        return this._maxUdpSize;
    }

    /**
     * 配置允许最大UDP包大小
     */
    maxUdpSize(maxUdpSize: number): this {
        this._maxUdpSize = maxUdpSize;
        return this;
    }

    /**
     * 生成 id
     * */
    generateId(): string {
        return this._idGenerator.generate();
    }
}