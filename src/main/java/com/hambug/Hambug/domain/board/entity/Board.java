package com.hambug.Hambug.domain.board.entity;


import com.hambug.Hambug.domain.user.entity.User;
import com.hambug.Hambug.global.timeStamped.Timestamped;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Board extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @ElementCollection
    @CollectionTable(name = "board_images", joinColumns = @JoinColumn(name = "board_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public Board(String title, String content, Category category, List<String> imageUrls, User user) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.imageUrls = imageUrls;
        this.user = user;
    }

    public void update(String title, String content, Category category, List<String> imageUrls) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.imageUrls = imageUrls;
    }
}
