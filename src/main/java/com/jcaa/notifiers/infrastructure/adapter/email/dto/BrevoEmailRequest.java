package com.jcaa.notifiers.infrastructure.adapter.email.dto;

import java.util.List;

/** Cuerpo de POST https://api.brevo.com/v3/smtp/email. */
public record BrevoEmailRequest(
    BrevoContact sender, List<BrevoContact> to, String subject, String htmlContent) {

  public record BrevoContact(String email, String name) {}
}
