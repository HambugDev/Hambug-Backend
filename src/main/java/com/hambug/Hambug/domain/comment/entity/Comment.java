package com.hambug.Hambug.domain.comment.entity;

import com.hambug.Hambug.domain.board.entity.Board;
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

    public Comment(String content, Board board) {
        this.content = content;
        this.board = board;
    }

    public void update(String content) {
        this.content = content;
    }
}
