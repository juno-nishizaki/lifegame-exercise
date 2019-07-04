package com.hs2n.exercise.lifegame.model.core;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

public final class Position implements Comparable<Position>, Serializable {

    private static final Comparator<Position> COMPARATOR = Comparator.comparing(Position::getRow)
        .thenComparing(Position::getColumn);

    private int row;
    private int column;

    public Position(int row, int colume) {
        this.row = row;
        this.column = colume;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        var other = getClass().cast(obj);
        if (row != other.row) {
            return false;
        }
        if (column != other.column) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Position other) {
        return COMPARATOR.compare(this, other);
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", row, column);
    }

}
