package com.exciting.common.util.security;

import org.apache.xmlbeans.impl.util.Base64;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 *
 * @author Alastor
 * @date 2019-7-24
 * @version 1.0
 */
public class RSAUtils {

    /** *//**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";

    /** *//**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /** *//**
     * 获取公钥的key
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";

    /** *//**
     * 获取私钥的key
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /** *//**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /** *//**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /** *//**
     * <p>
     * 生成密钥对(公钥和私钥)
     * </p>
     *
     * @return
     * @throws Exception
     */
    public static Map<String, Object> genKeyPair(int keySize) throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(keySize);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    /** *//**
     * <p>
     * 用私钥对信息生成数字签名
     * </p>
     *
     * @param data 已加密数据
     * @param privateKey 私钥(BASE64编码)
     *
     * @return
     * @throws Exception
     */
    public static byte[] sign(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64Utils.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return signature.sign();
    }
    public static String sign(String dataString, String privateKey) throws Exception {
        byte[] sign = sign(Base64Utils.decode(dataString), privateKey);
        return Base64Utils.encode(sign);
    }

    /** *//**
     * <p>
     * 校验数字签名
     * </p>
     *
     * @param data 已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign 数字签名
     *
     * @return
     * @throws Exception
     *
     */
    public static boolean verify(byte[] data, String publicKey, String sign)
            throws Exception {
        byte[] keyBytes = Base64Utils.decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(Base64Utils.decode(sign));
    }
    public static boolean verify(String dataString, String publicKey, String sign)
            throws Exception {
        return verify(Base64Utils.decode(dataString), publicKey, sign);
    }

    /** *//**
     * <P>
     * 私钥解密
     * </p>
     *
     * @param encryptedData 已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey)
            throws Exception {
        byte[] keyBytes = Base64Utils.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }
    public static String decryptByPrivateKey(String dataString, String privateKey)
            throws Exception {
        byte[] bytes = decryptByPrivateKey(Base64Utils.decode(dataString), privateKey);
        return new String(bytes);
    }

    /** *//**
     * <p>
     * 公钥解密
     * </p>
     *
     * @param encryptedData 已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey)
            throws Exception {
        byte[] keyBytes = Base64Utils.decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }
    public static String decryptByPublicKey(String dataString, String publicKey)
            throws Exception {
        byte[] bytes = decryptByPublicKey(Base64Utils.decode(dataString), publicKey);
        return new String(bytes);
    }

    /** *//**
     * <p>
     * 公钥加密
     * </p>
     *
     * @param data 源数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey)
            throws Exception {
        byte[] keyBytes = Base64Utils.decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }
    public static String encryptByPublicKey(String dataString, String publicKey)
            throws Exception {
        byte[] bytes = encryptByPublicKey(dataString.getBytes(), publicKey);
        return Base64Utils.encode(bytes);
    }

    /** *//**
     * <p>
     * 私钥加密
     * </p>
     *
     * @param data 源数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey)
            throws Exception {
        byte[] keyBytes = Base64Utils.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }
    public static String encryptByPrivateKey(String dataString, String privateKey)
            throws Exception {
        byte[] bytes = encryptByPrivateKey(dataString.getBytes(), privateKey);
        return Base64Utils.encode(bytes);
    }

    /** *//**
     * <p>
     * 获取私钥
     * </p>
     *
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return Base64Utils.encode(key.getEncoded());
    }

    /** *//**
     * <p>
     * 获取公钥
     * </p>
     *
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return Base64Utils.encode(key.getEncoded());
    }

    /*public static void main(String[] args) throws Exception {
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
    }*/


    /*public static void main(String[] args) throws Exception {
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
    }*/

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