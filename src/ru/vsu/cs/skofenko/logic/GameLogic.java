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
            case UP -> shiftVertically(true,true);
            case DOWN -> shiftVertically(true,false);
            case LEFT -> shiftHorizontally(true,true);
            case RIGHT -> shiftHorizontally(true,false);
        };
        if (madeMove)
            createCell();
        if (gameState == GameState.PLAYING && !shiftHorizontally(false,true) && !shiftHorizontally(false,false)
                && !shiftVertically(false,true) && !shiftVertically(false,true)) {
            gameState = GameState.LOSE;
        }
        return madeMove;
    }

    private boolean shiftHorizontally(boolean needChange, boolean toLeft) {
        boolean madeMove = false;
        for (int i = 0; i < rowCount; i++) {
            for (int j = (toLeft)?0:columnCount - 1;(toLeft)?j < columnCount:j >= 0;j+=boolToInt(toLeft)) {
                if (!field[i][j].isEmpty()) {
                    boolean swapped = false;
                    int swapWithIndex = -1;
                    for (int z = (toLeft)?j - 1:j + 1; (toLeft)?z >= 0:z < columnCount; z-=boolToInt(toLeft)) {
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

    private boolean shiftVertically(boolean needChange, boolean toTop) {
        boolean madeMove = false;
        for (int j = 0; j < columnCount; j++) {
            for (int i = (toTop)?0:rowCount - 1; (toTop)?i < rowCount:i >= 0; i+=boolToInt(toTop)) {
                if (!field[i][j].isEmpty()) {
                    boolean swapped = false;
                    int swapWithIndex = -1;
                    for (int z = (toTop)?i - 1:i + 1;(toTop)?z >= 0:z < rowCount; z-=boolToInt(toTop)) {
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

    public int boolToInt(boolean b) {
        return b ? 1 : -1;
    }
}
