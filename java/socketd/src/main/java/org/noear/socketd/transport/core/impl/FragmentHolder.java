package org.noear.socketd.transport.core.impl;

import org.noear.socketd.transport.core.Frame;

/**
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

    public int getIndex() {
        return index;
    }

    public Frame getFrame() {
        return frame;
    }
}
