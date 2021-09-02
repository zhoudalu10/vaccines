package com.yexin997.vaccines.helper;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author zhou.dalu
 * @date 2021/9/2
 */
public class HttpHelper {

    private static final Integer WECHAT_SUCCESS = 0;

    private static String bindingUrlParam(String url, Map<String, Object> params) {
        StringBuilder paramList = new StringBuilder();
        if (null != params) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                paramList.append(entry.getKey()).append("=");
                paramList.append(entry.getValue().toString()).append("&");
            }
            if (paramList.length() > 0) {
                paramList.deleteCharAt(paramList.length() - 1);
                url = url + "?" + paramList;
            }
        }
        return url;
    }

    private static void bindingRequestBody(Map<String, Object> requestBody, HttpPost post) {
        if (null == requestBody || requestBody.isEmpty()) {
            return;
        }
        StringEntity requestEntity = null;
        try {
            String reqBody = JSONObject.toJSONString(requestBody);
            System.out.println(reqBody);
            requestEntity = new StringEntity(reqBody);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        post.setEntity(requestEntity);
    }

    private static void bindingRequestBody(List<Object> requestBody, HttpPost post) {
        if (null == requestBody) {
            return;
        }
        StringEntity requestEntity = null;
        try {
            String reqBody = JSONObject.toJSONString(requestBody);
            System.out.println(reqBody);
            requestEntity = new StringEntity(reqBody);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        post.setEntity(requestEntity);
    }

    private static JSONObject getResponse(CloseableHttpClient client, HttpRequestBase httpRequest, boolean encoded) {
        try (CloseableHttpResponse response = client.execute(httpRequest)) {
            String value = EntityUtils.toString(response.getEntity());
            System.out.println("http response:" + value);
            JSONObject obj = JSONObject.parseObject(value);
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                //成功接口不是全部返回errorcode字段
                if (null != obj.getInteger("errcode") && !WECHAT_SUCCESS.equals(obj.getInteger("errcode"))) {
                    System.out.println(value);
                } else {
                    return obj;
                }
            }
        } catch (IOException e) {
            System.out.println("WechatHttpHelper getResponse() " + e);
        }
        return null;
    }

    public static JSONObject httpGet(String url, Map<String, Object> params, Map<String, String> headers) {
        System.out.println("*******http get start******");
        System.out.println("params -> " + params);
        url = bindingUrlParam(url, params);
        System.out.println("url -> " + url);
        HttpGet httpGet = new HttpGet(url);
        if (headers != null) {
            System.out.println("headers -> " + headers);
            headers.forEach((k, v) -> httpGet.addHeader(k, v));
        }
        JSONObject res = getResponse(HttpClients.createDefault(), httpGet, false);
        System.out.println("*******http get end******");
        System.out.println();
        return res;
    }

    public static JSONObject httpPost(String url, Map<String, Object> params, Map<String, Object> requestBody, Map<String, String> headers) {
        System.out.println("*******http post strat******");
        System.out.println("params -> " + params);
        url = bindingUrlParam(url, params);
        System.out.println("url -> " + url);
        HttpPost httpPost = new HttpPost(url);
        System.out.println("requestBody -> " + requestBody);
        bindingRequestBody(requestBody, httpPost);
        if (headers != null) {
            System.out.println("headers -> " + headers);
            headers.forEach((k, v) -> httpPost.addHeader(k, v));
        }
        JSONObject res = getResponse(HttpClients.createDefault(), httpPost, false);
        System.out.println("*******http post end******");
        System.out.println();
        return res;
    }

    public static JSONObject httpPost(String url, Map<String, Object> params, List<Object> requestBody, Map<String, String> headers) {
        System.out.println("*******http post strat******");
        System.out.println("params -> " + params);
        url = bindingUrlParam(url, params);
        System.out.println("url -> " + url);
        HttpPost httpPost = new HttpPost(url);
        System.out.println("requestBody -> " + requestBody);
        bindingRequestBody(requestBody, httpPost);
        if (headers != null) {
            System.out.println("headers -> " + headers);
            headers.forEach((k, v) -> httpPost.addHeader(k, v));
        }
        JSONObject res = getResponse(HttpClients.createDefault(), httpPost, false);
        System.out.println("*******http post end******");
        System.out.println();
        return res;
    }


}
