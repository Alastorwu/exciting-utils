package com.exciting.other.reach.payment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.exciting.common.util.httpclient.HttpClientUtils;
import com.exciting.common.util.param.ParamsUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.util.LinkedHashMap;

public class ReachPaymentHttpTest {

    private static final String baseUrl = "https://admin.doooly.com/payment";
//    private static final String baseUrl = "http://localhost:8772/payment;

    private static String authorize() throws IOException {
        String url = baseUrl + "/auth/authorize";
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("client_id","Product_doooly_payment_zuzu");
        //map.put("client_secret","");
        map.put("timestamp",System.currentTimeMillis()+"");
        map.put("client_secret","a4f3f219100947b48d7e63a3af040cb6");
        String paramString = ParamsUtils.mapToString(map);
        String sign = DigestUtils.md5Hex(paramString);
        map.put("sign",sign);
        map.remove("client_secret");
        return HttpClientUtils.httpPostJson(url, JSON.toJSONString(map));
    }

    private static String getToken() throws IOException {
        String res = authorize();
        JSONObject resObject = JSONObject.parseObject(res);
        assert 1000 != resObject.getIntValue("code");
        JSONObject data = resObject.getJSONObject("data");
        assert data!=null;
        return data.getString("access_token");
    }

    public static void main(String[] args) throws Exception {
        String token = getToken();
        System.out.println(token);
    }




}
