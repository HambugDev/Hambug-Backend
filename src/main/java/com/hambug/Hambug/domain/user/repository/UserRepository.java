package com.hambug.Hambug.domain.user.repository;

import com.hambug.Hambug.domain.user.entity.LoginType;
import com.hambug.Hambug.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.socialId = :socialId AND u.loginType = :loginType AND  u.deletedAt is null ")
    Optional<User> findBySocialIdAndLoginType(String socialId, LoginType loginType);

    @Query("SELECT u FROM User u WHERE u.nickname = :nickname AND u.deletedAt is null ")
    Optional<User> findByNickname(String nickname);

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.deletedAt is null")
    Optional<User> findByIdAndDeleteIsNull(Long id);
}
