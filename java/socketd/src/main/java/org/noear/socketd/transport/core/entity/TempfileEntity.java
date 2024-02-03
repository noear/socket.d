package org.noear.socketd.transport.core.entity;

import org.noear.socketd.transport.core.EntityMetas;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;

/**
 * 临时文件实体
 *
 * @author noear
 * @since 2.0
 */
public class TempfileEntity extends EntityDefault {
    private final File file;
    private final FileChannel fileC;
    private RandomAccessFile fileRaf;

    public TempfileEntity(File file, FileChannel fileC, ByteBuffer data, Map<String, String> metaMap) throws IOException {
        this.file = file;
        this.fileC = fileC;
        dataSet(data);
        metaMapPut(metaMap);
    }

    public TempfileEntity(File file) throws IOException {
        this.file = file;
        this.fileRaf = new RandomAccessFile(file, "r");
        this.fileC = fileRaf.getChannel();

        long len = file.length();
        MappedByteBuffer byteBuffer = fileC.map(FileChannel.MapMode.READ_ONLY, 0, len);

        dataSet(byteBuffer);
        metaPut(EntityMetas.META_DATA_DISPOSITION_FILENAME, file.getName());
    }

    @Override
    public void release() throws IOException {
        if (data() instanceof MappedByteBuffer) {
            UnmapUtil.unmap(fileC, (MappedByteBuffer) data());
        }

        if (fileRaf != null) {
            fileRaf.close();
        }

        file.delete();
    }
}
