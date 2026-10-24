package com.maksym_solodukhin.Shortener.dto;

import com.maksym_solodukhin.Shortener.validation.ValidHttpUrl;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateLinkRequest(
    @NotBlank @Size(max = 2048) @ValidHttpUrl String url) {}

