package com.maksym_solodukhin.Shortener.service;

import com.maksym_solodukhin.Shortener.entity.Link;
import com.maksym_solodukhin.Shortener.exception.LinkNotFoundException;
import com.maksym_solodukhin.Shortener.repository.LinkRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LinkServiceTest {
    
    @Mock
    LinkRepository repository;

    @InjectMocks
    LinkService service;

    @Test 
    void create_savesLinkWithGeneratedCode() {
        when(repository.existsByShortCode(anyString())).thenReturn(false);
        when(repository.save(any(Link.class))).thenAnswer(inv -> inv.getArgument(0));

        Link result = service.create("https://github.com");

        assertEquals("https://github.com", result.getOriginalUrl());
        assertEquals(7, result.getShortCode().length());
        verify(repository).save(any(Link.class));
    }

    @Test 
    void create_generatesNewCodeIfFirstOneIsTaken() {
        when(repository.existsByShortCode(anyString())).thenReturn(true, false);
        when(repository.save(any(Link.class))).thenAnswer(inv -> inv.getArgument(0));

        service.create("https://github.com");

        verify(repository, times(2)).existsByShortCode(anyString());
    }

    @Test
    void getByCode_throwsWhenNotFound() {
        when(repository.findByShortCode("nope")).thenReturn(Optional.empty());

        assertThrows(LinkNotFoundException.class, () -> service.getByCode("nope"));
    }
}
