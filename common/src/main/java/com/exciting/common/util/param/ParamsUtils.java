package com.exciting.common.util.param;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by ray.liu on 2017/4/13.
 */
public class ParamsUtils {

    /**
     * 把map参数转换成url可用的字符串参数
     *
     * @param paramsMap paramsMap
     * @return String
     */
    public  static String mapToString(Map<String, String> paramsMap) {
        StringBuilder urlBody = new StringBuilder();
        String result = null;
        paramsMap.forEach((k,v)->{
            if (v != null && !"class".equals(k)){
                urlBody.append(k).append("=").append(v).append("&");
            }
        });
        if (StringUtils.isNotBlank(urlBody)){
            result = String.valueOf(urlBody.deleteCharAt(urlBody.length()-1));
        }
        return result;
    }
}
