package org.noear.socketd.transport.core.fragment;

import org.noear.socketd.transport.core.*;

import java.io.IOException;

/**
 * 数据分片默认实现
 *
 * @author noear
 * @since 2.0
 */
public class FragmentHandlerDefault extends FragmentHandlerBase {
    /**
     * 创建分片聚合器
     */
    @Override
    protected FragmentAggregator createFragmentAggregator(MessageInternal message) throws IOException {
        return new FragmentAggregatorDefault(message);
    }

    @Override
    public boolean aggrEnable() {
        return true;
    }
}