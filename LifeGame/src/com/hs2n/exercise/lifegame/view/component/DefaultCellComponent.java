package com.hs2n.exercise.lifegame.view.component;

import java.awt.Color;
import java.awt.Graphics;

import com.hs2n.exercise.lifegame.model.DefaultLifeGameField;
import com.hs2n.exercise.lifegame.model.LifeGame;
import com.hs2n.exercise.lifegame.model.MonoLife;
import com.hs2n.exercise.lifegame.model.core.ICell;
import com.hs2n.exercise.lifegame.model.core.Position;

public class DefaultCellComponent extends AbstractCellComponent<MonoLife, DefaultLifeGameField> {

    public DefaultCellComponent(LifeGame<MonoLife, DefaultLifeGameField> lifeGame, Position position) {
        super(lifeGame, position);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        var selfCell = lifeGame.getCurrentCellAt(position);
        if (!selfCell.hasLife()) {
            return;
        }

        g.setColor(Color.CYAN);
        g.fillRect(1, 1, getWidth() - 1, getHeight() - 1);
    }

    @Override
    protected String getLifeInformation(ICell<MonoLife> selfCell) {
        return "生命あり";
    }
}
