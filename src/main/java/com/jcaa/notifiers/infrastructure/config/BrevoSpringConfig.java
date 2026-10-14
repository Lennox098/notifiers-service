package com.jcaa.notifiers.infrastructure.config;

import com.jcaa.notifiers.infrastructure.adapter.email.BrevoConfig;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration(proxyBeanMethods = false)
public class BrevoSpringConfig {

  private static final String PROP_BASE_URL = "${brevo.base-url}";
  private static final String PROP_API_KEY = "${brevo.api-key}";
  private static final String PROP_FROM_ADDRESS = "${brevo.from-address}";
  private static final String PROP_FROM_NAME = "${brevo.from-name}";

  private static final String HEADER_API_KEY = "api-key";
  private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
  private static final Duration READ_TIMEOUT = Duration.ofSeconds(30);

  @Bean
  public RestClient brevoRestClient(
      @Value(PROP_BASE_URL) final String baseUrl, @Value(PROP_API_KEY) final String apiKey) {
    final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(CONNECT_TIMEOUT);
    requestFactory.setReadTimeout(READ_TIMEOUT);
    return RestClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader(HEADER_API_KEY, apiKey)
        .requestFactory(requestFactory)
        .build();
  }

  @Bean
  public BrevoConfig brevoConfig(
      @Value(PROP_FROM_ADDRESS) final String fromAddress,
      @Value(PROP_FROM_NAME) final String fromName) {
    return new BrevoConfig(fromAddress, fromName);
  }
}
