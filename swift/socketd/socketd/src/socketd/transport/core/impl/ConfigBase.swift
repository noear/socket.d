//
//  ConfigBase.swift
//  socketd
//
//  Created by noear on 2024/1/13.
//

import Foundation

/**
 * 配置基类
 *
 * @author noear
 * @since 2.0
 */
class ConfigBase : Config{
   
    //是否客户端模式
    private var _clientMode: Bool;
    //流管理器
    private var _streamManger: StreamManger;
    //编解码器
    private var _codec: Codec;
    
    //id生成器
    private var _idGenerator: IdGenerator;
    //分片处理
    private var _fragmentHandler: FragmentHandler;
    //分片大小
    private var _fragmentSize: Int32;
    //字符集
    var _charset: String
    //内核线程数
    var _coreThreads: Int32;
    //最大线程数
    var _maxThreads: Int32;
    //读缓冲大小
    var _readBufferSize: Int32;
    //写缓冲大小
    var _writeBufferSize: Int32;
    
    //连接空闲超时
    var _idleTimeout: Int64;
    //请求默认超时
    var _requestTimeout: Int64;
    //消息流超时（从发起到应答结束）
    var _streamTimeout: Int64;
    //最大udp包大小
    var _maxUdpSize: Int32;
    
    init(_ clientMode: Bool) {
        self._clientMode = clientMode;
        self._streamManger =  StreamMangerDefault(self);
        self._codec =  CodecDefault(self);
        
        self._charset = "utf-8";
        
        self._idGenerator = GuidGenerator();
        self._fragmentHandler = FragmentHandlerDefault();
        self._fragmentSize = Constants.MAX_SIZE_DATA;
        
        self._coreThreads = 2;
        self._maxThreads = self._coreThreads * 4;
        
        self._readBufferSize = 512;
        self._writeBufferSize = 512;
        
        self._idleTimeout = 0; //默认不关（提供用户特殊场景选择）
        self._requestTimeout = 10_000; //10秒（默认与连接超时同）
        self._streamTimeout = 1000 * 60 * 60 * 2;//2小时 //避免永不回调时，不能释放
        self._maxUdpSize = 2048; //2k //与 netty 保持一致 //实际可用 1464
    }
    
    /**
     * 是否客户端模式
     */
    func  clientMode() -> Bool {
        return self._clientMode;
    }
    
    /**
     * 获取流管理器
     */
    func   getStreamManger() -> StreamManger {
        return self._streamManger;
    }
    
    /**
     * 获取角色名
     * */
    func  getRoleName() -> String {
        return self.clientMode() ? "Client" : "Server";
    }
    
    
    /**
     * 获取字符集
     */
    func getCharset() -> String {
        return self._charset;
    }
    
    /**
     * 配置字符集
     */
    func charset(_ charset: String)-> Self {
        self._charset = charset;
        return self;
    }
    
    /**
     * 获取编解码器
     */
    func  getCodec() -> Codec {
        return self._codec;
    }
    
    /**
     * 获取标识生成器
     */
    func   getIdGenerator() -> IdGenerator {
        return self._idGenerator;
    }
    
    /**
     * 配置标识生成器
     */
    func  idGenerator(_ idGenerator: IdGenerator) -> Self {
        Asserts.assertNull("idGenerator", idGenerator);
        
        self._idGenerator = idGenerator;
        return self;
    }
    
    /**
     * 获取分片处理
     */
    func   getFragmentHandler() -> FragmentHandler {
        return self._fragmentHandler;
    }
    
    /**
     * 配置分片处理
     */
    func   fragmentHandler(_ fragmentHandler: FragmentHandler) -> Self {
        Asserts.assertNull("fragmentHandler", fragmentHandler);
        
        self._fragmentHandler = fragmentHandler;
        return self;
    }
    
    /**
     * 获取分片大小
     */
    func   getFragmentSize() -> Int32 {
        return self._fragmentSize;
    }
    
    func getSslContext() -> SSLContext? {
        return nil;
    }
    
    /**
     * 配置分片大小
     */
    func   fragmentSize(_ fragmentSize: Int32) -> Self {
        if (fragmentSize > Constants.MAX_SIZE_DATA) {
            //todo: throw NSInvalidArgumentException("The parameter fragmentSize cannot > 16m");
        }
        
        if (fragmentSize < Constants.MIN_FRAGMENT_SIZE) {
            //todo:  throw NSInvalidArgumentException("The parameter fragmentSize cannot < 1k");
        }
        
        self._fragmentSize = fragmentSize;
        return self;
    }
    
    /**
     * 获取核心线程数
     */
    func   getCoreThreads() -> Int32 {
        return self._coreThreads;
    }
    
    /**
     * 配置核心线程数
     */
    func   coreThreads(_ coreThreads: Int32) -> Self {
        self._coreThreads = coreThreads;
        self._maxThreads = coreThreads * 4;
        return self;
    }
    
    /**
     * 获取最大线程数
     */
    func   getMaxThreads() -> Int32 {
        return self._maxThreads;
    }
    
    /**
     * 配置最大线程数
     */
    func   maxThreads(_ maxThreads: Int32) -> Self{
        self._maxThreads = maxThreads;
        return self;
    }
    
    /**
     * 获取读缓冲大小
     */
    func   getReadBufferSize() -> Int32 {
        return self._readBufferSize;
    }
    
    /**
     * 配置读缓冲大小
     */
    func   readBufferSize(_ readBufferSize: Int32) -> Self {
        self._readBufferSize = readBufferSize;
        return self;
    }
    
    /**
     * 获取写缓冲大小
     */
    func  getWriteBufferSize() -> Int32 {
        return self._writeBufferSize;
    }
    
    /**
     * 配置写缓冲大小
     */
    func writeBufferSize(_ writeBufferSize: Int32) -> Self {
        self._writeBufferSize = writeBufferSize;
        return self;
    }
    
    /**
     * 配置连接空闲超时
     */
    func getIdleTimeout() -> Int64 {
        return self._idleTimeout;
    }
    
    /**
     * 配置连接空闲超时
     */
    func   idleTimeout(_ idleTimeout: Int64) ->Self {
        self._idleTimeout = idleTimeout;
        return self;
    }
    
    /**
     * 配置请求默认超时
     */
    func getRequestTimeout() -> Int64 {
        return self._requestTimeout;
    }
    
    /**
     * 配置请求默认超时
     */
    func requestTimeout(_ requestTimeout: Int64) -> Self {
        self._requestTimeout = requestTimeout;
        return self;
    }
    
    /**
     * 获取消息流超时（单位：毫秒）
     * */
    func getStreamTimeout() -> Int64 {
        return self._streamTimeout;
    }
    
    /**
     * 配置消息流超时（单位：毫秒）
     * */
    func   streamTimeout(_ streamTimeout: Int64) -> Self{
        self._streamTimeout = streamTimeout;
        return self;
    }
    
    /**
     * 获取允许最大UDP包大小
     */
    func getMaxUdpSize() -> Int32 {
        return self._maxUdpSize;
    }
    
    /**
     * 配置允许最大UDP包大小
     */
    func  maxUdpSize(maxUdpSize: Int32) -> Self {
        self._maxUdpSize = maxUdpSize;
        return self;
    }
    
    /**
     * 生成 id
     * */
    func generateId() -> String {
        return self._idGenerator.generate();
    }
}


