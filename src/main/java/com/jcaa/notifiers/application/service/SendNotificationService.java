package com.jcaa.notifiers.application.service;

import com.jcaa.notifiers.application.port.in.SendNotificationUseCase;
import com.jcaa.notifiers.application.port.out.EmailSenderPort;
import com.jcaa.notifiers.application.service.dto.command.SendNotificationCommand;
import com.jcaa.notifiers.application.service.mapper.NotificationApplicationMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Paso 4 del diagrama: envía con Brevo cada correo que llega por Kafka. */
@Service
@RequiredArgsConstructor
public class SendNotificationService implements SendNotificationUseCase {

  private final EmailSenderPort emailSenderPort;
  private final Validator validator;

  @Override
  public void execute(final SendNotificationCommand command) {
    validateCommand(command);
    emailSenderPort.send(NotificationApplicationMapper.fromCommandToModel(command));
  }

  private void validateCommand(final SendNotificationCommand command) {
    final Set<ConstraintViolation<SendNotificationCommand>> violations =
        validator.validate(command);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }
}
