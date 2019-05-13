package com.exciting.webapp.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.codec.Charsets.UTF_8;

@Slf4j
public class EncryptDecryptUtil {
	private static Integer AES_LENGTH = 128;
	private static Integer RSA_LENGTH = 1024;
	private static String AES_ALGORITHM = "AES";
	private static String DES_ALGORITHM = "DES";
	private static String RSA_ALGORITHM = "RSA";

	// 工作模式/填充方式
	private static String ALGORITHM_MODE = "/CBC/PKCS5Padding";

	/**
	 * AES加密算法
	 * 
	 * @author Alastor
	 * @date 创建时间：2018年7月3日 下午2:31:28
	 * @version 1.0
	 * @parameter content 待加密字符串
	 * @parameter password 加密密钥
	 * @since
	 * @return
	 */
	public static String encryptByAES(String content, String password) {
		// 加密结果
		String result = "";
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
            // 初始化密钥 需要手动设置
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());
            keyGenerator.init(AES_LENGTH,random);
			// 生成密钥key
			SecretKey secretKey = keyGenerator.generateKey();
			// 转换为AES专用密钥
			SecretKeySpec sks = new SecretKeySpec(secretKey.getEncoded(), AES_ALGORITHM);
			// 创建密码器
			Cipher cipher = Cipher.getInstance(AES_ALGORITHM + ALGORITHM_MODE);
            // 初始化加密模式的密码器
            //CBC模式必须提供初始向量IvParameterSpec，不然报错 java.security.InvalidKeyException: Parameters missing
            cipher.init(Cipher.ENCRYPT_MODE, sks, new IvParameterSpec(getIV()));
			byte[] resultByte = cipher.doFinal(content.getBytes(UTF_8));
			result = new String(Hex.encodeHex(resultByte));

		} catch (Exception e) {
			log.error("encryptByAES error=" + e.getMessage(), e);
		}
		return result;
	}

    /**
     * 指定一个初始化向量 (Initialization vector，IV)，IV 必须是16位
     */
    public static byte[] getIV() throws Exception {
        return "asdfivh7".getBytes(UTF_8);
    }

    /**
	 * DES算法解密
	 * 
	 * @author Alastor
	 * @date 创建时间：2018年7月3日 下午2:35:50
	 * @version 1.0
	 * @parameter content 待解密的字节码
	 * @parameter password 解密密钥
	 * @since
	 * @return
	 */
	public static String decryptByAES(byte[] content, String password) {
		String result = "";
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
			// 初始化密钥 需要手动设置
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());
			keyGenerator.init(AES_LENGTH,random);
			// 生成密钥key
			SecretKey secretKey = keyGenerator.generateKey();
			// 转换为AES专用密钥
			SecretKeySpec sks = new SecretKeySpec(secretKey.getEncoded(), AES_ALGORITHM);
			// 创建密码器
			Cipher cipher = Cipher.getInstance(AES_ALGORITHM + ALGORITHM_MODE);
			// 初始化加密模式的密码器
            //CBC模式必须提供初始向量IvParameterSpec，不然报错 java.security.InvalidKeyException: Parameters missing
			cipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec(getIV()));
			result = new String(cipher.doFinal(content), UTF_8);

		} catch (Exception e) {
			log.error("encryptByAES error=" + e.getMessage(), e);
		}
		return result;
	}

    /**
     * AES算法解密
     *
     * @author Alastor
     * @date 创建时间：2018年7月3日 下午2:35:50
     * @version 1.0
     * @parameter content 待解密的字符串
     * @parameter password 解密密钥
     * @since
     * @return
     */
    public static String decryptByAES(String content, String password) {
        String result = "";
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
            // 初始化密钥 需要手动设置
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());
            keyGenerator.init(AES_LENGTH,random);
            // 生成密钥key
            SecretKey secretKey = keyGenerator.generateKey();
            // 转换为AES专用密钥
            SecretKeySpec sks = new SecretKeySpec(secretKey.getEncoded(), AES_ALGORITHM);
            // 创建密码器
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM + ALGORITHM_MODE);
            // 初始化加密模式的密码器
            //CBC模式必须提供初始向量IvParameterSpec，不然报错 java.security.InvalidKeyException: Parameters missing
            cipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec(getIV()));
            result = new String(cipher.doFinal(Hex.decodeHex(content.toCharArray())), UTF_8);

        } catch (Exception e) {
            log.error("encryptByAES error=" + e.getMessage(), e);
        }
        return result;
    }

    /**
     * DES算法加密
     *
     * @author Alastor
     * @date 创建时间：2018年7月3日 下午2:31:28
     * @version 1.0
     * @parameter content 待加密字符串
     * @parameter password 加密密钥
     * @since
     * @return
     */
	// public static String encrptByDES(String content, String password) {
	// // 加密结果
	// String result = "";
	// try {
	// KeyGenerator keyGenerator = KeyGenerator.getInstance(DES_ALGORITHM);
	// // 初始化密钥
	// keyGenerator.init(new SecureRandom(password.getBytes()));
	// // 生成密钥key
	// SecretKey secretKey = keyGenerator.generateKey();
	// // 转换为DES专用密钥
	// SecretKeySpec sks = new SecretKeySpec(secretKey.getEncoded(),
	// DES_ALGORITHM);
	// // 创建密码器
	// Cipher cipher = Cipher.getInstance(DES_ALGORITHM + ALGORITHM_MODE);
	// // 初始化加密模式的密码器
	// cipher.init(Cipher.ENCRYPT_MODE, sks);
	// byte[] resultByte =
	// cipher.doFinal(content.getBytes(UTF_8));
	// result = Base64.encodeBase64String(resultByte);
	// } catch (Exception e) {
	// log.error("encrptByAES error=" + e.getMessage(), e);
	// }
	// return result;
	// }

	/**
	 * DES算法解密
	 * 
	 * @author Alastor
	 * @date 创建时间：2018年7月3日 下午2:35:50
	 * @version 1.0
	 * @parameter content 待解密的字节码
	 * @parameter password 解密密钥
	 * @since
	 * @return
	 */
	// public static String decrptByDES(byte[] content, String password) {
	// String result = "";
	// try {
	// KeyGenerator keyGenerator = KeyGenerator.getInstance(DES_ALGORITHM);
	// // 初始化密钥
	// keyGenerator.init(new SecureRandom(password.getBytes()));
	// // 生成密钥key
	// SecretKey secretKey = keyGenerator.generateKey();
	// // 转换为DES专用密钥
	// SecretKey sks = new SecretKeySpec(secretKey.getEncoded(), DES_ALGORITHM);
	// // 创建密码器
	// Cipher cipher = Cipher.getInstance(DES_ALGORITHM + ALGORITHM_MODE);
	// // 初始化加密模式的密码器
	// cipher.init(Cipher.DECRYPT_MODE, sks);
	// result = new String(cipher.doFinal(content),
	// UTF_8);
	//
	// } catch (Exception e) {
	// log.error("encrptByAES error=" + e.getMessage(), e);
	// }
	// return result;
	// }
	/**
	 * DES算法加密
	 * 
	 * @author Alastor
	 * @date 创建时间：2018年7月3日 下午4:34:54
	 * @version 1.0
	 * @parameter
	 * @since
	 * @return
	 */
	public static String encryptByDES(String content, String key) {
		String result = "";
		try {
			// 生成密钥Key
			Key desKey = desKeyGenerator(key);
			IvParameterSpec viSpec = new IvParameterSpec(key.getBytes(UTF_8));
			Cipher cipher = Cipher.getInstance(DES_ALGORITHM + ALGORITHM_MODE);
			// 初始化加密模式密码器
			cipher.init(Cipher.ENCRYPT_MODE, desKey, new IvParameterSpec(getIV()));
			byte[] byteResult = cipher.doFinal(content.getBytes(UTF_8));
			result = Base64.encodeBase64String(byteResult);
		} catch (Exception e) {
			log.error("encryptByAES error=" + e.getMessage(), e);
		}
		return result;
	}

	/**
	 * DES算法解密
	 * 
	 * @author Alastor
	 * @date 创建时间：2018年7月3日 下午4:34:54
	 * @version 1.0
	 * @parameter
	 * @since
	 * @return
	 */
	public static String decryptByDES(String content, String key) {
		String result = "";
		try {
			// 生成密钥Key
			Key desKey = desKeyGenerator(key);
			IvParameterSpec viSpec = new IvParameterSpec(key.getBytes(UTF_8));
			Cipher cipher = Cipher.getInstance(DES_ALGORITHM + ALGORITHM_MODE);
			// 初始化加密模式密码器
			cipher.init(Cipher.DECRYPT_MODE, desKey, new IvParameterSpec(getIV()));
			byte[] byteResult = cipher.doFinal(Base64.decodeBase64(content.getBytes(UTF_8)));
			result = new String(byteResult, UTF_8);
		} catch (Exception e) {
			log.error("encryptByAES error=" + e.getMessage(), e);
		}
		return result;
	}

	/**
	 * RSA算法：公钥加密,私钥解密-加密
	 * 
	* @author  Alastor 
	* @date 创建时间：2018年7月4日 上午10:45:39 
	* @version 1.0 
	* @param content 待加密字符串
	* @param publicKey RSA 公钥加密key
	* @since  
	* @return 经base64编码的字符串
	 */
	public static String encryptByRSAPublicKey(String content, String publicKey){
		String result = "";
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
			X509EncodedKeySpec x509EncodeKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
			RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(x509EncodeKeySpec);
			Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
			byte[] resultBytes = cipher.doFinal(content.getBytes(UTF_8));
			result = Base64.encodeBase64String(resultBytes);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("encryptByRSAPublicKey error=" + e.getMessage(), e);
		}
		return result;
	}
	/**
	 * RSA算法：公钥加密,私钥解密-解密
	 * 
	 * @author  Alastor 
	 * @date 创建时间：2018年7月4日 上午10:45:39 
	 * @version 1.0 
	 * @param content 待解密字符串
	 * @param privateKey rsa解密私钥key  
	 * @since  
	 * @return 解密后的字符串
	 */
	public static String decryptByRSAPrivateKey(String content, String privateKey){
		String result = "";
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
			PKCS8EncodedKeySpec pkcs8EncodeKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
			RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8EncodeKeySpec);
			Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
			byte[] resultBytes = cipher.doFinal(Base64.decodeBase64(content));
			result = new String(resultBytes, UTF_8);
		} catch (Exception e) {
			log.error("decryptByRSAPrivateKey error=" + e.getMessage(), e);
		}
		return result;
	}

	/**
	 * RSA算法：私钥加密,公钥解密-解密
	 * 
	* @author  Alastor 
	* @date 创建时间：2018年7月4日 上午10:45:39 
	* @version 1.0 
	* @param content 待解密的字符串
	* @param publicKey rsa公钥key 
	* @since  
	* @return 解密后的字符串
	 */
	public static String decryptByRSAPublicKey(String content, String publicKey){
		String result = "";
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
			X509EncodedKeySpec x509EncodeKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
			RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(x509EncodeKeySpec);
			Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, rsaPublicKey);
			byte[] resultBytes = cipher.doFinal(Base64.decodeBase64(content));
			result = new String(resultBytes, UTF_8);
		} catch (Exception e) {
			log.error("decryptByRSAPublicKey error=" + e.getMessage(), e);
		}
		return result;
	}
	/**
	 * RSA算法：私钥加密,公钥解密-加密
	 * 
	 * @author  Alastor 
	 * @date 创建时间：2018年7月4日 上午10:45:39 
	 * @version 1.0 
	 * @param content 待加密字符串
	 * @param privateKey rsa私钥key
	 * @since  
	 * @return 
	 */
	public static String encryptByRSAPrivateKey(String content, String privateKey){
		String result = "";
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
			PKCS8EncodedKeySpec pkcs8EncodeKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
			RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8EncodeKeySpec);
			Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, rsaPrivateKey);
			byte[] resultBytes = cipher.doFinal(content.getBytes(UTF_8));
			result = Base64.encodeBase64String(resultBytes);
		} catch (Exception e) {
			log.error("encryptByRSAPrivateKey error=" + e.getMessage(), e);
		}
		return result;
	}

	
	/**
	 * 创建RSA算法公私钥Key
	 * 
	 * @author Alastor
	 * @date 创建时间：2018年7月4日 上午10:09:45
	 * @version 1.0
	 * @parameter
	 * @since
	 * @return
	 */
	public static Map<String, String> createRSAKeys() {
		Map<String, String> rsaMap = new HashMap<>();
		try {
			// 创建密钥对实例
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
			// 初始化密钥对长度
			keyPairGenerator.initialize(RSA_LENGTH);
			// 生成密钥对
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			// 生成公钥key
			Key publicKey = keyPair.getPublic();
			// 生成私钥key
			Key privateKey = keyPair.getPrivate();
			rsaMap.put("publicKey", Base64.encodeBase64String(publicKey.getEncoded()));
			rsaMap.put("privateKey", Base64.encodeBase64String(privateKey.getEncoded()));
		} catch (Exception e) {
			log.error("createRSAKeys error=" + e.getMessage(), e);
		}

		return rsaMap;
	}

	/**
	 * 创建DES 密钥key
	 * 
	 * @author Alastor
	 * @date 创建时间：2018年7月3日 下午4:26:15
	 * @version 1.0
	 * @parameter
	 * @since
	 * @return
	 */
	private static SecretKey desKeyGenerator(String key) throws Exception {
		DESKeySpec desKey = new DESKeySpec(key.getBytes(UTF_8));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES_ALGORITHM);
		return keyFactory.generateSecret(desKey);
	}
}
