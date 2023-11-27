package org.noear.socketd.transport.core.internal;

import org.noear.socketd.transport.core.Acceptor;
import org.noear.socketd.transport.core.AcceptorBase;
import org.noear.socketd.transport.core.AcceptorManger;
import org.noear.socketd.transport.core.Config;
import org.noear.socketd.utils.RunUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 答复接收管理器
 *
 * @author noear
 * @since 1.0
 */
public class AcceptorMangerDefault implements AcceptorManger {
    private static Logger log = LoggerFactory.getLogger(ChannelDefault.class);

    //配置
    private final Config config;
    //答复接收器字典（管理）
    private final Map<String, AcceptorBase> acceptorMap;

    public AcceptorMangerDefault(Config config) {
        this.acceptorMap = new ConcurrentHashMap<>();
        this.config = config;
    }

    /**
     * 添加接收器
     *
     * @param sid      流Id
     * @param acceptor 答复接收器
     */
    @Override
    public void addAcceptor(String sid, AcceptorBase acceptor) {
        acceptorMap.put(sid, acceptor);

        //增加超时处理
        if (config.getAcceptorTimeout() > 0) {
            acceptor.insuranceFuture = RunUtils.delay(() -> {
                acceptorMap.remove(sid);
            }, config.getAcceptorTimeout());
        }
    }

    /**
     * 获取接收器
     *
     * @param sid 流Id
     */
    @Override
    public Acceptor getAcceptor(String sid) {
        return acceptorMap.get(sid);
    }

    /**
     * 移除接收器
     *
     * @param sid 流Id
     */
    @Override
    public void removeAcceptor(String sid) {
        AcceptorBase acceptor = acceptorMap.remove(sid);

        if (acceptor != null) {
            if (acceptor.insuranceFuture != null) {
                acceptor.insuranceFuture.cancel(false);
            }

            if (log.isDebugEnabled()) {
                log.debug("{} acceptor removed, sid={}", config.getRoleName(), sid);
            }
        }
    }
}