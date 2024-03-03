package org.example.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class TetrominoTest {
    /**
     * Тестує хешкод блоків тетроміно на наявність колізій
     */
    @Test
    public void hashCodeCollisions() {
        int size = 15 * 20;
        Set<Tetromino.Block> allBlocks = new HashSet<>(size);

        for (int x = 0; x < 15; x++) {
            for (int y = 0; y < 20; y++) {
                allBlocks.add(new Tetromino.Block(x, y, null));
            }
        }

        Assertions.assertEquals(size, allBlocks.size());
    }
}