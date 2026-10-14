package com.jcaa.notifiers.infrastructure.entrypoint.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcaa.notifiers.application.port.in.SendNotificationUseCase;
import com.jcaa.notifiers.application.service.dto.command.SendNotificationCommand;
import com.jcaa.notifiers.domain.exception.InvalidNotificationException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("NotificationKafkaListener")
@ExtendWith(MockitoExtension.class)
class NotificationKafkaListenerTest {

  private static final String TOPIC = "notificaciones";

  @Mock private SendNotificationUseCase sendNotificationUseCase;

  private NotificationKafkaListener listener;

  @BeforeEach
  void setUp() {
    listener = new NotificationKafkaListener(sendNotificationUseCase, new ObjectMapper());
  }

  @Test
  @DisplayName("onMessage() convierte el JSON acordado en un comando de envío")
  void shouldTranslateAgreedJsonToCommand() {
    // Arrange
    final String payload =
        "{\"email\":\"ana@gmail.com\",\"nombre\":\"Ana Pérez\","
            + "\"asunto\":\"Bienvenida a Gestión de Usuarios\","
            + "\"mensaje\":\"<p>Hola Ana, tu usuario fue creado.</p>\"}";
    final ConsumerRecord<String, String> record = new ConsumerRecord<>(TOPIC, 0, 7L, null, payload);

    // Act
    listener.onMessage(record);

    // Assert
    verify(sendNotificationUseCase)
        .execute(
            new SendNotificationCommand(
                "ana@gmail.com",
                "Ana Pérez",
                "Bienvenida a Gestión de Usuarios",
                "<p>Hola Ana, tu usuario fue creado.</p>"));
  }

  @Test
  @DisplayName("onMessage() lanza InvalidNotificationException si el mensaje no es JSON")
  void shouldRejectNonJsonMessage() {
    // Arrange
    final ConsumerRecord<String, String> record =
        new ConsumerRecord<>(TOPIC, 0, 8L, null, "esto no es json");

    // Act
    final InvalidNotificationException exception =
        assertThrows(InvalidNotificationException.class, () -> listener.onMessage(record));

    // Assert
    assertEquals(
        InvalidNotificationException.becauseMessageIsUnreadable(null).getMessage(),
        exception.getMessage());
    verifyNoInteractions(sendNotificationUseCase);
  }
}
