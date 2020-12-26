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
        boolean madeMove = shift(dir,true);
        if (madeMove)
            createCell();
        if (gameState == GameState.PLAYING && !shift(Direction.DOWN,false) && !shift(Direction.UP,false)
                && !shift(Direction.RIGHT,false) && !shift(Direction.LEFT,false)) {
            gameState = GameState.LOSE;
        }
        return madeMove;
    }

    private boolean shift(Direction dir, boolean needChange){
        boolean madeMove = false;
        FieldRotator rotator = new FieldRotator(field,rowCount,columnCount,dir);
        for (int i = 0; i < rowCount; i++) {
            boolean previousNotIncreased=true;
            for (int j = 0; j < columnCount; j++) {
                if (!rotator.getCell(i,j).isEmpty()) {
                    boolean swapped = false;
                    int swapWithIndex = -1;
                    for (int z = j - 1; z >= 0; z--) {
                        if (rotator.getCell(i,z).isEmpty()) {
                            swapWithIndex = z;
                        } else {
                            if (previousNotIncreased && rotator.getCell(i,z).getValue() == rotator.getCell(i,j).getValue()) {
                                if (needChange) {
                                    rotator.getCell(i,z).increaseValue();
                                    if (rotator.getCell(i,z).getValue() == 2048) {
                                        gameState = GameState.WIN;
                                    }
                                    score += rotator.getCell(i,z).getValue();
                                    rotator.getCell(i,j).makeEmpty();
                                    filledCells--;
                                    swapped = true;
                                } else
                                    return true;
                            }
                            break;
                        }
                    }
                    previousNotIncreased= !swapped;
                    if (!swapped && swapWithIndex != -1) {
                        if (needChange) {
                            rotator.getCell(i,j).swapWith(rotator.getCell(i,swapWithIndex));
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
