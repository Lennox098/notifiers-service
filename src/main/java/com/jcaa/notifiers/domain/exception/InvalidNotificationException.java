package com.jcaa.notifiers.domain.exception;

public final class InvalidNotificationException extends DomainException {

  private static final String MESSAGE_UNREADABLE =
      "El mensaje de Kafka no tiene el formato acordado (email, nombre, asunto, mensaje).";

  private InvalidNotificationException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public static InvalidNotificationException becauseMessageIsUnreadable(final Throwable cause) {
    return new InvalidNotificationException(MESSAGE_UNREADABLE, cause);
  }
}
