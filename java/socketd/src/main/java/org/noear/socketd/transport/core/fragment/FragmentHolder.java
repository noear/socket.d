package org.noear.socketd.transport.core.fragment;

import org.noear.socketd.transport.core.MessageInternal;

/**
 * 分片持有人
 *
 * @author noear
 * @since 2.0
 */
public class FragmentHolder {
    private int index;
    private MessageInternal message;

    public FragmentHolder(int index, MessageInternal message) {
        this.index = index;
        this.message = message;
    }

    /**
     * 获取顺序位
     */
    public int getIndex() {
        return index;
    }

    /**
     * 获取分片帧
     */
    public MessageInternal getMessage() {
        return message;
    }
}
