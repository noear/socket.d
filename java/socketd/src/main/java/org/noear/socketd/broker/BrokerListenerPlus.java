package org.noear.socketd.broker;

import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 经纪人监听器加强版（基于定制环循器转发）
 *
 * @author noear
 * @since 2.5
 */
public class BrokerListenerPlus extends BrokerListener {
    //经理人线程
    private transient Thread brokerThread;
    //经理人消息队列
    private final transient Queue<BrokerData<Message>> brokerMessageQueue = new ConcurrentLinkedQueue<>();
    //经理人消息记数器
    protected final transient AtomicLong brokerMessageCounter = new AtomicLong(0L);

    @Override
    public void onMessage(Session requester, Message message) throws IOException {
        brokerMessageCounter.incrementAndGet();
        brokerMessageQueue.add(new BrokerData<>(requester, message));
    }

    protected void brokerThreadHandle() {
        while (!brokerThread.isInterrupted()) {
            BrokerData<Message> brokerData = brokerMessageQueue.poll();

            try {
                if (brokerData != null) {
                    brokerMessageCounter.decrementAndGet();
                    onMessageDo(brokerData.requester, brokerData.data);
                } else {
                    //如果没数据，休息会儿
                    Thread.sleep(10);
                }
            } catch (Throwable e) {
                if (brokerData != null) {
                    onError(brokerData.requester, e);
                }
            }
        }
    }

    public void start() {
        if (brokerThread == null) {
            brokerThread = new Thread(this::brokerThreadHandle);
            brokerThread.setDaemon(true);
            brokerThread.start();
        }
    }

    public void stop() {
        if (brokerThread != null) {
            brokerThread.interrupt();
            brokerThread = null;
        }
    }
}
