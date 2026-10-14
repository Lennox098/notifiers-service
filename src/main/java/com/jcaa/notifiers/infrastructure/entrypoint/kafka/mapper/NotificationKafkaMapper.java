package com.jcaa.notifiers.infrastructure.entrypoint.kafka.mapper;

import com.jcaa.notifiers.application.service.dto.command.SendNotificationCommand;
import com.jcaa.notifiers.infrastructure.entrypoint.kafka.dto.NotificationMessage;
import lombok.experimental.UtilityClass;

@UtilityClass
public class NotificationKafkaMapper {

  public SendNotificationCommand toCommand(final NotificationMessage message) {
    return new SendNotificationCommand(
        message.email(), message.nombre(), message.asunto(), message.mensaje());
  }
}
