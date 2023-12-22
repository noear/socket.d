import {Codec, CodecByteBuffer} from "./Codec";
import {StreamManger} from "./Stream";
import {GuidGenerator, IdGenerator} from "./IdGenerator";
import {FragmentHandler, FragmentHandlerDefault} from "./FragmentHandler";
import {Constants} from "./Constants";

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
    getRoleName(): String;

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

export class ConfigBase implements Config {
    //是否客户端模式
    _clientMode:boolean;
    //流管理器
    _streamManger: StreamManger;
    //编解码器
    _codec: Codec;

    //id生成器
    _idGenerator:IdGenerator;
    //分片处理
    _fragmentHandler: FragmentHandler;
    //分片大小
    _fragmentSize:number;
    //字符集
    _charset:string
    //内核线程数
    _coreThreads:number;
    //最大线程数
    _maxThreads:number;
    //读缓冲大小
    _readBufferSize:number;
    //写缓冲大小
    _writeBufferSize:number;

    //连接空闲超时
    _idleTimeout:number;
    //请求默认超时
    _requestTimeout:number;
    //消息流超时（从发起到应答结束）
    _streamTimeout:number;
    //最大udp包大小
    _maxUdpSize:number;

    constructor(clientMode:boolean) {
        this._clientMode = clientMode;
        this._codec = new CodecByteBuffer();
        this._streamManger = new StreamManger();

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

    clientMode(): boolean {
        return this._clientMode;
    }
    getStreamManger(): StreamManger {
        return this._streamManger;
    }
    getRoleName(): String {
        return this.clientMode() ? "Client" : "Server";
    }
    getCharset(): string {
        return this._charset;
    }
    getCodec(): Codec {
       return  this._codec;
    }
    getIdGenerator(): IdGenerator {
        return this._idGenerator;
    }
    getFragmentHandler(): FragmentHandler {
        return this._fragmentHandler;
    }
    getFragmentSize(): number {
        return this._fragmentSize;
    }
    getCoreThreads(): number {
        return this._coreThreads;
    }
    getMaxThreads(): number {
        return this._maxThreads;
    }
    getReadBufferSize(): number {
        return this._readBufferSize;
    }
    getWriteBufferSize(): number {
        return this._writeBufferSize;
    }
    getIdleTimeout(): number {
        return this._idleTimeout;
    }
    getRequestTimeout(): number {
        return this._requestTimeout;
    }
    getStreamTimeout(): number {
        return this._streamTimeout;
    }
    getMaxUdpSize(): number {
        return this._maxUdpSize;
    }

    generateId():string{
        return this._idGenerator.generate();
    }
}