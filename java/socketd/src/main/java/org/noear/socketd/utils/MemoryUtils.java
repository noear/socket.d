package org.noear.socketd.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * 内存工具
 *
 * @author noear
 * @since 2.4
 */
public class MemoryUtils {
    private static MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

    public static float getUseMemoryRatio() {
        MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
        return memoryUsage.getUsed() * 1.0F / memoryUsage.getMax();
    }
}
