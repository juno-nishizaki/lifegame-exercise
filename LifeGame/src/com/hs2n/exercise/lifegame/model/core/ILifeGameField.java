package com.hs2n.exercise.lifegame.model.core;

public interface ILifeGameField<L> {

    ICell<L> getCellAt(Position position);

    ILifeGameField<L> update();
}
