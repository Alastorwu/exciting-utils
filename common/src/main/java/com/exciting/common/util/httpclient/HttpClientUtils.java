package com.exciting.common.util.httpclient;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLContext;

@Slf4j
public class HttpClientUtils {

    private static final int TIME_OUT = 10 * 1000;
    private static PoolingHttpClientConnectionManager cm = null;

    static {
        LayeredConnectionSocketFactory sslsf = null;
        try {
            sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault());
        } catch (NoSuchAlgorithmException e) {
            log.error("创建SSL连接失败...");
        }
        assert sslsf != null;
        Registry<ConnectionSocketFactory> sRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslsf)
                .register("http", new PlainConnectionSocketFactory())
                .build();
        cm = new PoolingHttpClientConnectionManager(sRegistry);
        // 设置最大的连接数
        cm.setMaxTotal(200);
        // 设置每个路由的基础连接数【默认，每个路由基础上的连接不超过2个，总连接数不能超过20】
        cm.setDefaultMaxPerRoute(20);
    }

    private static CloseableHttpClient getHttpClient() {
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm).build();
        assert httpClient != null;
        return httpClient;
    }

    /**
     * 发送get请求
     *
     * @param url 路径
     * @return String
     */
    public static String httpGet(String url) throws IOException {


        // 配置请求超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(TIME_OUT).setConnectionRequestTimeout(TIME_OUT)
                .setSocketTimeout(TIME_OUT).build();
        // 发送get请求
        HttpGet request = new HttpGet(url);
        request.setConfig(requestConfig);
        return doget(request);

    }



    public static String httpPost(String url, Map<String, String> params) throws IOException {
        return httpPost(url, params, CharEncoding.UTF_8);
    }


    public static String httpPost(String url, Map<String, String> params, String charset) throws IOException {
        // 配置请求超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(TIME_OUT).setConnectionRequestTimeout(TIME_OUT)
                .setSocketTimeout(TIME_OUT).build();
        // 发送get请求
        HttpPost request = new HttpPost(url);
        request.setConfig(requestConfig);
        //设置参数
        List<NameValuePair> list = new ArrayList<>();
        params.forEach((k, v) -> list.add(new BasicNameValuePair(k, v)));
        if (list.size() > 0) {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
            request.setEntity(entity);
        }
        return dopost(request);
    }


    /**
     * 发送post请求
     *
     * @param url    url
     * @param params params
     * @return String
     * @throws IOException IOException
     */
    public static String httpPostJson(String url, String params) throws IOException {
        return httpPostJson(url, params, CharEncoding.UTF_8);
    }

    /**
     * 发送post请求
     *
     * @param url     url
     * @param params  params
     * @param charset charset
     * @return String
     * @throws IOException IOException
     */
    public static String httpPostJson(String url, String params, String charset) throws IOException {
        // 配置请求超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(TIME_OUT).setConnectionRequestTimeout(TIME_OUT)
                .setSocketTimeout(TIME_OUT).build();
        // 发送get请求
        HttpPost request = new HttpPost(url);
        request.setHeader("Content-Type", "application/json");
        request.setConfig(requestConfig);
        //设置参数
        StringEntity entity = new StringEntity(params, charset);
        entity.setContentType("text/json");
        entity.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
        request.setEntity(entity);
        return dopost(request);
    }

    private static String doget(HttpGet request) throws IOException {
        CloseableHttpClient httpClient = getHttpClient();
        // get请求返回结果

        @Cleanup CloseableHttpResponse response = httpClient.execute(request);
        // 请求发送成功，并得到响应
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            // 读取服务器返回过来的json字符串数据
            return EntityUtils.toString(response.getEntity());
        } else {
            log.error("get请求提交失败:" + request.getURI().getPath());
        }
        return null;
    }

    private static String dopost(HttpPost request) throws IOException {
        CloseableHttpClient httpClient = getHttpClient();
        // get请求返回结果

        @Cleanup CloseableHttpResponse response = httpClient.execute(request);
        // 请求发送成功，并得到响应
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            // 读取服务器返回过来的json字符串数据
            String res = EntityUtils.toString(response.getEntity());
            log.info("post请求成功 response={}",res);
            return res;
        } else {
            log.error("post请求提交失败:" + request.getURI().getPath());
        }
        return null;
    }




    public static void main(String[] args) throws Exception {
        String res = httpGet("https://www.baidu.com");
        System.out.println(res);
    }


}
