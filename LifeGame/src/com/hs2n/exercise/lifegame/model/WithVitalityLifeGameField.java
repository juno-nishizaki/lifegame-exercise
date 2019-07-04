package com.hs2n.exercise.lifegame.model;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.hs2n.exercise.lifegame.model.core.AbstractLifeGameField;
import com.hs2n.exercise.lifegame.model.core.ICell;
import com.hs2n.exercise.lifegame.model.core.ILifeGameField;
import com.hs2n.exercise.lifegame.model.core.Position;

public class WithVitalityLifeGameField extends AbstractLifeGameField<MonoLifeWithVitality> {
    public WithVitalityLifeGameField(int rowSize, int columnSize) {
        super(rowSize, columnSize);
    }

    protected WithVitalityLifeGameField(int rowSize, int columnSize, Map<Position, ICell<MonoLifeWithVitality>> cells) {
        super(rowSize, columnSize, cells);
    }

    @Override
    protected void updateCell(ICell<MonoLifeWithVitality> selfCell, List<ICell<MonoLifeWithVitality>> neiborCells) {
        long lifeCount = neiborCells.stream()
            .filter(neiborCell -> neiborCell.hasLife())
            .count();

        if (!selfCell.hasLife()) {
            if (lifeCount == 3) {
                selfCell.putLife(new MonoLifeWithVitality());
            }
        } else {

            if (lifeCount < 2 || lifeCount > 3) {
                var life = selfCell.getLife();
                if (!life.weaken()) {
                    selfCell.removeLife();
                }
            }
        }
    }

    @Override
    protected ILifeGameField<MonoLifeWithVitality> copyLifeGameField(int rowSize, int columnSize,
        Map<Position, ICell<MonoLifeWithVitality>> sourceCells) {
        return new WithVitalityLifeGameField(rowSize, columnSize, sourceCells);
    }

    @Override
    protected void birth(ICell<MonoLifeWithVitality> cell, Random random) {
        int vitality = random.nextInt(MonoLifeWithVitality.VITALITY_MAX) + 1;
        cell.putLife(new MonoLifeWithVitality(vitality));
    }

    @Override
    public void rotateCellState(ICell<MonoLifeWithVitality> cell) {
        if (!cell.hasLife()) {
            cell.putLife(new MonoLifeWithVitality());
        } else {
            if (!cell.getLife().weaken()) {
                cell.removeLife();
            }
        }
    }
}
