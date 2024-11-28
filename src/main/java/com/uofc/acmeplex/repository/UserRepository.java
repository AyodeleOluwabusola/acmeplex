package com.uofc.acmeplex.repository;


import com.uofc.acmeplex.dto.request.IUserDetails;
import com.uofc.acmeplex.entities.AcmePlexUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AcmePlexUser, Long> {
    Optional<AcmePlexUser> findByEmail(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u.email as email, u.firstName as firstName FROM AcmePlexUser u")
    List<IUserDetails> findAllEmailsAndFirstName();
}
