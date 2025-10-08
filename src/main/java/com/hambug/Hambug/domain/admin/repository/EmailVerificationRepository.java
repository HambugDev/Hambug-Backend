package com.hambug.Hambug.domain.admin.repository;

import com.hambug.Hambug.domain.admin.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
}
