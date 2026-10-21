package com.maksym_solodukhin.Shortener.repository;

import com.maksym_solodukhin.Shortener.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

//                                                  <Entity, type>
public interface LinkRepository extends JpaRepository<Link, Long> {
    Optional<Link> findByShortCode(String shortCode);
    boolean existsByShortCode(String shortCode);
}
