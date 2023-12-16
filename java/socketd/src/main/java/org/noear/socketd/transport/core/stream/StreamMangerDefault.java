package org.noear.socketd.transport.core.stream;

import org.noear.socketd.exception.SocketdTimeoutException;
import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.internal.ChannelDefault;
import org.noear.socketd.utils.RunUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 流管理器
 *
 * @author noear
 * @since 2.0
 */
public class StreamMangerDefault implements StreamManger {
    private static Logger log = LoggerFactory.getLogger(ChannelDefault.class);

    //配置
    private final Config config;
    //流接收器字典（管理）
    private final Map<String, StreamBase> streamMap;

    public StreamMangerDefault(Config config) {
        this.streamMap = new ConcurrentHashMap<>();
        this.config = config;
    }

    /**
     * 添加流接收器
     *
     * @param sid    流Id
     * @param stream 流
     */
    @Override
    public void addStream(String sid, StreamBase stream) {
        Asserts.assertNull("stream", stream);
        streamMap.put(sid, stream);

        //增加流超时处理（做为后备保险）
        long streamTimeout = stream.timeout() > 0 ? stream.timeout() : config.getStreamTimeout();
        if (streamTimeout > 0) {
            stream.insuranceFuture = RunUtils.delay(() -> {
                streamMap.remove(sid);
                stream.onError(new SocketdTimeoutException("The stream response timeout, sid=" + sid));
            }, streamTimeout);
        }
    }

    /**
     * 获取流接收器
     *
     * @param sid 流Id
     */
    @Override
    public StreamInternal getStream(String sid) {
        return streamMap.get(sid);
    }

    /**
     * 移除流接收器
     *
     * @param sid 流Id
     */
    @Override
    public void removeStream(String sid) {
        StreamBase stream = streamMap.remove(sid);

        if (stream != null) {
            if (stream.insuranceFuture != null) {
                stream.insuranceFuture.cancel(false);
            }

            if (log.isDebugEnabled()) {
                log.debug("{} stream removed, sid={}", config.getRoleName(), sid);
            }
        }
    }
}