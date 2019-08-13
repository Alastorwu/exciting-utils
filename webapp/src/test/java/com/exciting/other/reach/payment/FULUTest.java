package com.exciting.other.reach.payment;

import com.alibaba.fastjson.JSON;
import com.exciting.common.util.DateUtils;
import com.exciting.common.util.httpclient.OkHttpUtil;
import org.apache.http.HttpException;
//import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class FULUTest {


    public static String MD5(String pwd) {
        try {
            // 创建加密对象
            MessageDigest digest = MessageDigest.getInstance("md5");

            // 调用加密对象的方法，加密的动作已经完成
            byte[] bs = digest.digest(pwd.getBytes());
            // 第一步，将数据全部转换成正数：
            String hexString = "";
            for (byte b : bs) {
                // 第一步，将数据全部转换成正数：
                // 解释：为什么采用b&255
                /*
                 * b:它本来是一个byte类型的数据(1个字节) 255：是一个int类型的数据(4个字节)
                 * byte类型的数据与int类型的数据进行运算，会自动类型提升为int类型 eg: b: 1001 1100(原始数据)
                 * 运算时： b: 0000 0000 0000 0000 0000 0000 1001 1100 255: 0000
                 * 0000 0000 0000 0000 0000 1111 1111 结果：0000 0000 0000 0000
                 * 0000 0000 1001 1100 此时的temp是一个int类型的整数
                 */
                int temp = b & 255;
                // 第二步，将所有的数据转换成16进制的形式
                // 注意：转换的时候注意if正数>=0&&<16，那么如果使用Integer.toHexString()，可能会造成缺少位数
                // 因此，需要对temp进行判断
                if (temp < 16 && temp >= 0) {
                    // 手动补上一个“0”
                    hexString = hexString + "0" + Integer.toHexString(temp);
                } else {
                    hexString = hexString + Integer.toHexString(temp);
                }
            }
            return hexString;
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public static String Sign(Map map,String sysSecret){

        char[] s =JSON.toJSONString(map).toCharArray();
        Arrays.sort(s);

        String outputSignOriginalStr = new String(s) + sysSecret;
        String sign = MD5(outputSignOriginalStr);
        return sign;
    }

    private static final String ALGORITHM = "AES/ECB/PKCS7Padding";
    private static String Aes256Encode(String str, byte[] key) {
        byte[] result = null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM, "BC");
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            result = cipher.doFinal(str.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(Base64.getEncoder().encode(result));
    }
    private static String Aes256Decode(String str, byte[] key) {

        byte[] bytes = Base64.getDecoder().decode(str);
        String result = null;
        try {
            //Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance(ALGORITHM, "BC");
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = cipher.doFinal(bytes);
            result = new String(decoded, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    @Test
    public void decode(){
        String s = Aes256Decode("12nCp6X/nALmrvr1erxK+HJ7Ur2MDbUXrfWqA2tlqU4=",
                "0a091b3aa4324435aab703142518a8f7".getBytes());
        System.out.println(s);
    }

    public static void main(String[] args) throws HttpException {
        String nowTime = DateUtils.localDateTimeToString(LocalDateTime.now(), DateUtils.format_ymdhms);
        String sysSecret = "0a091b3aa4324435aab703142518a8f7";
        Map<String,Object> dataJson = new LinkedHashMap<>();
        dataJson.put("app_key","i4esv1l+76l/7NQCL3QudG90Fq+YgVfFGJAWgT+7qO1Bm9o/adG/1iwO2qXsAXNB");
        dataJson.put("method", "fulu.order.card.add");
        dataJson.put("timestamp", nowTime);
        dataJson.put("version", "2.0");
        dataJson.put("format", "json");
        dataJson.put("charset", "utf-8");
        dataJson.put("sign_type", "md5");
        dataJson.put("app_auth_token", "");
        Map<String,String> biz_content = new LinkedHashMap<>();
        biz_content.put("customer_order_no",System.currentTimeMillis()+"");
        biz_content.put("product_id","10000497");
        biz_content.put("buy_num","2");
        dataJson.put("biz_content", JSON.toJSONString(biz_content));
        String sign = Sign(dataJson,sysSecret);
        dataJson.put("sign",sign);
        String toJSONString = JSON.toJSONString(dataJson);
        System.out.println(toJSONString);
        String url = "http://pre.openapi.fulu.com/api/getway";
        String post = OkHttpUtil.postJson(url, toJSONString);
        System.out.println(post);
    }
}
