package com.sky.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.properties.WeChatProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class HttpClientUtil implements DisposableBean {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WeChatProperties weChatProperties;

    private static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";
    private static final String GRANT_TYPE = "authorization_code";
    private static final int TIMEOUT_MSEC = 5 * 1000;
    private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom()
            .setConnectionRequestTimeout(TIMEOUT_MSEC)// 从连接池获取连接超时
            .setConnectTimeout(TIMEOUT_MSEC)// 建立tcp连接超时
            .setSocketTimeout(TIMEOUT_MSEC)// 数据读取超时
            .build();

    // 创建HttpClient连接池
    private final PoolingHttpClientConnectionManager clientConnectionManager;
    // 创建HttpClient对象
    private final CloseableHttpClient client;

    public HttpClientUtil() {
        clientConnectionManager = new PoolingHttpClientConnectionManager();
        client = HttpClients.custom()
                .setConnectionManager(clientConnectionManager)
                .setDefaultRequestConfig(REQUEST_CONFIG)
                .build();
        log.info("HttpClient对象和连接池已创建...");
    }

    public String doGet(String url, Map<String, String> paramMap) {

        String result = "";
        CloseableHttpResponse response = null;

        try {
            URIBuilder builder = new URIBuilder(url);
            if (paramMap != null) {
                for (Map.Entry<String, String> param : paramMap.entrySet()) {
                    builder.addParameter(param.getKey(), param.getValue());
                }
            }
            URI uri = builder.build();

            //创建GET请求
            HttpGet httpGet = new HttpGet(uri);

            //执行请求
            response = client.execute(httpGet);

            //判断响应状态
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                result = EntityUtils.toString(response.getEntity(), "UTF-8");
            } else {
                result = "请求失败：" + statusCode;
            }
        } catch (Exception e) {
            log.error("请求失败：{}", e.getMessage());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error("请求失败：{}", e.getMessage());
            }
        }

        return result;
    }

    public String doPost(String url, Map<String, String> paramMap) {

        String result = "";
        CloseableHttpResponse response = null;

        try {
            //创建Post请求
            HttpPost httpPost = new HttpPost(url);

            if (paramMap != null) {
                List<NameValuePair> paramList = new ArrayList();
                for (Map.Entry<String, String> param : paramMap.entrySet()) {
                    paramList.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }
                //模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
                //设置请求编码
                entity.setContentEncoding("utf-8");
                //设置数据类型
                entity.setContentType("application/x-www-form-urlencoded");
                httpPost.setEntity(entity);
            }

            //执行请求
            response = client.execute(httpPost);

            //判断响应状态
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                result = EntityUtils.toString(response.getEntity(), "UTF-8");
            } else {
                result = "请求失败：" + statusCode;
            }
        } catch (Exception e) {
            log.error("请求失败：{}", e.getMessage());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error("请求失败：{}", e.getMessage());
            }
        }

        return result;
    }

    public String doPost4Json(String url, Map<String, String> paramMap) {

        String result = "";
        CloseableHttpResponse response = null;

        try {
            //创建Post请求
            HttpPost httpPost = new HttpPost(url);

            if (paramMap != null) {
                StringEntity entity = new StringEntity(objectMapper.writeValueAsString(paramMap), "utf-8");
                //设置请求编码
                entity.setContentEncoding("utf-8");
                //设置数据类型
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
            }

            //执行请求
            response = client.execute(httpPost);

            //判断响应状态
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                result = EntityUtils.toString(response.getEntity(), "UTF-8");
            } else {
                result = "请求失败：" + statusCode;
            }
        } catch (Exception e) {
            log.error("请求失败：{}", e.getMessage());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error("请求失败：{}", e.getMessage());
            }
        }

        return result;
    }

    public String getOpenid(String code) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appid", weChatProperties.getAppid());
        paramMap.put("secret", weChatProperties.getSecret());
        paramMap.put("js_code", code);
        paramMap.put("grant_type", GRANT_TYPE);

        String resultJSON = doGet(WX_LOGIN_URL, paramMap);
        Map<String, String> resultMap = null;
        try {
            resultMap = objectMapper.readValue(resultJSON, Map.class);
        } catch (JsonProcessingException e) {
            log.error("解析微信返回数据失败：{}", e.getMessage());
        }
        if (resultMap == null) {
            return null;
        }
        return resultMap.get("openid");
    }

    @Override
    public void destroy() throws IOException {
        client.close();
        clientConnectionManager.close();
        log.info("HttpClient对象和连接池已关闭...");
    }
}
