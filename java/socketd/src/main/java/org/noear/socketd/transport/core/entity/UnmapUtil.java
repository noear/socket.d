package org.noear.socketd.transport.core.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 解除映射工具
 *
 * @author noear
 * @since 2.3
 */
public class UnmapUtil {
    private static Logger log = LoggerFactory.getLogger(UnmapUtil.class);
    private static Method unmapMethod;

    /**
     * 解除映射
     * */
    public static void unmap(FileChannel fileC, MappedByteBuffer buffer) {
        if (fileC == null || buffer == null) {
            return;
        }

        try {
            //为了不直接使用可能会过期的 FileChannelImpl
            if (unmapMethod == null) {
                unmapMethod = fileC.getClass().getDeclaredMethod("unmap", MappedByteBuffer.class);
                unmapMethod.setAccessible(true);
            }

            unmapMethod.invoke(unmapMethod.getDeclaringClass(), buffer);
        } catch (Exception e) {
            log.warn("MappedByteBuffer unmap failure", e);
        }
    }
}
