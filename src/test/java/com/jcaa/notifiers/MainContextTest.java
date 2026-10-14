package com.jcaa.notifiers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.jcaa.notifiers.application.port.out.EmailSenderPort;
import com.jcaa.notifiers.infrastructure.adapter.email.BrevoEmailSenderAdapter;
import com.jcaa.notifiers.infrastructure.entrypoint.kafka.NotificationKafkaListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Contexto de Spring")
class MainContextTest {

  @Autowired private EmailSenderPort emailSenderPort;
  @Autowired private NotificationKafkaListener notificationKafkaListener;

  @Test
  @DisplayName("el consumidor de Kafka y el adaptador de Brevo quedan registrados")
  void shouldWireListenerAndBrevoAdapter() {
    // Arrange — contexto levantado por @SpringBootTest

    // Act & Assert
    assertAll(
        () -> assertInstanceOf(BrevoEmailSenderAdapter.class, emailSenderPort),
        () -> assertNotNull(notificationKafkaListener));
  }
}
