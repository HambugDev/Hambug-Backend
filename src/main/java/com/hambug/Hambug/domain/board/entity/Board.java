package com.hambug.Hambug.domain.board.entity;


import com.hambug.Hambug.domain.user.entity.User;
import com.hambug.Hambug.global.timeStamped.Timestamped;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@ToString(exclude = {"images", "user"})
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

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardImage> images = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long viewCount = 0L;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long commentCount = 0L;

    @Builder
    public Board(String title, String content, Category category, List<String> imageUrls, User user) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.user = user;
        this.viewCount = 0L;
        this.commentCount = 0L;
        setImagesFromUrls(imageUrls);
    }

    public void update(String title, String content, Category category, List<String> imageUrls) {
        this.title = title;
        this.content = content;
        this.category = category;
        setImagesFromUrls(imageUrls);
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    // Backward compatible getter used by DTOs and services
    public List<String> getImageUrls() {
        return images.stream().map(BoardImage::getImageUrl).collect(Collectors.toList());
    }

    public void setImagesFromUrls(List<String> imageUrls) {
        this.images.clear();
        if (imageUrls == null) return;
        for (String url : imageUrls) {
            BoardImage image = new BoardImage(new BoardImageId(null, url), this);
            this.images.add(image);
        }
    }

    public void incrementCommentCount() {
        this.commentCount++;
    }

    public void decrementCommentCount() {
        this.commentCount--;
    }

    public Boolean isAuthor(Long userId) {
        if (userId == null || this.user == null) {
            return false;
        }
        try {
            return this.user.getId().equals(userId);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return false;
        }
    }
}
