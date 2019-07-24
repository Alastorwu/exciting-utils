package com.exciting.common.util.security;


import org.junit.Test;

import java.util.Map;

public class RSAUtilsTest {

    @Test
    public void genKeyPair() throws Exception {
        Map<String, Object> keyMap = RSAUtils.genKeyPair(1024);
        String publicKey = RSAUtils.getPublicKey(keyMap);
        String privateKey = RSAUtils.getPrivateKey(keyMap);
        System.err.println("公钥: \n\r" + publicKey);
        System.err.println("私钥： \n\r" + privateKey);
        System.err.println("公钥加密——私钥解密");
        String source = "这是一行没有任何意义的文字，你看完了等于没看，不是吗？";
        System.out.println("\r加密前文字：\r\n" + source);
        String encodedData = RSAUtils.encryptByPublicKey(source, publicKey);
        System.out.println("加密后文字：\r\n" + encodedData);
        String decodedData = RSAUtils.decryptByPrivateKey(encodedData, privateKey);
        System.out.println("解密后文字: \r\n" + decodedData);
    }


    @Test
    public void encrypt() throws Exception {
        Map<String, Object> keyMap = RSAUtils.genKeyPair(1024);
        String publicKey = RSAUtils.getPublicKey(keyMap);
        String privateKey = RSAUtils.getPrivateKey(keyMap);
        System.err.println("私钥加密——公钥解密");
        String source = "这是一行测试RSA数字签名的无意义文字";
        System.out.println("原文字：\r\n" + source);
        String encode = RSAUtils.encryptByPrivateKey(source, privateKey);
        System.out.println("加密后：\r\n" + encode);
        String decode = RSAUtils.decryptByPublicKey(encode, publicKey);
        System.out.println("解密后: \r\n" + decode);
        System.err.println("私钥签名——公钥验证签名");
        String sign = RSAUtils.sign(encode, privateKey);
        System.err.println("签名:\r" + sign);
        boolean status = RSAUtils.verify(encode, publicKey, sign);
        System.err.println("验证结果:\r" + status);
    }

    public static void main(String[] args) throws Exception {
        String publicKey =
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCL06hAZMhnXO/GhP5g05hAhdk4" +
                        "PG0LZXChbqbdrMhPOaDkuLvvdmVVQHNBaVpPGNOBei6r7qvzQabpdsIiL4r5xjXo" +
                        "QKG7wVVwYj5K8XkUxHVz1LUrpTIhaosIuPVb3duMGDsjwJM8u0dNuM09CJV8GWGU" +
                        "dNSULA3dFIPwJGveuwIDAQAB";
        String privateKey =
                "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIvTqEBkyGdc78aE" +
                        "/mDTmECF2Tg8bQtlcKFupt2syE85oOS4u+92ZVVAc0FpWk8Y04F6Lqvuq/NBpul2" +
                        "wiIvivnGNehAobvBVXBiPkrxeRTEdXPUtSulMiFqiwi49Vvd24wYOyPAkzy7R024" +
                        "zT0IlXwZYZR01JQsDd0Ug/Aka967AgMBAAECgYBSJNzyP4LwHkwKnzTl40jNlxR+" +
                        "khkpmcVqQRwsnIXs7RkVZWWbDz4GsF9FbUQNtbMDnKjwfr1rogURoJ/5/14v8SJ0" +
                        "2z25w7R6m87fMpZKQ9AvI/6FIC4Gvc0TtvZ9ft5toiGUqoBC2/yJJH4V6XHtj1Qb" +
                        "18h0/z6D06jVaTQCAQJBANCeBs4hqqfXBiSl3kdD0RJrTEZvgv/+2DnnpiJ1x9BR" +
                        "kEFLf1SW0XRCFzmPv+ycSyRt7hBJzmTLZqy9E45JUQECQQCrldUwLW/7AXYpIfgl" +
                        "9ZYT/gvyejuna470Tgaz34DC4DoV4egLHVfoHuQiK42dm1nyg4KskGEZNseJmMaX" +
                        "OrO7AkBwxjyfigubF942SYRTuhF3h5GAMh/7C2UQSG3DCzCtKKp24b0/mRg5ZNDx" +
                        "SQhTfFoSo2qiW4O7cUlb8Ap2TKQBAkAF1U4uGR1Zdy9BLrp07+huPZDCaY1ln654" +
                        "d76altqVxXG0FL4CwrieV46B6uEhnopsSDRFEZN7e/VEpED8Gy7JAkEAgC2eS9wt" +
                        "sCbpW+guy7F6eiAYlZXipvyWcX+e+zE2JLHdR2qSoszs8Sx5bthtU7E46tclKK+v" +
                        "GPTSay8VFUPZZQ==";
        System.err.println("私钥加密——公钥解密");
        String source = "这是一行测试RSA数字签名的无意义文字";
        System.out.println("原文字：\r\n" + source);
        String encode = RSAUtils.encryptByPrivateKey(source, privateKey);
        System.out.println("加密后：\r\n" + encode);
        String decode = RSAUtils.decryptByPublicKey(encode, publicKey);
        System.out.println("解密后: \r\n" + decode);
        System.err.println("私钥签名——公钥验证签名");
        String sign = RSAUtils.sign(encode, privateKey);
        System.err.println("签名:\r" + sign);
        boolean status = RSAUtils.verify(encode, publicKey, sign);
        System.err.println("验证结果:\r" + status);
    }
}