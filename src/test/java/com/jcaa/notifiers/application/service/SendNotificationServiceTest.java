package com.jcaa.notifiers.application.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.jcaa.notifiers.application.port.out.EmailSenderPort;
import com.jcaa.notifiers.application.service.dto.command.SendNotificationCommand;
import com.jcaa.notifiers.domain.model.EmailDestinationModel;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("SendNotificationService")
@ExtendWith(MockitoExtension.class)
class SendNotificationServiceTest {

  @Mock private EmailSenderPort emailSenderPort;

  private SendNotificationService service;

  @BeforeEach
  void setUp() {
    try (final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
      service = new SendNotificationService(emailSenderPort, validatorFactory.getValidator());
    }
  }

  @Test
  @DisplayName("execute() envía el correo con los datos del mensaje")
  void shouldSendEmail() {
    // Arrange
    final SendNotificationCommand command =
        new SendNotificationCommand("ana@gmail.com", "Ana Pérez", "Bienvenida", "<p>Hola</p>");

    // Act
    service.execute(command);

    // Assert
    final ArgumentCaptor<EmailDestinationModel> captor =
        ArgumentCaptor.forClass(EmailDestinationModel.class);
    verify(emailSenderPort).send(captor.capture());
    assertAll(
        "correo enviado",
        () -> assertEquals("ana@gmail.com", captor.getValue().getDestinationEmail()),
        () -> assertEquals("Ana Pérez", captor.getValue().getDestinationName()),
        () -> assertEquals("Bienvenida", captor.getValue().getSubject()),
        () -> assertEquals("<p>Hola</p>", captor.getValue().getBody()));
  }

  @Test
  @DisplayName("execute() rechaza mensajes incompletos sin llamar a Brevo")
  void shouldRejectIncompleteMessage() {
    // Arrange
    final SendNotificationCommand command =
        new SendNotificationCommand("no-es-email", "Ana", null, "<p>Hola</p>");

    // Act & Assert
    assertThrows(ConstraintViolationException.class, () -> service.execute(command));
    verifyNoInteractions(emailSenderPort);
  }
}
