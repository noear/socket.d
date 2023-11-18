/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core {
    /**
     * @author noear
     * @since 2.0
     * @param {boolean} clientMode
     * @class
     */
    export abstract class ConfigBase<T extends org.noear.socketd.transport.core.Config> implements org.noear.socketd.transport.core.Config {
        /*private*/ __clientMode: boolean;

        __charset: string;

        __codec: org.noear.socketd.transport.core.Codec<org.noear.socketd.transport.core.buffer.BufferReader, org.noear.socketd.transport.core.buffer.BufferWriter>;

        __idGenerator: org.noear.socketd.transport.core.IdGenerator;

        __fragmentHandler: org.noear.socketd.transport.core.FragmentHandler;

        __sslContext: javax.net.ssl.SSLContext;

        __channelExecutor: java.util.concurrent.ExecutorService;

        __coreThreads: number;

        __maxThreads: number;

        __readBufferSize: number;

        __writeBufferSize: number;

        __idleTimeout: number;

        __requestTimeout: number;

        __maxRequests: number;

        __maxUdpSize: number;

        public constructor(clientMode: boolean) {
            if (this.__clientMode === undefined) { this.__clientMode = false; }
            if (this.__charset === undefined) { this.__charset = null; }
            if (this.__codec === undefined) { this.__codec = null; }
            if (this.__idGenerator === undefined) { this.__idGenerator = null; }
            if (this.__fragmentHandler === undefined) { this.__fragmentHandler = null; }
            if (this.__sslContext === undefined) { this.__sslContext = null; }
            if (this.__channelExecutor === undefined) { this.__channelExecutor = null; }
            if (this.__coreThreads === undefined) { this.__coreThreads = 0; }
            if (this.__maxThreads === undefined) { this.__maxThreads = 0; }
            if (this.__readBufferSize === undefined) { this.__readBufferSize = 0; }
            if (this.__writeBufferSize === undefined) { this.__writeBufferSize = 0; }
            if (this.__idleTimeout === undefined) { this.__idleTimeout = 0; }
            if (this.__requestTimeout === undefined) { this.__requestTimeout = 0; }
            if (this.__maxRequests === undefined) { this.__maxRequests = 0; }
            if (this.__maxUdpSize === undefined) { this.__maxUdpSize = 0; }
            this.__clientMode = clientMode;
            this.__charset = java.nio.charset.StandardCharsets.UTF_8;
            this.__codec = new org.noear.socketd.transport.core.CodecByteBuffer(this);
            this.__idGenerator = new org.noear.socketd.transport.core.identifier.GuidGenerator();
            this.__fragmentHandler = new org.noear.socketd.transport.core.fragment.FragmentHandlerDefault();
            this.__coreThreads = Math.max(java.lang.Runtime.getRuntime().availableProcessors(), 2);
            this.__maxThreads = this.__coreThreads * 8;
            this.__readBufferSize = 512;
            this.__writeBufferSize = 512;
            this.__idleTimeout = 0;
            this.__requestTimeout = 10000;
            this.__maxRequests = 10;
            this.__maxUdpSize = 2048;
        }

        /**
         * 是否客户端模式
         * @return {boolean}
         */
        public clientMode(): boolean {
            return this.__clientMode;
        }

        /**
         * 获取字符集
         * @return {string}
         */
        public getCharset(): string {
            return this.__charset;
        }

        /**
         * 配置字符集
         * @param {string} charset
         * @return {*}
         */
        public charset(charset: string): T {
            this.__charset = charset;
            return <T><any>this;
        }

        /**
         * 获取编解码器
         * @return {*}
         */
        public getCodec(): org.noear.socketd.transport.core.Codec<org.noear.socketd.transport.core.buffer.BufferReader, org.noear.socketd.transport.core.buffer.BufferWriter> {
            return this.__codec;
        }

        /**
         * 配置编解码器
         * @param {*} codec
         * @return {*}
         */
        public codec(codec: org.noear.socketd.transport.core.Codec<org.noear.socketd.transport.core.buffer.BufferReader, org.noear.socketd.transport.core.buffer.BufferWriter>): T {
            org.noear.socketd.transport.core.Asserts.assertNull(codec, "codec");
            this.__codec = codec;
            return <T><any>this;
        }

        /**
         * 获取分片处理
         * @return {*}
         */
        public getFragmentHandler(): org.noear.socketd.transport.core.FragmentHandler {
            return this.__fragmentHandler;
        }

        /**
         * 配置分片处理
         * @param {*} fragmentHandler
         * @return {*}
         */
        public fragmentHandler(fragmentHandler: org.noear.socketd.transport.core.FragmentHandler): T {
            org.noear.socketd.transport.core.Asserts.assertNull(fragmentHandler, "fragmentHandler");
            this.__fragmentHandler = fragmentHandler;
            return <T><any>this;
        }

        /**
         * 获取标识生成器
         * @return {*}
         */
        public getIdGenerator(): org.noear.socketd.transport.core.IdGenerator {
            return this.__idGenerator;
        }

        /**
         * 配置标识生成器
         * @param {*} idGenerator
         * @return {*}
         */
        public idGenerator(idGenerator: org.noear.socketd.transport.core.IdGenerator): T {
            org.noear.socketd.transport.core.Asserts.assertNull(idGenerator, "idGenerator");
            this.__idGenerator = idGenerator;
            return <T><any>this;
        }

        /**
         * 获取 ssl 上下文
         * @return {javax.net.ssl.SSLContext}
         */
        public getSslContext(): javax.net.ssl.SSLContext {
            return this.__sslContext;
        }

        /**
         * 配置 ssl 上下文
         * @param {javax.net.ssl.SSLContext} sslContext
         * @return {*}
         */
        public sslContext(sslContext: javax.net.ssl.SSLContext): T {
            this.__sslContext = sslContext;
            return <T><any>this;
        }

        /**
         * 
         * @return {*}
         */
        public getChannelExecutor(): java.util.concurrent.ExecutorService {
            if (this.__channelExecutor == null){
                {
                    if (this.__channelExecutor == null){
                        const nThreads: number = this.clientMode() ? this.__coreThreads : this.__maxThreads;
                        this.__channelExecutor = new java.util.concurrent.ThreadPoolExecutor(nThreads, nThreads, 0, java.util.concurrent.TimeUnit.MILLISECONDS, <any>(new java.util.concurrent.LinkedBlockingQueue<() => void>()), new org.noear.socketd.utils.NamedThreadFactory("Socketd-channelExecutor-"));
                    }
                };
            }
            return this.__channelExecutor;
        }

        /**
         * 配置调试执行器
         * 
         * @param {*} channelExecutor
         * @return {*}
         */
        public channelExecutor(channelExecutor: java.util.concurrent.ExecutorService): T {
            this.__channelExecutor = channelExecutor;
            return <T><any>this;
        }

        /**
         * 获取核心线程数
         * @return {number}
         */
        public getCoreThreads(): number {
            return this.__coreThreads;
        }

        /**
         * 配置核心线程数
         * @param {number} coreThreads
         * @return {*}
         */
        public coreThreads(coreThreads: number): T {
            this.__coreThreads = coreThreads;
            return <T><any>this;
        }

        /**
         * 获取最大线程数
         * @return {number}
         */
        public getMaxThreads(): number {
            return this.__maxThreads;
        }

        /**
         * 配置最大线程数
         * @param {number} maxThreads
         * @return {*}
         */
        public maxThreads(maxThreads: number): T {
            this.__maxThreads = maxThreads;
            return <T><any>this;
        }

        /**
         * 获取读缓冲大小
         * @return {number}
         */
        public getReadBufferSize(): number {
            return this.__readBufferSize;
        }

        /**
         * 配置读缓冲大小
         * @param {number} readBufferSize
         * @return {*}
         */
        public readBufferSize(readBufferSize: number): T {
            this.__readBufferSize = readBufferSize;
            return <T><any>this;
        }

        /**
         * 获取写缓冲大小
         * @return {number}
         */
        public getWriteBufferSize(): number {
            return this.__writeBufferSize;
        }

        /**
         * 配置写缓冲大小
         * @param {number} writeBufferSize
         * @return {*}
         */
        public writeBufferSize(writeBufferSize: number): T {
            this.__writeBufferSize = writeBufferSize;
            return <T><any>this;
        }

        /**
         * 获取连接空闲超时
         * @return {number}
         */
        public getIdleTimeout(): number {
            return this.__idleTimeout;
        }

        /**
         * 配置连接空闲超时
         * @param {number} idleTimeout
         * @return {*}
         */
        public idleTimeout(idleTimeout: number): T {
            this.__idleTimeout = idleTimeout;
            return <T><any>this;
        }

        /**
         * 获取答复默认超时
         * @return {number}
         */
        public getRequestTimeout(): number {
            return this.__requestTimeout;
        }

        /**
         * 配置请求默认超时
         * @param {number} requestTimeout
         * @return {*}
         */
        public requestTimeout(requestTimeout: number): T {
            this.__requestTimeout = requestTimeout;
            return <T><any>this;
        }

        /**
         * 允许最大同时请求数
         * @return {number}
         */
        public getMaxRequests(): number {
            return this.__maxRequests;
        }

        /**
         * 配置最大同时请求数
         * @param {number} maxRequests
         * @return {*}
         */
        public maxRequests(maxRequests: number): T {
            this.__maxRequests = maxRequests;
            return <T><any>this;
        }

        /**
         * 获取允许最大UDP包大小
         * @return {number}
         */
        public getMaxUdpSize(): number {
            return this.__maxUdpSize;
        }

        /**
         * 配置允许最大UDP包大小
         * @param {number} maxUdpSize
         * @return {*}
         */
        public maxUdpSize(maxUdpSize: number): T {
            this.__maxUdpSize = maxUdpSize;
            return <T><any>this;
        }
    }
    ConfigBase["__class"] = "org.noear.socketd.transport.core.ConfigBase";
    ConfigBase["__interfaces"] = ["org.noear.socketd.transport.core.Config"];


}

