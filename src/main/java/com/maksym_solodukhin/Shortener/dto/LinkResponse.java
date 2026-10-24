package com.maksym_solodukhin.Shortener.dto;

public record LinkResponse(String shortCode, String shortUrl, String originalUrl) {}
