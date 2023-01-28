package com.execute233.qqbot;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

/**
 * @author execute233
 **/
@Slf4j
public class HttpUtils {

    public static final HttpClient HTTP_CLIENT = HttpClients.createDefault();

    /**
     * 发送一个get请求到指定的url，含有url参数，请求头参数，请求体内容，请求内容类型.
     * @param url 访问url。
     * @param query url参数.
     * @param headers 请求头.
     * @return utf-8格式的文本数据.
     * **/
    public static String get(String url, Map<String, String> query,
                             Map<String, String> headers) {
        try {
            HttpGet get = new HttpGet(url);
            if (query != null) {
                URIBuilder builder = new URIBuilder(url);
                for (String key: query.keySet()) {
                    builder.addParameter(key, query.get(key));
                }
                get.setURI(builder.build());
            }
            if (headers != null) {
                for (String key: headers.keySet()) {
                    get.addHeader(key, headers.get(key));
                }
            }
            log.debug("post: " + Map.of("uri", get.getURI().toString(),
                    "header", Arrays.toString(get.getAllHeaders())));
            HttpResponse response = HTTP_CLIENT.execute(get);
            EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("请求" + url + "时发生错误", e);
        }
        return "";
    }
    public static String get(String url,Map<String, String> query) {
        return get(url, query, null);
    }
    public static String get(String url) {
        return get(url, null, null);
    }


    /**
     * 发送一个post请求到指定的url，含有url参数，请求头参数，请求体内容，请求内容类型.
     * @param url 访问url。
     * @param query url参数.
     * @param headers 请求头.
     * @param body 请求体正文.
     * @param contentType 请求数据类型.
     * @return utf-8格式的文本数据.
     * **/
    public static String post(String url, Map<String, String> query,
                              Map<String, String> headers, String body, ContentType contentType) {
        try {
            HttpPost post = new HttpPost(url);
            if (query != null) {
                URIBuilder builder = new URIBuilder(url);
                for (String key: query.keySet()) {
                    builder.addParameter(key, query.get(key));
                }
                post.setURI(builder.build());
            }
            if (headers != null) {
                for (String key: headers.keySet()) {
                    post.addHeader(key, headers.get(key));
                }
            }
            if (body != null) {
                post.setEntity(new StringEntity(body, contentType));
            }
            log.debug("post: " + Map.of("uri", post.getURI().toString(),
                    "header", Arrays.toString(post.getAllHeaders()),
                    "body", body == null? "" : body,
                    "contentType", contentType == null? "": contentType.getMimeType()));
            HttpResponse response = HTTP_CLIENT.execute(post);
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("请求" + url + "时发生错误", e);
        }
        return null;
    }
    public static String post(String url, Map<String, String> query, Map<String, String> headers) {
        return post(url, query, headers, null, null);
    }
    public static String post(String url,Map<String, String> query) {
        return post(url, query, null, null, null);
    }
    public static String post(String url) {
        return post(url, null, null, null,null);
    }

}
