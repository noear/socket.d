package org.noear.socketd.transport.core;

/**
 * 帧（帧[消息[实体]]）
 *
 * @author noear
 * @since 2.0
 */
public class Frame {
    private int flag;
    private Message message;

    public Frame(int flag, Message message) {
        this.flag = flag;
        this.message = message;
    }

    /**
     * 标志
     * */
    public int getFlag(){
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
