package com.exciting.common.util;

public class ExcitingStringUtils {

    /**
     * 首字母大写工具
     *
     * @param name name
     * @return String
     */
    public static String captureName(String name) {
        char[] cs=name.toCharArray();
        cs[0]-=32;
        return String.valueOf(cs);

    }
}
