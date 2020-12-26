package ru.vsu.cs.skofenko.logic;


public class FieldRotator {

    @FunctionalInterface
    private interface GetFunction {
        GameCell function(int row, int column);
    }

    private final GameCell[][] field;
    private final int rowCount;
    private final int columnCount;
    private final GetFunction getReference;

    public FieldRotator(GameCell[][] field,int rowCount, int columnCount, Direction dir){
        this.field=field;
        this.rowCount=rowCount;
        this.columnCount=columnCount;
        switch (dir){
            case UP -> getReference =this::getIfUp;
            case DOWN -> getReference =this::getIfDown;
            case RIGHT -> getReference =this::getIfRight;
            default -> getReference =this::getIfLeft;
        }
    }

    public GameCell getCell(int row, int column){
        return getReference.function(row, column);
    }

    private GameCell getIfLeft(int row, int column){
        return field[row][column];
    }

    private GameCell getIfRight(int row, int column){
        return field[rowCount-row-1][columnCount-column-1];
    }

    private GameCell getIfDown(int row, int column){
        return field[columnCount-column-1][row];
    }

    private GameCell getIfUp(int row, int column){
        return field[column][rowCount-row-1];
    }
}


