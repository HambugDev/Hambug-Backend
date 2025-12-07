package com.hambug.Hambug.domain.board.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardImageId implements Serializable {

    @Column(name = "board_id")
    private Long boardId;

    @Column(name = "image_url", nullable = false, length = 1024)
    private String imageUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardImageId that = (BoardImageId) o;
        return Objects.equals(boardId, that.boardId) && Objects.equals(imageUrl, that.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardId, imageUrl);
    }
}
