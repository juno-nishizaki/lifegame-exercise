package com.hs2n.exercise.lifegame.model;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import com.hs2n.exercise.lifegame.model.core.AbstractLifeGameField;
import com.hs2n.exercise.lifegame.model.core.ICell;
import com.hs2n.exercise.lifegame.model.core.ILifeGameField;
import com.hs2n.exercise.lifegame.model.core.Position;
import com.hs2n.exercise.lifegame.util.MapCounter;

public class TrilemmaLifeGameField extends AbstractLifeGameField<TrilemmaLife> {

    public TrilemmaLifeGameField(int rowSize, int columnSize) {
        super(rowSize, columnSize);
    }

    protected TrilemmaLifeGameField(int rowSize, int columnSize, Map<Position, ICell<TrilemmaLife>> cells) {
        super(rowSize, columnSize, cells);
    }

    @Override
    protected void updateCell(ICell<TrilemmaLife> selfCell, List<ICell<TrilemmaLife>> neiborCells) {
        var lifeCounts = new MapCounter<>(
            TrilemmaLife.ROCK,
            TrilemmaLife.PAPER,
            TrilemmaLife.SCISSORS);

        neiborCells.stream()
            .filter(neiborCell -> neiborCell.hasLife())
            .forEach(neiborCell -> {
                lifeCounts.increment(neiborCell.getLife());
            });

        if (!selfCell.hasLife()) {
            Stream.of(TrilemmaLife.values())
                .filter(life -> lifeCounts.getCount(life) == 3
                    && lifeCounts.getCount(life.getStrongOpponent()) != 3)
                .forEach(life -> selfCell.putLife(life));

        } else {

            var life = selfCell.getLife();
            int lifeCount = lifeCounts.getCount(life);
            int plusWeakOpponentCount = lifeCount + lifeCounts.getCount(life.getWeakOpponent());
            int plusStrongOpponentCount = lifeCount + lifeCounts.getCount(life.getStrongOpponent());
            if (plusWeakOpponentCount < 2 || plusStrongOpponentCount > 3) {
                selfCell.removeLife();
            }
        }
    }

    @Override
    protected ILifeGameField<TrilemmaLife> copyLifeGameField(int rowSize, int columnSize,
        Map<Position, ICell<TrilemmaLife>> sourceCells) {
        return new TrilemmaLifeGameField(rowSize, columnSize, sourceCells);
    }

    @Override
    protected void birth(ICell<TrilemmaLife> cell, Random random) {
        var trilemmaLifes = TrilemmaLife.values();
        cell.putLife(trilemmaLifes[random.nextInt(trilemmaLifes.length)]);
    }

    @Override
    public void rotateCellState(ICell<TrilemmaLife> cell) {
        if (!cell.hasLife()) {
            cell.putLife(TrilemmaLife.ROCK);
        } else {
            switch (cell.getLife()) {
            case ROCK:
                cell.putLife(TrilemmaLife.SCISSORS);
                break;
            case SCISSORS:
                cell.putLife(TrilemmaLife.PAPER);
                break;
            case PAPER:
                cell.removeLife();
                break;
            default:
                throw new InternalError();
            }
        }
    }
}
