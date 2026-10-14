package com.jcaa.notifiers.application.service.dto.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendNotificationCommand(
    @NotBlank @Email String email,
    @NotBlank String nombre,
    @NotBlank String asunto,
    @NotBlank String mensaje) {}
