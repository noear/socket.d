package org.noear.socketd.transport.core.identifier;

import org.noear.socketd.transport.core.IdGenerator;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 时间生成器时间适配（不适合分布式）
 *
 * @author noear
 * @since 2.0
 */
public class TimeidGenerator implements IdGenerator {
    private AtomicLong basetime = new AtomicLong();
    private AtomicLong count = new AtomicLong();

    /**
     * 创建
     */
    @Override
    public String generate() {
        long tmp = System.currentTimeMillis() / 1000;
        if (tmp != basetime.get()) {
            basetime.set(tmp);
            count.set(0);
        }

        //1秒内可容1亿
        return String.valueOf(basetime.get() * 1000 * 1000 * 1000 + count.incrementAndGet());
    }
}
