package com.hambug.Hambug.domain.board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "board_images")
@Getter
@NoArgsConstructor
public class BoardImage {

    @EmbeddedId
    private BoardImageId id;

    @MapsId("boardId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Board board;

    public BoardImage(BoardImageId id, Board board) {
        this.id = id;
        this.board = board;
    }

    public String getImageUrl() {
        return id != null ? id.getImageUrl() : null;
    }
}
