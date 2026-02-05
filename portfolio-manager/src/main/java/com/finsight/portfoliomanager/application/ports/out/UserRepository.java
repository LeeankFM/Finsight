package com.finsight.portfoliomanager.application.ports.out;

import java.util.Optional;
import java.util.UUID;

import com.finsight.portfoliomanager.domain.User;

public interface UserRepository {

    Optional<User> findById(UUID userId);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    User save(User user);
}
