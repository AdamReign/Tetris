package org.tetris.gameplay.board;

public class BoardMapper {
    public static BoardDTO toDTO(Board board) {
        return new BoardDTO(board);
    }

    public static Board toModel(BoardDTO boardDTO) {
        return null;
    }
}