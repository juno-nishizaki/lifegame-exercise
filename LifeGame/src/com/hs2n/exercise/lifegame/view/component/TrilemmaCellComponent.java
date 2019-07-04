package com.hs2n.exercise.lifegame.view.component;

import java.awt.Color;
import java.awt.Graphics;

import com.hs2n.exercise.lifegame.model.LifeGame;
import com.hs2n.exercise.lifegame.model.TrilemmaLife;
import com.hs2n.exercise.lifegame.model.TrilemmaLifeGameField;
import com.hs2n.exercise.lifegame.model.core.ICell;
import com.hs2n.exercise.lifegame.model.core.Position;

public class TrilemmaCellComponent extends AbstractCellComponent<TrilemmaLife, TrilemmaLifeGameField> {

    public TrilemmaCellComponent(LifeGame<TrilemmaLife, TrilemmaLifeGameField> lifeGame, Position position) {
        super(lifeGame, position);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        var selfCell = lifeGame.getCurrentCellAt(position);
        if (!selfCell.hasLife()) {
            return;
        }

        switch (selfCell.getLife()) {
        case ROCK:
            g.setColor(Color.RED);
            break;
        case SCISSORS:
            g.setColor(Color.GREEN);
            break;
        case PAPER:
            g.setColor(Color.BLUE);
            break;
        default:
            throw new InternalError();
        }
        g.fillRect(1, 1, getWidth() - 1, getHeight() - 1);
    }

    @Override
    protected String getLifeInformation(ICell<TrilemmaLife> selfCell) {
        switch (selfCell.getLife()) {
        case ROCK:
            return "グー";
        case SCISSORS:
            return "チョキ";
        case PAPER:
            return "パー";
        default:
            throw new InternalError();
        }
    }

}
