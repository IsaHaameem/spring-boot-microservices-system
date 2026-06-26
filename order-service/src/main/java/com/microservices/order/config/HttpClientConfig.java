package com.microservices.order.config;

import com.microservices.order.client.ProductServiceClient;
import com.microservices.order.client.UserServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class HttpClientConfig {

    @Bean
    public UserServiceClient userServiceClient(
            @Value("${app.clients.user-service.base-url}") String baseUrl) {
        return buildClient(baseUrl, UserServiceClient.class);
    }

    @Bean
    public ProductServiceClient productServiceClient(
            @Value("${app.clients.product-service.base-url}") String baseUrl) {
        return buildClient(baseUrl, ProductServiceClient.class);
    }

    private <T> T buildClient(String baseUrl, Class<T> clientInterface) {
        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory())
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();

        return factory.createClient(clientInterface);
    }

    private ClientHttpRequestFactory requestFactory() {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(3));
        return requestFactory;
    }

}