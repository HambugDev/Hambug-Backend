package com.hambug.Hambug.domain.burger.repository;

import com.hambug.Hambug.domain.burger.entity.Burger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BurgerRepository extends JpaRepository<Burger, Long> {

    boolean existsByFranchiseAndMenuName(String franchise, String menuName);

    @Query(value = "SELECT * FROM burger ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Burger> findRandomBurgers(int limit);
}
