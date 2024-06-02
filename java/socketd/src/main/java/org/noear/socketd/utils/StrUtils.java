package org.noear.socketd.utils;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * 字会串工具
 *
 * @author noear
 * @since 2.0
 */
public class StrUtils {
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
}
