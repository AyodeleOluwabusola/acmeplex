package com.uofc.acmeplex.repository;

import com.uofc.acmeplex.entities.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Page<Ticket> findAllByEmail(Pageable pageable, String email);
    Optional<Ticket> findByCode(String code);
}
