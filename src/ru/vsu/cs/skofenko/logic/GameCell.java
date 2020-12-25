package ru.vsu.cs.skofenko.logic;

import java.util.Random;

public class GameCell {
    private int value;
    private boolean empty;

    public void makeEmpty() {
        empty = true;
    }

    public boolean isEmpty() {
        return empty;
    }

    public int getValue() {
        return value;
    }

    public void increaseValue() {
        value *= 2;
    }

    public GameCell() {
        empty = true;
    }

    public void fillCell(Random random) {
        int tmp = random.nextInt(10);
        if (tmp == 9) {
            value = 4;
        } else {
            value = 2;
        }
        empty = false;
    }

    public void swapWith(GameCell gameCell) {
        int temp = gameCell.value;
        gameCell.value = value;
        value = temp;

        boolean temp2 = gameCell.empty;
        gameCell.empty = empty;
        empty = temp2;
    }
}
