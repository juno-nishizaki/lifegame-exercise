package com.hs2n.exercise.lifegame.view;

import com.hs2n.exercise.lifegame.model.MonoLifeWithVitality;
import com.hs2n.exercise.lifegame.model.WithVitalityLifeGameField;
import com.hs2n.exercise.lifegame.model.core.Position;
import com.hs2n.exercise.lifegame.view.component.WithVitalityCellComponent;

public class WithVitalityLifeGameView extends AbstractLifeGameView<MonoLifeWithVitality, WithVitalityLifeGameField> {

    @Override
    protected WithVitalityLifeGameField createLifeGameField(int rowSize, int columnSize) {
        return new WithVitalityLifeGameField(rowSize, columnSize);
    }

    @Override
    protected WithVitalityCellComponent createCellComponent(Position position) {
        return new WithVitalityCellComponent(lifeGame, position);
    }

    @Override
    protected String getName() {
        return "体力あり";
    }
}
