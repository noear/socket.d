package org.noear.socketd.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 压缩工具
 *
 * @author noear
 * @since 2.0
 */
public class GzipUtils {
    /**
     * 压缩
     */
    public static byte[] compress(byte[] input) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream)
        ) {
            gzipOutputStream.write(input);
            gzipOutputStream.finish();

            return outputStream.toByteArray();
        }
    }

    /**
     * 解压
     */
    public static byte[] decompress(byte[] compressed) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(compressed);
             GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = gzipInputStream.read(buffer, 0, buffer.length)) != -1) {
                outputStream.write(buffer, 0, length);
            }

            return outputStream.toByteArray();
        }
    }
}
