package org.noear.socketd.protocol;

/**
 * 帧
 *
 * @author noear
 * @since 2.0
 */
public class Frame {
    private Flag flag;
    private Payload payload;

    public Frame(Flag flag, Payload payload) {
        this.flag = flag;
        this.payload = payload;
    }

    /**
     * 标志
     * */
    public Flag getFlag(){
        return flag;
    }

    /**
     * 载体
     * */
    public Payload getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "Frame{" +
                "flag=" + flag +
                ", payload=" + payload +
                '}';
    }
}
