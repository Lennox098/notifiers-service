package com.jcaa.notifiers.domain.exception;

public final class EmailSenderException extends DomainException {

  private static final String MESSAGE_PROVIDER_FAILED =
      "El proveedor de correo rechazó el envío. Detalle: %s";

  private EmailSenderException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public static EmailSenderException becauseProviderFailed(final Throwable cause) {
    return new EmailSenderException(
        String.format(MESSAGE_PROVIDER_FAILED, cause.getMessage()), cause);
  }
}
