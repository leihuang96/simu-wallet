package io.github.leihuang96.exchange_rate_service.interceptor;

import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.HttpRequest;

import java.io.IOException;

public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // 打印请求信息
        System.out.println("Request URI: " + request.getURI());
        System.out.println("Request Headers: " + request.getHeaders());
        System.out.println("Request Body: " + new String(body));

        // 执行请求并获取响应
        ClientHttpResponse response = execution.execute(request, body);

        // 打印响应信息
        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Headers: " + response.getHeaders());
        return response;
    }
}