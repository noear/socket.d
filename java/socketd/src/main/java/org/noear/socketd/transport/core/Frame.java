package org.noear.socketd.transport.core;

/**
 * 帧（帧[消息[实体]]）
 *
 * @author noear
 * @since 2.0
 */
public class Frame {
    private int flag;
    private MessageInternal message;

    public Frame(int flag, MessageInternal message) {
        this.flag = flag;
        this.message = message;
    }

    /**
     * 标志（保持与 Message 的获取风格）
     * */
    public int flag(){
        return flag;
    }

    /**
     * 消息
     * */
    public MessageInternal message() {
        return message;
    }

    @Override
    public String toString() {
        return "Frame{" +
                "flag=" + Flags.name(flag) +
                ", message=" + message +
                '}';
    }
}
