package com.uofc.acmeplex.repository;

import com.uofc.acmeplex.entities.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Page<Card> findAllByUserEmail(String email, Pageable pageable);
}
