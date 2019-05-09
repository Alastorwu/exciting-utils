/**
 * e-fuli.com Inc.
 * Copyright (c) 2015-2018 All Rights Reserved.
 */
package com.exciting.util;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;


/**
 * 
 * <pre>
 *
 * </pre>
 *
 * @author wujiaxing
 * @version $Id: Base64Util.java, v 0.1 2019年4月26日 下午5:16:49 wujiaxing Exp $
 */
public final class Base64Util {

    private Base64Util() {

    }

    /**
     * <pre>
     * 解密
     * </pre>
     * @param base64String
     * @return
     */
    public static byte[] decodeBase64(String base64String) {
            return Base64.decodeBase64(base64String);
    }

    /**
     * <pre>
     * 解码成字符串
     * </pre>
     * @param base64String
     * @return
     */
    public static String decodeBase64ToString(String base64String) {
        try {
            return new String(decodeBase64(base64String), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Decode base64 string error", e);
        }
    }

    /**
     * <pre>
     * 加密
     * </pre>
     * @param data
     * @return
     */
    public static byte[] encodeBase64(byte[] data) {
        return Base64.encodeBase64(data);
    }

    /**
     * <pre>
     * 编码返回字符串
     * </pre>
     * @param data
     * @return
     */
    public static String encodeBase64ToString(String data) {
        try {
            return new String(encodeBase64(data.getBytes("UTF-8")), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Ecode base64 string error", e);
        }
    }
}
