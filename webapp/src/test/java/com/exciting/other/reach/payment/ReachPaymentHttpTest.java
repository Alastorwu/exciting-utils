package com.exciting.other.reach.payment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.exciting.common.util.httpclient.HttpClientUtils;
import com.exciting.common.util.param.ParamsUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ReachPaymentHttpTest {

    private static final String baseUrl = "https://admin.doooly.com/payment";
//    private static final String baseUrl = "http://localhost:8772/payment;

    private static String authorize() throws IOException {
        String url = baseUrl + "/auth/authorize";
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("client_id","Test_doooly_payment_kuping");
        //map.put("client_secret","");
        map.put("timestamp",System.currentTimeMillis()+"");
        map.put("client_secret","b2a571fc51b940e9907b998480dc5639");
        String paramString = ParamsUtils.mapToString(map);
        String sign = DigestUtils.md5Hex(paramString);
        map.put("sign",sign);
        map.remove("client_secret");
        return HttpClientUtils.httpPostJson(url, JSON.toJSONString(map));
    }

    private static String refund(String token) throws IOException {
        /*{
            "access_token":"${__property(access_token)}",
                "param":{
                    "businessId"        :"test_huochepiao",
                    "merchantOrderNo"   :"dly008921518834179",
                    "nonceStr"          :"xhd7m02ugkxfzsu7",
                    "storesId"          :"A001",
                    "clientIp"          :"58.33.116.244",
                    "notifyUrl"         :"http://test2.doooly.com/Doooly/jersey/doooly/payment/dooolyPayCallback",
                    "merchantRefundNo"  :"${__property(merchantRefundNo)}",
                    "refundPrice"       :"46",
                    "refundAmount"      :"46",
                    "orderDetail":[
                        {
                                "number"    :"1",
                                "amount"    :"46",
                                "code"      :"1001",
                                "price"     :"46",
                                "goods"     :"123123",
                                "tax"       :"0",
                                "category"  :"0000"
                        }
                    ],
                    "cardNumber":"155986133301",
                            "tradeType":"DOOOLY_JS"
                },
            "sign":"${sign_str3}",
                "client_id":"Test_doooly_payment_kuping",
                "timestamp":"${__time(/1000,)}"
        }*/
        String url = baseUrl + "mchpay/refund";
        JSONObject map = new JSONObject();
        map.put("client_id","Test_doooly_payment_kuping");
        map.put("access_token",token);
        JSONObject params = new JSONObject();
        params.put("businessId"      ,"test_7847584kupingfilm");
        params.put("merchantOrderNo" ,"dly008921518834179");
        params.put("nonceStr"        ,"xhd7m02ugkxfzsu7");
        params.put("storesId"        ,"A001");
        params.put("clientIp"        ,"58.33.116.244");
        params.put("notifyUrl"       ,"http://test2.doooly.com/Doooly/jersey/doooly/payment/dooolyPayCallback");
        params.put("merchantRefundNo","dly008921518834179");
        params.put("refundPrice"     ,"46");
        params.put("refundAmount"    ,"46");
        params.put("cardNumber"      ,"155986133301");
        params.put("tradeType"       ,"DOOOLY_JS");
        JSONArray orderDetails = new JSONArray();
        JSONObject orderDetail = new JSONObject();
        orderDetail.put("number"  ,"1");
        orderDetail.put("amount"  ,"46");
        orderDetail.put("code"    ,"1001");
        orderDetail.put("price"   ,"46");
        orderDetail.put("goods"   ,"123123");
        orderDetail.put("tax"     ,"0");
        orderDetail.put("category","0000");
        orderDetails.add(orderDetail);
        params.put("orderDetail"    ,orderDetails);
        map.put("param",JSON.toJSONString(params));
        map.put("timestamp",System.currentTimeMillis()+"");
        map.put("client_secret","b2a571fc51b940e9907b998480dc5639");
        String paramString = ParamsUtils.jsonobjectToString(map);
        String sign = DigestUtils.md5Hex(paramString);
        map.put("sign",sign);
        map.remove("client_secret");
        return HttpClientUtils.httpPostJson(url, map.toJSONString());
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
        String refund = refund(token);
        System.out.println(refund);
    }




}
