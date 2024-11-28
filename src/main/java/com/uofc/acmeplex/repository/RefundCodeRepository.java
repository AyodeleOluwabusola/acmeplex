package com.uofc.acmeplex.repository;

import com.uofc.acmeplex.entities.RefundCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface RefundCodeRepository extends JpaRepository<RefundCode, Long> {

    boolean findByExpiryDateBeforeAndCode(LocalDate expiryDate, String code);

    Optional<RefundCode> findByCode(String code);
    boolean existsByCode(String code);
}
