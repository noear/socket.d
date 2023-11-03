package org.noear.socketd.core.entity;

import org.noear.socketd.core.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 文件实体
 *
 * @author noear
 * @since 2.0
 */
public class FileEntity extends StreamEntity {

    public FileEntity(File file) throws IOException {
        super(new FileInputStream(file));
        putMeta(Constants.META_DATA_DISPOSITION_FILENAME, file.getName());
    }
}
