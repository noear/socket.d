package org.noear.socketd.transport.core;

/**
 * 帧（帧[消息[实体]]）
 *
 * @author noear
 * @since 2.0
 */
public class Frame {
    private Flag flag;
    private Message message;

    public Frame(Flag flag, Message message) {
        this.flag = flag;
        this.message = message;
    }

    /**
     * 标志
     * */
    public Flag getFlag(){
        return flag;
    }

    /**
     * 消息
     * */
    public Message getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Frame{" +
                "flag=" + flag +
                ", message=" + message +
                '}';
    }
}
