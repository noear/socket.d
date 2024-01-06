package org.noear.socketd.transport.core.stream;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.internal.ChannelDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 流管理器默认实现
 *
 * @author noear
 * @since 2.0
 */
public class StreamMangerDefault implements StreamManger {
    private static Logger log = LoggerFactory.getLogger(ChannelDefault.class);

    //配置
    private final Config config;
    //流接收器字典（管理）
    private final Map<String, StreamInternal> streamMap;

    public StreamMangerDefault(Config config) {
        this.streamMap = new ConcurrentHashMap<>();
        this.config = config;
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
     * 添加流接收器
     *
     * @param sid    流Id
     * @param stream 流
     */
    @Override
    public void addStream(String sid, StreamInternal stream) {
        Asserts.assertNull("stream", stream);

        if (stream.demands() == Constants.DEMANDS_ZERO) {
            //零需求，则不添加
            return;
        }

        streamMap.put(sid, stream);

        //增加流超时处理（做为后备保险）
        long streamTimeout = stream.timeout() > 0 ? stream.timeout() : config.getStreamTimeout();
        if (streamTimeout > 0) {
            stream.insuranceStart(this, streamTimeout);
        }
    }

    /**
     * 移除流接收器
     *
     * @param sid 流Id
     */
    @Override
    public void removeStream(String sid) {
        StreamInternal stream = streamMap.remove(sid);

        if (stream != null) {
            stream.insuranceCancel();

            if (log.isDebugEnabled()) {
                log.debug("{} stream removed, sid={}", config.getRoleName(), sid);
            }
        }
    }
}