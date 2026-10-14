package com.jcaa.notifiers.infrastructure.config;

import com.jcaa.notifiers.domain.exception.InvalidNotificationException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration(proxyBeanMethods = false)
public class KafkaConsumerSpringConfig {

  // Si Brevo falla, se reintenta 3 veces cada 5 s; después el mensaje se registra y se descarta
  private static final long RETRY_INTERVAL_MS = 5_000L;
  private static final long MAX_RETRIES = 3L;

  @Bean
  public CommonErrorHandler kafkaErrorHandler() {
    final DefaultErrorHandler errorHandler =
        new DefaultErrorHandler(new FixedBackOff(RETRY_INTERVAL_MS, MAX_RETRIES));
    // Un mensaje mal formado nunca va a funcionar: no tiene sentido reintentarlo
    errorHandler.addNotRetryableExceptions(
        InvalidNotificationException.class, ConstraintViolationException.class);
    return errorHandler;
  }
}
