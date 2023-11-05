package org.noear.socketd.transport.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * 常量
 *
 * @author noear
 * @since 2.0
 */
public interface Constants {
    String DEF_SID = "";
    String DEF_TOPIC = "";
    String DEF_META_STRING = "";
    InputStream DEF_DATA = new ByteArrayInputStream(new byte[]{});


    int MAX_SIZE_SID = 128;
    int MAX_SIZE_TOPIC = 512;
    int MAX_SIZE_META = 4096;
}
