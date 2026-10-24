package com.maksym_solodukhin.Shortener.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateLinkRequest(@NotBlank String url) {}

