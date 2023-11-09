package org.noear.socketd.transport.core.fragment;

import org.noear.socketd.transport.core.Frame;

/**
 * 分片持有人
 *
 * @author noear
 * @since 2.0
 */
public class FragmentHolder {
    private int index;
    private Frame frame;

    public FragmentHolder(int index, Frame frame) {
        this.index = index;
        this.frame = frame;
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
    public Frame getFrame() {
        return frame;
    }
}
