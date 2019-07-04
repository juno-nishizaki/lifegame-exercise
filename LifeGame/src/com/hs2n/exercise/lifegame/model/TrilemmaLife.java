package com.hs2n.exercise.lifegame.model;

public enum TrilemmaLife {
    ROCK("R"), SCISSORS("S"), PAPER("P");

    private String toStringValue;

    private TrilemmaLife(String toStringValue) {
        this.toStringValue = toStringValue;
    }

    public TrilemmaLife getStrongOpponent() {
        switch (this) {
        case ROCK:
            return PAPER;
        case PAPER:
            return SCISSORS;
        case SCISSORS:
            return ROCK;
        default:
            throw new InternalError();
        }
    }

    public TrilemmaLife getWeakOpponent() {
        switch (this) {
        case ROCK:
            return SCISSORS;
        case SCISSORS:
            return PAPER;
        case PAPER:
            return ROCK;
        default:
            throw new InternalError();
        }
    }

    @Override
    public String toString() {
        return toStringValue;
    }
}
