package org.noear.socketd.transport.core.impl;

import org.noear.socketd.transport.core.IdGenerator;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 时间生成器时间适配（不适合分布式）
 *
 * @author noear
 * @since 2.0
 */
public class IdGeneratorTime implements IdGenerator {
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
