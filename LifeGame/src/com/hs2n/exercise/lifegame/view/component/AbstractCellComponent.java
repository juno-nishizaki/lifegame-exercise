package com.hs2n.exercise.lifegame.view.component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import com.hs2n.exercise.lifegame.model.LifeGame;
import com.hs2n.exercise.lifegame.model.core.AbstractLifeGameField;
import com.hs2n.exercise.lifegame.model.core.ICell;
import com.hs2n.exercise.lifegame.model.core.Position;

public abstract class AbstractCellComponent<L, F extends AbstractLifeGameField<L>> extends JComponent {

    protected LifeGame<L, F> lifeGame;

    protected Position position;

    public AbstractCellComponent(LifeGame<L, F> lifeGame, Position position) {
        this.lifeGame = lifeGame;
        this.position = position;
        updateToolTipText();

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!lifeGame.isInitialState()) {
                    return;
                }
                switch (e.getButton()) {
                case MouseEvent.BUTTON1:
                    lifeGame.rotateCellStateAt(position);
                    break;
                case MouseEvent.BUTTON3:
                    lifeGame.toggleCellAt(position);
                    break;
                default:
                    break;
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (lifeGame.isEnabledCellAt(position)) {
            g.setColor(Color.WHITE);
        } else {
            g.setColor(Color.DARK_GRAY);
        }
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.DARK_GRAY);
        g.drawLine(0, 0, getWidth(), 0);
        g.drawLine(0, 0, 0, getHeight());
    }

    public void updateToolTipText() {
        String text;
        if (lifeGame.isEnabledCellAt(position)) {
            var selfCell = lifeGame.getCurrentCellAt(position);
            if (!selfCell.hasLife()) {
                text = "生命なし";
            } else {
                text = getLifeInformation(selfCell);
            }
        } else {
            text = "無効セル";
        }

        setToolTipText(text);
    }

    protected abstract String getLifeInformation(ICell<L> selfCell);
}
