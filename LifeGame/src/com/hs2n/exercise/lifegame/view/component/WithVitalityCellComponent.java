package com.hs2n.exercise.lifegame.view.component;

import java.awt.Color;
import java.awt.Graphics;

import com.hs2n.exercise.lifegame.model.LifeGame;
import com.hs2n.exercise.lifegame.model.MonoLifeWithVitality;
import com.hs2n.exercise.lifegame.model.WithVitalityLifeGameField;
import com.hs2n.exercise.lifegame.model.core.ICell;
import com.hs2n.exercise.lifegame.model.core.Position;

public class WithVitalityCellComponent extends AbstractCellComponent<MonoLifeWithVitality, WithVitalityLifeGameField> {

    public WithVitalityCellComponent(LifeGame<MonoLifeWithVitality, WithVitalityLifeGameField> lifeGame,
        Position position) {
        super(lifeGame, position);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        var selfCell = lifeGame.getCurrentCellAt(position);
        if (!selfCell.hasLife()) {
            return;
        }

        g.setColor(Color.MAGENTA);
        int lifeWeakness = (MonoLifeWithVitality.VITALITY_MAX - selfCell.getLife().getVitality()) * 2;
        g.fillRect(lifeWeakness + 1, lifeWeakness + 1,
            getWidth() - (lifeWeakness * 2) - 1, getHeight() - (lifeWeakness * 2) - 1);
    }

    @Override
    protected String getLifeInformation(ICell<MonoLifeWithVitality> selfCell) {
        return String.format("体力：%2d", selfCell.getLife().getVitality());
    }
}
