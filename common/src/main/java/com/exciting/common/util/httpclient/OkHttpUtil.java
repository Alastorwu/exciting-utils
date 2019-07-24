package com.exciting.common.util.httpclient;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.http.HttpException;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OkHttpUtil {

    public static final MediaType XML_MEDIA_TYPE = MediaType.parse("application/xml");

    public static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");

    private static OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .sslSocketFactory(createSSLSocketFactory())
            .hostnameVerifier(new TrustAllHostnameVerifier()).build();


    private static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }


    /**
     * okhttp get请求需要封装好url
     *
     * @param url url，需要拼接好参数
     * @return 结果字符串
     * @throws HttpException http 请求异常
     */
    public static String get(String url) throws HttpException {
        return getWithClient(url, client);
    }

    /**
     * okhttp get请求
     *
     * @param url
     * @param paramsMap 参数列表
     * @return java.lang.String
     * @author jim
     * @date 2018/5/7
     */
    public static String get(String url, HashMap<String, String> paramsMap) throws HttpException {
        return get(url + '?' + buildUrlParams(paramsMap));
    }

    private static String buildUrlParams(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (result.length() > 0) {
                result.append("&");
            }
            result.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return result.toString();
    }

    /**
     * okhttp get请求需要封装好url
     *
     * @param url url，需要拼接好参数
     * @return 结果byte[]
     * @throws HttpException http 请求异常
     */
    public static byte[] getByte(String url) throws HttpException {
        Request request = new Request.Builder().url(url).build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body() != null ? response.body().bytes() : null;
            } else {
                throw new HttpException("http请求异常" +  response);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new HttpException("http请求异常", e);
        } finally {
            if (null != response && null != response.body()) {
                response.body().close();
            }
        }
    }

    /**
     * post 请求
     *
     * @param url       url
     * @param paramsMap 请求参数map
     * @return 请求结果
     * @throws HttpException 异常
     */
    public static String post(String url, Map<String, String> paramsMap) throws HttpException {
        Request request;
        if (!paramsMap.isEmpty()) {
            FormBody.Builder builder = new FormBody.Builder();
            for (String key : paramsMap.keySet()) {
                builder.add(key, paramsMap.get(key));
            }
            RequestBody requestBody = builder.build();
            request = new Request.Builder().url(url).post(requestBody).build();
        } else {
            request = new Request.Builder().url(url).build();
        }

        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                throw new HttpException("http请求异常");
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new HttpException("http请求异常", e);
        } finally {
            if (null != response && response.body() != null) {
                response.body().close();
            }
        }
    }

    public static Response postXml(String url, String xmlStr) throws HttpException {
        RequestBody body = RequestBody.create(XML_MEDIA_TYPE, xmlStr);
        Request request = new Request.Builder().url(url).post(body)
                .addHeader("Content-Type", "application/xml; charset=utf-8").build();
        Response response = null;
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new HttpException("http请求异常", e);
        } finally {
            if (response != null && response.body() != null) {
                response.body().close();
            }
        }
    }

    public static String postXmlStr(String url, String xmlStr) throws HttpException {
        RequestBody body = RequestBody.create(XML_MEDIA_TYPE, xmlStr);
        Request request = new Request.Builder().url(url).post(body).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                throw new HttpException("http请求异常" +  response);
            }
        } catch (IOException e) {
            log.error("",e);
            throw new HttpException(e.getMessage());
        }
    }

    public static String postRequest(Request request) throws HttpException {
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                throw new HttpException("http请求异常" +  response);
            }
        } catch (IOException e) {
            log.error("",e);
            throw new HttpException(e.getMessage());
        }
    }

    /**
     *
     * 功能描述: dotw启用gzip压缩
     *
     * @auther: silence
     * @date: 2018/11/4 16:38
     * @company: jollycorp
     */
    public static Response postXmlDotw(String url, String xmlStr) throws HttpException {
        RequestBody body = RequestBody.create(XML_MEDIA_TYPE, xmlStr);
        Request request = new Request.Builder().url(url).post(body)
                .addHeader("Content-Type", "application/xml; charset=utf-8").addHeader("Content-Encoding", "gzip").build();
        Response response = null;
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new HttpException("http请求异常", e);
        } finally {
            if (response != null && response.body() != null) {
                response.body().close();
            }
        }
    }

    public static String postJson(String url, String json) throws HttpException {
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, json);
        Request request = new Request.Builder().url(url).post(body)
                .addHeader("Content-Type", "application/json; charset=utf-8").build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body() != null ? response.body().string() : null;
            } else {
                throw new HttpException("http请求异常" +  response);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new HttpException("http请求异常", e);
        } finally {
            if (null != response && null != response.body()) {
                response.body().close();
            }
        }
    }

    /**
     * 发送get请求
     *
     * @param url
     * @param httpClient
     * @return
     * @throws HttpException
     */
    public static String getWithClient(String url, OkHttpClient httpClient) throws HttpException {
        Request request = new Request.Builder().url(url).build();
        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body() != null ? response.body().string() : null;
            } else {
                throw new HttpException("http请求异常" +  response);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new HttpException("http请求异常", e);
        } finally {
            if (null != response && null != response.body()) {
                response.body().close();
            }
        }
    }
}
