package org.noear.socketd.protocol.impl;

import org.noear.socketd.protocol.Flag;
import org.noear.socketd.protocol.Payload;

/**
 * 负载
 *
 * @author noear
 * @since 2.0
 */
public class PayloadInternal extends Payload {
    public PayloadInternal(String key, String routeDescriptor, String header, byte[] body) {
        super(key, routeDescriptor, header, body);
    }

    public Flag getFlag() {
        return flag;
    }

    public void setFlag(Flag flag) {
        this.flag = flag;
    }
}
