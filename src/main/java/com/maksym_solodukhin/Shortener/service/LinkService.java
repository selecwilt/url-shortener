package com.maksym_solodukhin.Shortener.service;

import com.maksym_solodukhin.Shortener.entity.Link;
import com.maksym_solodukhin.Shortener.exception.LinkNotFoundException;
import com.maksym_solodukhin.Shortener.repository.LinkRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class LinkService {
    
    private static final String ALPHABET = 
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final SecureRandom random = new SecureRandom();
    private final LinkRepository repository;

    public LinkService(LinkRepository repository){
        this.repository = repository;
    }

    public Link create(String originalUrl) {
        Link link = new Link();
        link.setOriginalUrl(originalUrl);
        link.setShortCode(generateUniqueCode());
        return repository.save(link);
    }

    public Link getByCode(String code) {
        return repository.findByShortCode(code)
            .orElseThrow(() -> new LinkNotFoundException(code));
    }

    private String generateUniqueCode() {
        String code;
        do {
            StringBuilder sb = new StringBuilder(7);
            for (int i = 0; i < 7; i++){
                sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
            }
            code = sb.toString();
        } while (repository.existsByShortCode(code));
        return code;
    }
}
