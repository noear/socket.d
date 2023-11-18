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

    /**
     * 因协议关闭
     */
    int CLOSE1_PROTOCOL = 1;
    /**
     * 因异常关闭
     */
    int CLOSE2_ERROR = 2;
    /**
     * 因用户主动关闭
     */
    int CLOSE3_USER = 3;
}
