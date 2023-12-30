package org.noear.socketd.transport.core.fragment;

import org.noear.socketd.transport.core.*;

import java.io.IOException;

/**
 * 数据分片监时文件实现
 *
 * @author noear
 * @since 2.1
 */
public class FragmentHandlerTempfile extends FragmentHandlerBase {
    /**
     * 创建分片聚合器
     */
    @Override
    protected FragmentAggregator createFragmentAggregator(MessageInternal message) throws IOException {
        return new FragmentAggregatorTempfile(message);
    }

    @Override
    public boolean aggrEnable() {
        return true;
    }
}