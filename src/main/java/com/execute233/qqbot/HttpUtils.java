package com.execute233.qqbot;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author execute233
 **/
@Slf4j
public class HttpUtils {

    public static final HttpClient HTTP_CLIENT = HttpClients.createDefault();

    /**
     * 发送一个post请求到指定的url，含有url参数，请求头参数，请求体内容，请求内容类型.
     * **/
    public static String post(String url, Map<String, String> parameters,
                              Map<String, String> headers, String body, ContentType contentType) {
        try {
            HttpPost post = new HttpPost(url);
            if (parameters != null) {
                URIBuilder builder = new URIBuilder(url);
                for (String key: parameters.keySet()) {
                    builder.addParameter(key,parameters.get(key));
                }
                post.setURI(builder.build());
            }
            if (headers != null) {
                for (String key: headers.keySet()) {
                    post.addHeader(key,headers.get(key));
                }
            }
            if (body != null) {
                post.setEntity(new StringEntity(body, contentType));
            }
            HttpResponse response = HTTP_CLIENT.execute(post);
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("{}", e);
        }
        return null;
    }

}
