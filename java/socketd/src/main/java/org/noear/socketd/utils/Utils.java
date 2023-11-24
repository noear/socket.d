package org.noear.socketd.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * 工具
 *
 * @author noear
 * @since 2.0
 */
public class Utils {
    /**
     * 生成 guid
     * */
    public static String guid(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 检查字符串是否为空
     *
     * @param s 字符串
     */
    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    /**
     * 检查集合是否为空
     *
     * @param s 集合
     */
    public static boolean isEmpty(Collection s) {
        return s == null || s.size() == 0;
    }

    public static boolean isEmpty(Map s) {
        return s == null || s.size() == 0;
    }

    /**
     * 检查字符串是否为非空
     *
     * @param s 字符串
     */
    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    /**
     * 检查集合是否非空
     *
     * @param s 集合
     */
    public static boolean isNotEmpty(Collection s) {
        return !isEmpty(s);
    }


    /**
     * 解包异常
     *
     * @param ex 异常
     */
    public static Throwable throwableUnwrap(Throwable ex) {
        Throwable th = ex;

        while (true) {
            if (th instanceof InvocationTargetException) {
                th = ((InvocationTargetException) th).getTargetException();
            } else if (th instanceof UndeclaredThrowableException) {
                th = ((UndeclaredThrowableException) th).getUndeclaredThrowable();
            } else if (th.getClass() == RuntimeException.class) {
                if (th.getCause() != null) {
                    th = th.getCause();
                } else {
                    break;
                }
            } else {
                break;
            }
        }

        return th;
    }
}
