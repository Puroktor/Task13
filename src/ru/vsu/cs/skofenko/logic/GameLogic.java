package ru.vsu.cs.skofenko.logic;

import java.util.Random;

public class GameLogic {

    private final Random RND = new Random();
    private GameState gameState;
    private int score = 0;
    private final int rowCount;
    private final int columnCount;
    private final GameCell[][] field;
    int filledCells = 0;

    public int getScore() {
        return score;
    }

    public GameState getGameState() {
        return gameState;
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public GameCell getCell(int row, int column) {
        return field[row][column];
    }

    public GameLogic(int rowCount, int columnCount) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        field = new GameCell[rowCount][columnCount];
        initGame();
    }

    private void initGame() {
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                field[i][j] = new GameCell();
            }
        }
        for (int i = 0; i < 2; i++) {
            createCell();
        }
        gameState = GameState.PLAYING;
    }

    private void createCell() {
        int index = RND.nextInt(rowCount * columnCount - filledCells);
        int nowIndex = 0;
        outerLoop:
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                if (field[i][j].isEmpty()) {
                    if (nowIndex == index) {
                        field[i][j].fillCell(RND);
                        break outerLoop;
                    }
                    nowIndex++;
                }
            }
        }
        filledCells++;
    }

    public boolean shift(Direction dir) {
        boolean madeMove = switch (dir) {
            case UP -> shiftUp(true);
            case DOWN -> shiftDown(true);
            case LEFT -> shiftLeft(true);
            case RIGHT -> shiftRight(true);
        };
        if (madeMove)
            createCell();
        if (gameState == GameState.PLAYING && !shiftLeft(false) && !shiftRight(false)
                && !shiftDown(false) && !shiftUp(false)) {
            gameState = GameState.LOSE;
        }
        return madeMove;
    }

    private boolean shiftLeft(boolean needChange) {
        boolean madeMove = false;
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                if (!field[i][j].isEmpty()) {
                    boolean swapped = false;
                    int swapWithIndex = -1;
                    for (int z = j - 1; z >= 0; z--) {
                        if (field[i][z].isEmpty()) {
                            swapWithIndex = z;
                        } else {
                            if (field[i][z].getValue() == field[i][j].getValue()) {
                                if (needChange) {
                                    field[i][z].increaseValue();
                                    if (field[i][z].getValue() == 2048) {
                                        gameState = GameState.WIN;
                                    }
                                    score += field[i][z].getValue();
                                    field[i][j].makeEmpty();
                                    filledCells--;
                                    swapped = true;
                                } else
                                    return true;
                            }
                            break;
                        }
                    }
                    if (!swapped && swapWithIndex != -1) {
                        if (needChange) {
                            field[i][j].swapWith(field[i][swapWithIndex]);
                            swapped = true;
                        } else
                            return true;
                    }

                    madeMove = madeMove || swapped;
                }
            }
        }
        return madeMove;
    }

    private boolean shiftRight(boolean needChange) {
        boolean madeMove = false;
        for (int i = 0; i < rowCount; i++) {
            for (int j = columnCount - 1; j >= 0; j--) {
                if (!field[i][j].isEmpty()) {
                    boolean swapped = false;
                    int swapWithIndex = -1;
                    for (int z = j + 1; z < columnCount; z++) {
                        if (field[i][z].isEmpty()) {
                            swapWithIndex = z;
                        } else {
                            if (field[i][z].getValue() == field[i][j].getValue()) {
                                if (needChange) {
                                    field[i][z].increaseValue();
                                    if (field[i][z].getValue() == 2048) {
                                        gameState = GameState.WIN;
                                    }
                                    score += field[i][z].getValue();
                                    field[i][j].makeEmpty();
                                    filledCells--;
                                    swapped = true;
                                } else
                                    return true;
                            }
                            break;
                        }
                    }
                    if (!swapped && swapWithIndex != -1) {
                        if (needChange) {
                            field[i][j].swapWith(field[i][swapWithIndex]);
                            swapped = true;
                        } else
                            return true;
                    }

                    madeMove = madeMove || swapped;
                }
            }
        }
        return madeMove;
    }

    private boolean shiftUp(boolean needChange) {
        boolean madeMove = false;
        for (int j = 0; j < columnCount; j++) {
            for (int i = 0; i < rowCount; i++) {
                if (!field[i][j].isEmpty()) {
                    boolean swapped = false;
                    int swapWithIndex = -1;
                    for (int z = i - 1; z >= 0; z--) {
                        if (field[z][j].isEmpty()) {
                            swapWithIndex = z;
                        } else {
                            if (field[z][j].getValue() == field[i][j].getValue()) {
                                if (needChange) {
                                    field[z][j].increaseValue();
                                    if (field[z][j].getValue() == 2048) {
                                        gameState = GameState.WIN;
                                    }
                                    score += field[z][j].getValue();
                                    field[i][j].makeEmpty();
                                    filledCells--;
                                    swapped = true;
                                } else
                                    return true;
                            }
                            break;
                        }
                    }
                    if (!swapped && swapWithIndex != -1) {
                        if (needChange) {
                            field[i][j].swapWith(field[swapWithIndex][j]);
                            swapped = true;
                        } else
                            return true;
                    }

                    madeMove = madeMove || swapped;
                }
            }
        }
        return madeMove;
    }

    private boolean shiftDown(boolean needChange) {
        boolean madeMove = false;
        for (int j = 0; j < columnCount; j++) {
            for (int i = rowCount - 1; i >= 0; i--) {
                if (!field[i][j].isEmpty()) {
                    boolean swapped = false;
                    int swapWithIndex = -1;
                    for (int z = i + 1; z < rowCount; z++) {
                        if (field[z][j].isEmpty()) {
                            swapWithIndex = z;
                        } else {
                            if (field[z][j].getValue() == field[i][j].getValue()) {
                                if (needChange) {
                                    field[z][j].increaseValue();
                                    if (field[z][j].getValue() == 2048) {
                                        gameState = GameState.WIN;
                                    }
                                    score += field[z][j].getValue();
                                    field[i][j].makeEmpty();
                                    filledCells--;
                                    swapped = true;
                                } else
                                    return true;
                            }
                            break;
                        }
                    }
                    if (!swapped && swapWithIndex != -1) {
                        if (needChange) {
                            field[i][j].swapWith(field[swapWithIndex][j]);
                            swapped = true;
                        } else
                            return true;
                    }

                    madeMove = madeMove || swapped;
                }
            }
        }
        return madeMove;
    }
}
