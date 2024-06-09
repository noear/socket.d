package org.noear.socketd.broker;

import org.noear.socketd.transport.core.Session;

/**
 * 经理人数据
 *
 * @author noear
 * @since 2.5
 */
public class BrokerData<T> {
    public final Session requester;
    public final T data;

    public BrokerData(Session requester, T data) {
        this.requester = requester;
        this.data = data;
    }
}
