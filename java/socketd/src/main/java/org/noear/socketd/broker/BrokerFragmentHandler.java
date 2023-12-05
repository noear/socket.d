package org.noear.socketd.broker;

import org.noear.socketd.transport.core.fragment.FragmentHandlerDefault;

/**
 * 经纪人分片处理（关掉聚合）
 *
 * @author noear
 * @since 2.1
 */
public class BrokerFragmentHandler extends FragmentHandlerDefault {
    @Override
    public boolean aggrEnable() {
        return false;
    }
}
