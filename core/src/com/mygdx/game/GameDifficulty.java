package com.mygdx.game;

public enum GameDifficulty {
    EMPTY(0, 0),
    EASY(3, 25),
    MEDIUM(4, 50),
    HARD(5, 99);

    private final int size;
    private final int maxNumber;

    GameDifficulty(int size, int maxNumber) {
        this.size = size;
        this.maxNumber = maxNumber;
    }

    public int getSize() {
        return size;
    }

    public int getMaxNumber() {
        return maxNumber;
    }
}