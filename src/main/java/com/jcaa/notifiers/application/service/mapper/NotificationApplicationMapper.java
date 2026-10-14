package com.jcaa.notifiers.application.service.mapper;

import com.jcaa.notifiers.application.service.dto.command.SendNotificationCommand;
import com.jcaa.notifiers.domain.model.EmailDestinationModel;
import lombok.experimental.UtilityClass;

@UtilityClass
public class NotificationApplicationMapper {

  public EmailDestinationModel fromCommandToModel(final SendNotificationCommand command) {
    return new EmailDestinationModel(
        command.email(), command.nombre(), command.asunto(), command.mensaje());
  }
}
