package com.maksym_solodukhin.Shortener.dto;
import java.time.Instant;

public record ErrorResponse(int status, String error, String message, Instant timestamp) {}
