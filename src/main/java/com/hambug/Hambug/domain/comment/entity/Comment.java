package com.hambug.Hambug.domain.comment.entity;

import com.hambug.Hambug.domain.board.entity.Board;
import com.hambug.Hambug.domain.user.entity.User;
import com.hambug.Hambug.global.timeStamped.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Comment(String content, Board board, User user) {
        this.content = content;
        this.board = board;
        this.user = user;
    }

    public void update(String content) {
        this.content = content;
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
