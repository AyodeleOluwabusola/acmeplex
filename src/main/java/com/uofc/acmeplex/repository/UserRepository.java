package com.uofc.acmeplex.repository;


import com.uofc.acmeplex.entities.AcmePlexUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AcmePlexUser, Long> {
    Optional<AcmePlexUser> findByEmail(String username);

    boolean existsByEmail(String email);
}
