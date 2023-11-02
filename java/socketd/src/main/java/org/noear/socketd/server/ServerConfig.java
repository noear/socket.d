package org.noear.socketd.server;

import org.noear.socketd.core.Codec;
import org.noear.socketd.core.CodecByteBuffer;
import org.noear.socketd.core.Config;
import org.noear.socketd.core.KeyGenerator;
import org.noear.socketd.core.impl.KeyGeneratorGuid;

import javax.net.ssl.SSLContext;
import java.nio.ByteBuffer;

/**
 * 服务端属性（单位：毫秒）
 *
 * @author noear
 * @since 2.0
 */
public class ServerConfig implements Config {
    private final String schema;
    private Codec<ByteBuffer> codec;
    private KeyGenerator keyGenerator;

    private String host;
    private int port;

    private SSLContext sslContext;

    private int coreThreads;
    private int maxThreads;

    private int readBufferSize;
    private int writeBufferSize;

    private int maxRequests;

    public ServerConfig(String schema) {
        this.schema = schema;
        this.codec = new CodecByteBuffer();
        this.keyGenerator = new KeyGeneratorGuid();

        this.host = "";
        this.port = 6329;

        this.coreThreads = Runtime.getRuntime().availableProcessors() * 2;
        this.maxThreads = coreThreads * 8;

        this.readBufferSize = 512;
        this.writeBufferSize = 512;

        this.maxRequests = 10;
    }

    /**
     * 获取协议架构
     */
    public String getSchema() {
        return schema;
    }

    public Codec<ByteBuffer> getCodec() {
        return codec;
    }

    public ServerConfig codec(Codec<ByteBuffer> codec) {
        this.codec = codec;
        return this;
    }

    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    public ServerConfig keyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
        return this;
    }

    /**
     * 获取主机
     */
    public String getHost() {
        return host;
    }

    /**
     * 配置主机
     */
    public ServerConfig host(String host) {
        this.host = host;
        return this;
    }

    /**
     * 获取端口
     */
    public int getPort() {
        return port;
    }

    /**
     * 配置端口
     */
    public ServerConfig port(int port) {
        this.port = port;
        return this;
    }

    /**
     * 获取 ssl 上下文
     */
    public SSLContext getSslContext() {
        return sslContext;
    }

    /**
     * 配置 ssl 上下文
     */
    public ServerConfig sslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return this;
    }

    /**
     * 获取核心线程数
     */
    public int getCoreThreads() {
        return coreThreads;
    }

    /**
     * 配置核心线程数
     */
    public ServerConfig coreThreads(int coreThreads) {
        this.coreThreads = coreThreads;
        return this;
    }

    /**
     * 获取最大线程数
     */
    public int getMaxThreads() {
        return maxThreads;
    }

    /**
     * 配置最大线程数
     */
    public ServerConfig maxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
        return this;
    }

    /**
     * 获取读缓冲大小
     */
    public int getReadBufferSize() {
        return readBufferSize;
    }

    /**
     * 配置读缓冲大小
     */
    public ServerConfig readBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
        return this;
    }

    /**
     * 获取写缓冲大小
     */
    public int getWriteBufferSize() {
        return writeBufferSize;
    }

    /**
     * 配置写缓冲大小
     */
    public ServerConfig writeBufferSize(int writeBufferSize) {
        this.writeBufferSize = writeBufferSize;
        return this;
    }

    /**
     * 允许最大请求数
     * */
    public int getMaxRequests() {
        return maxRequests;
    }

    public ServerConfig maxRequests(int maxRequests) {
        this.maxRequests = maxRequests;
        return this;
    }
}