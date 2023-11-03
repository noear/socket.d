package org.noear.socketd.transport.core.impl;

import org.noear.socketd.transport.core.KeyGenerator;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author noear
 * @since 2.0
 */
public class KeyGeneratorTime implements KeyGenerator {
    private long basetime;
    private AtomicLong count = new AtomicLong();

    /**
     * 创建
     */
    @Override
    public String generate() {
        long tmp = System.currentTimeMillis() / 1000;
        if (tmp != basetime) {
            synchronized (count) {
                basetime = tmp;
                count.set(0);
            }
        }

        //1秒内可容1亿
        return String.valueOf(basetime * 1000 * 1000 * 1000 + count.incrementAndGet());
    }
}
