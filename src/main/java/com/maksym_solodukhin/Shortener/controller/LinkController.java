package com.maksym_solodukhin.Shortener.controller;

import com.maksym_solodukhin.Shortener.dto.CreateLinkRequest;
import com.maksym_solodukhin.Shortener.dto.LinkResponse;
import com.maksym_solodukhin.Shortener.entity.Link;
import com.maksym_solodukhin.Shortener.service.LinkService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class LinkController {
    
    private final LinkService service;

    public LinkController(LinkService service) {
        this.service = service;
    }

    @PostMapping("/api/links")
    public LinkResponse create(@Valid @RequestBody CreateLinkRequest request) {
        Link link = service.create(request.url());
        return new LinkResponse(
            link.getShortCode(),
            "http://localhost:8080/" + link.getShortCode(),
            link.getOriginalUrl()
        );
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        Link link = service.getByCode(code);
        return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(link.getOriginalUrl()))
        .build();
    }
}
