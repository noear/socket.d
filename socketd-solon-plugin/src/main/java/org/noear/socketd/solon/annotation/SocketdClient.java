package org.noear.socketd.solon.annotation;


import java.lang.annotation.*;

/**
 * @author noear
 * @since 2.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SocketdClient {
    /**
     * 资源描述
     */
    String url();

    /**
     * 是否自动重链
     * */
    boolean autoReconnect() default true;

    /**
     * 心跳频率（单位：秒）
     */
    int heartbeatRate() default 20;
}
