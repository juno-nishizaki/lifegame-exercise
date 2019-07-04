package com.hs2n.exercise.lifegame.view;

import com.hs2n.exercise.lifegame.model.DefaultLifeGameField;
import com.hs2n.exercise.lifegame.model.MonoLife;
import com.hs2n.exercise.lifegame.model.core.Position;
import com.hs2n.exercise.lifegame.view.component.DefaultCellComponent;

public class DefaultLifeGameView extends AbstractLifeGameView<MonoLife, DefaultLifeGameField> {

    @Override
    protected DefaultLifeGameField createLifeGameField(int rowSize, int columnSize) {
        return new DefaultLifeGameField(rowSize, columnSize);
    }

    @Override
    protected DefaultCellComponent createCellComponent(Position position) {
        return new DefaultCellComponent(lifeGame, position);
    }

    @Override
    protected String getName() {
        return "標準";
    }
}
