package com.hambug.Hambug.global.fcm.repository;

import com.hambug.Hambug.global.fcm.entity.FcmDeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FcmDeviceTokenRepository extends JpaRepository<FcmDeviceToken, Long> {
    Optional<FcmDeviceToken> findByUserId(long userId);
}