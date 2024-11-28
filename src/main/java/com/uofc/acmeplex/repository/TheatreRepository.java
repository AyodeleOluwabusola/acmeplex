package com.uofc.acmeplex.repository;

import com.uofc.acmeplex.entities.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheatreRepository extends JpaRepository<Theatre, Long> {
    boolean existsByName(String name);
}
