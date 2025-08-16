package com.hambug.Hambug.domain.user.repository;

import com.hambug.Hambug.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndIsActive(String email, boolean isActive);

    Optional<User> findByEmail(String email);
}
