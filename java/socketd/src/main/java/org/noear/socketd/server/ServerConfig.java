package org.noear.socketd.server;

import org.noear.socketd.core.Codec;
import org.noear.socketd.core.CodecByteBuffer;
import org.noear.socketd.core.Config;
import org.noear.socketd.core.KeyGenerator;
import org.noear.socketd.core.impl.KeyGeneratorGuid;
import org.noear.socketd.utils.Utils;

import javax.net.ssl.SSLContext;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 服务端属性（单位：毫秒）
 *
 * @author noear
 * @since 2.0
 */
public class ServerConfig implements Config {
    private final String schema;
    private Charset charset;

    private Codec<ByteBuffer> codec;
    private KeyGenerator keyGenerator;
    private SSLContext sslContext;

    private String host;
    private int port;

    private int coreThreads;
    private int maxThreads;

    private int readBufferSize;
    private int writeBufferSize;

    private int maxRequests;
    private int maxUdpSize;
    private int maxRangeSize;

    public ServerConfig(String schema) {
        this.schema = schema;
        this.charset = StandardCharsets.UTF_8;
        this.codec = new CodecByteBuffer(this);
        this.keyGenerator = new KeyGeneratorGuid();

        this.host = "";
        this.port = 6329;

        this.coreThreads = Runtime.getRuntime().availableProcessors() * 2;
        this.maxThreads = coreThreads * 8;

        this.readBufferSize = 512;
        this.writeBufferSize = 512;

        this.maxRequests = 10;
        this.maxUdpSize = 2048; //2k //与 netty 保持一致 //实际可用 1464
        this.maxRangeSize = 1024 * 1024 * 16; //16m
    }

    /**
     * 是否客户端模式
     */
    @Override
    public boolean clientMode() {
        return false;
    }

    /**
     * 获取协议架构
     */
    public String getSchema() {
        return schema;
    }


    @Override
    public Charset getCharset() {
        return charset;
    }

    public ServerConfig charset(Charset charset) {
        this.charset = charset;
        return this;
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
     * 获取本机地址
     */
    public String getLocalUrl() {
        if (Utils.isEmpty(host)) {
            return schema + "://127.0.0.1:" + port;
        } else {
            return schema + "://" + host + ":" + port;
        }
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
     */
    public int getMaxRequests() {
        return maxRequests;
    }

    public ServerConfig maxRequests(int maxRequests) {
        this.maxRequests = maxRequests;
        return this;
    }


    /**
     * 允许最大UDP包大小
     */
    @Override
    public int getMaxUdpSize() {
        return maxUdpSize;
    }

    /**
     * 允许最大分片大小
     */
    @Override
    public int getMaxRangeSize() {
        return maxRangeSize;
    }

    public ServerConfig maxRangeSize(int maxRangeSize) {
        this.maxRangeSize = maxRangeSize;
        return this;
    }
    public ServerConfig maxUdpSize(int maxUdpSize) {
        this.maxUdpSize = maxUdpSize;
        return this;
    }

    @Override
    public String toString() {
        return "ServerConfig{" +
                "schema='" + schema + '\'' +
                ", charset=" + charset +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", coreThreads=" + coreThreads +
                ", maxThreads=" + maxThreads +
                ", readBufferSize=" + readBufferSize +
                ", writeBufferSize=" + writeBufferSize +
                ", maxRequests=" + maxRequests +
                ", maxUdpSize=" + maxUdpSize +
                '}';
    }
}