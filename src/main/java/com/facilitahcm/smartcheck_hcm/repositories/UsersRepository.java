package com.facilitahcm.smartcheck_hcm.repositories;

import com.facilitahcm.smartcheck_hcm.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<UserDetails> findByLogin(String login);
}
