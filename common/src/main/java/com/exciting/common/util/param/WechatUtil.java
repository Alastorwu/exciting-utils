package com.exciting.common.util.param;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 微信支付工具类
 * Created by liangjun on 2017/5/18.
 */
public class WechatUtil {
    /** 日志*/
    private static Logger logger = LoggerFactory.getLogger(WechatUtil.class);
    /***/
    private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5","6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
    /**编码*/
    public static String CHARACTER_ENCODING = "UTF-8";
    /**
     * 创建签名
     * @param characterEncoding
     * @param parameters
     * @param key
     * @return
     */
    public static String createSign(String characterEncoding, SortedMap<Object,Object> parameters, String key){
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();//所有参与传参的参数按照accsii排序（升序）
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            Object v = entry.getValue();
            if(null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)&& !"class".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + key);
        String sign = WechatUtil.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
        return sign;
    }

    /**
     * 获取随机数 32位
     * @return
     */
    public static String getNonceStr() {
        Random random = new Random();
        return WechatUtil.MD5Encode(String.valueOf(random.nextInt(10000)), CHARACTER_ENCODING);
    }

    /**
     * 微信md5加密
     * @param origin
     * @param charsetname
     * @return
     */
    public static String MD5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname)) {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
            }else {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
            }
        } catch (Exception exception) {

        }
        return resultString;
    }

    /**
     * @Description：将请求参数转换为xml格式的string
     * @param parameters  请求参数
     * @return
     */
    public static String getRequestXml(SortedMap<Object,Object> parameters){
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            String v = (String)entry.getValue();
            if ("attach".equalsIgnoreCase(k)||"body".equalsIgnoreCase(k)) {
                sb.append("<" + k + ">" + "<![CDATA[" + v + "]]></" + k + ">");
            }else {
                sb.append("<" + k + ">" + v + "</" + k + ">");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }

    /**
     * 用SHA1算法生成安全签名
     * @param jsapiTicket 票据
     * @param timeStamp 时间戳
     * @param nonceStr 随机字符串
     * @param url url
     * @return 安全签名
     * @throws NoSuchAlgorithmException
     */
    public static String getSign(String jsapiTicket, String timeStamp, String nonceStr, String url) throws NoSuchAlgorithmException {
        String signature = "";
        String str = "jsapi_ticket=" + jsapiTicket +
                "&noncestr=" + nonceStr +
                "&timestamp=" + timeStamp +
                "&url=" + url;
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(str.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return signature;
    }

    /**
     * 返回给微信的参数
     * @param success
     * @param msg
     * @return
     */
    public static String returnXml(boolean success, String msg){
        String returnXml = "<xml>" +
                "<return_code><![CDATA[" + (success ? "SUCCESS" : "FAIL")+ "]]></return_code>" +
                "<return_msg><![CDATA[" + msg + "]]></return_msg>" +
                "</xml>";
        return returnXml;
    }

    /**
     * app端预付费结果
     * @param appid appid
     * @param partnerid 商户号
     * @param prepayid 预支付交易会话ID
     * @param key
     * @return
     * @throws Exception
     */
    public static Map<String, String> getWechatPrepay(String appid, String partnerid, String prepayid, String key) throws Exception {
        String nonceStr = WechatUtil.getNonceStr();
        String timestamp = WechatUtil.getTimeStamp();
        // 生成sign签名
        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("appid", appid);
        parameters.put("partnerid", partnerid);
        parameters.put("prepayid", prepayid);
        parameters.put("package", "Sign=WXPay");
        parameters.put("noncestr", nonceStr);
        parameters.put("timestamp", timestamp);
        String sign = createSign(WechatUtil.CHARACTER_ENCODING, parameters, key);
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("appid", appid);
        resultMap.put("partnerid", partnerid);
        resultMap.put("prepayid", prepayid);
        resultMap.put("package", "Sign=WXPay");
        resultMap.put("noncestr", nonceStr);
        resultMap.put("timestamp", timestamp);
        resultMap.put("sign", sign);
        return resultMap;
    }

    /**
     * @description 将xml字符串转换成map
     * @param xml
     * @return Map
     */
    public static Map<String,String> readStringXmlOut(String xml) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            // 将字符串转为XML
            Document doc = DocumentHelper.parseText(xml);
            // 获取根节点
            Element rootElt = doc.getRootElement();
            // 获取根节点下所有节点
            List<Element> list = rootElt.elements();
            // 遍历节点
            for (Element element : list) {
                // 节点的name为map的key，text为map的value
                map.put(element.getName(), element.getText());
            }
        } catch (DocumentException e) {
            e.printStackTrace();
            logger.error("xml转换异常:" + e.getMessage() + "");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("xml转换异常2:" + e.getMessage() + "");
        }
        return map;
    }


    /**
     * 时间戳
     * @return
     */
    public static String getTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash){
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++){
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0){
            n += 256;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

}
