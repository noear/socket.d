package org.noear.socketd.transport.core.entity;

import org.noear.socketd.transport.core.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 文件实体
 *
 * @author noear
 * @since 2.0
 */
public class FileEntity extends EntityDefault {
    public FileEntity(File file) throws IOException {
        data(new FileInputStream(file));
        putMeta(Constants.META_DATA_DISPOSITION_FILENAME, file.getName());
    }
}
