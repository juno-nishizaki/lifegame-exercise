package com.hs2n.exercise.lifegame.view;

import com.hs2n.exercise.lifegame.model.TrilemmaLife;
import com.hs2n.exercise.lifegame.model.TrilemmaLifeGameField;
import com.hs2n.exercise.lifegame.model.core.Position;
import com.hs2n.exercise.lifegame.view.component.TrilemmaCellComponent;

public class TrilemmaLifeGameView extends AbstractLifeGameView<TrilemmaLife, TrilemmaLifeGameField> {

    @Override
    protected TrilemmaLifeGameField createLifeGameField(int rowSize, int columnSize) {
        return new TrilemmaLifeGameField(rowSize, columnSize);
    }

    @Override
    protected TrilemmaCellComponent createCellComponent(Position position) {
        return new TrilemmaCellComponent(lifeGame, position);
    }

    @Override
    protected String getName() {
        return "三すくみ";
    }

}
