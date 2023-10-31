package org.noear.socketd.solon.annotation;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 2.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SocketdServer {
    /**
     * 路径
     * */
    String path() default "";

    /**
     * 架构
     */
    String[] schema();
}
