package com.uofc.acmeplex.repository;

import com.uofc.acmeplex.entities.PromotionCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PromotionCodeRepository extends JpaRepository<PromotionCode, Long> {

    boolean findByExpiryDateBeforeAndCode(LocalDate expiryDate, String code);

    Optional<PromotionCode> findByCode(String code);
    boolean existsByCode(String code);
}
