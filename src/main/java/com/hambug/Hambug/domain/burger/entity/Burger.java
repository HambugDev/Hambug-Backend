package com.hambug.Hambug.domain.burger.entity;

import com.hambug.Hambug.global.timeStamped.Timestamped;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "burger")
public class Burger extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String menuImage;

    @Column(nullable = false, length = 100)
    private String franchise;

    @Column(nullable = false, length = 100)
    private String menuName;

    @Column(columnDefinition = "TEXT")
    private String menuDescription;

    @Builder
    public Burger(Long id, String menuImage, String franchise, String menuName, String menuDescription) {
        this.id = id;
        this.menuImage = menuImage;
        this.franchise = franchise;
        this.menuName = menuName;
        this.menuDescription = menuDescription;
    }
}
