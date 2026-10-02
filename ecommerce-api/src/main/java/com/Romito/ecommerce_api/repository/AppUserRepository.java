package com.Romito.ecommerce_api.repository;

import com.Romito.ecommerce_api.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM AppUser u JOIN FETCH u.role WHERE u.username = :username")
    Optional<AppUser> findByUsernameWithRole(@Param("username") String username);
}