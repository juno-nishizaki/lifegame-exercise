package com.hs2n.exercise.lifegame.model;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.hs2n.exercise.lifegame.model.core.AbstractLifeGameField;
import com.hs2n.exercise.lifegame.model.core.ICell;
import com.hs2n.exercise.lifegame.model.core.ILifeGameField;
import com.hs2n.exercise.lifegame.model.core.Position;

/**
 * 標準のライフゲームを扱う二次元平面クラスです。
 *
 * @author Juno NISHIZAKI
 *
 */
public class DefaultLifeGameField extends AbstractLifeGameField<MonoLife> {

    /**
     * 指定されたパラメータで標準のライフゲーム二次元平面を構築します。
     *
     * <p>
     * このコンストラクタはインスタンスの複製用で、copyLifeGameField の実装のために使用します。
     * </p>
     *
     * @param rowSize 行サイズ
     * @param columnSize 列サイズ
     * @param cells セルの集合を表す内部表現
     */
    protected DefaultLifeGameField(int rowSize, int columnSize, Map<Position, ICell<MonoLife>> cells) {
        super(rowSize, columnSize, cells);
    }

    /**
     * 指定された行列サイズで標準のライフゲーム二次元平面を構築します。
     *
     * @param rowSize 行サイズ
     * @param columnSize 列サイズ
     */
    public DefaultLifeGameField(int rowSize, int columnSize) {
        super(rowSize, columnSize);
    }

    /**
     * 標準のライフゲームのルールに従って、自セルと近傍の現状態から自セルを次の状態に更新します。
     *
     * <p>
     * 標準のライフゲームのルールは以下のとおりです。
     * </p>
     *
     * <ul>
     *   <li>自セルに生命体が存在しない場合
     *     <ul>
     *       <li>近傍に存在する生命体の数が 3 と等しい場合、自セルに新しい生命体を配置する</li>
     *       <li>近傍に存在する生命体の数が 3 と等しくない場合、自セルの状態は変化しない</li>
     *     </ul>
     *   </li>
     *   <li>自セルに生命体が存在する場合
     *     <ul>
     *       <li>近傍に存在する生命体の数が 2 か 3 のいずれでもない場合、自セルから生命体を取り除く</li>
     *       <li>近傍に存在する生命体の数が 2 か 3 のいずれかの場合、自セルの状態は変化しない</li>
     *     </ul>
     *   </li>
     * </ul>
     */
    @Override
    protected void updateCell(ICell<MonoLife> selfCell, List<ICell<MonoLife>> neiborCells) {
        // 近傍から生命体が存在するセルの数を取得する
        long lifeCount = neiborCells.stream()
            .filter(neiborCell -> neiborCell.hasLife())
            .count();

        // 自セルに生命体が存在するかどうかで次状態の判定を分岐する
        if (!selfCell.hasLife()) {
            // 自セルに生命体が存在しない場合

            // 近傍に存在する生命体の数が 3 と等しい場合、自セルに新しい生命体を配置する
            if (lifeCount == 3) {
                selfCell.putLife(MonoLife.ONE);
            }
        } else {
            // 自セルに生命体が存在する場合

            // 近傍に存在する生命体の数が 2 か 3 のいずれでもない場合、自セルから生命体を取り除く
            if (lifeCount < 2 || lifeCount > 3) {
                selfCell.removeLife();
            }
        }
    }

    @Override
    protected ILifeGameField<MonoLife> copyLifeGameField(int rowSize, int columnSize,
        Map<Position, ICell<MonoLife>> sourceCells) {
        return new DefaultLifeGameField(rowSize, columnSize, sourceCells);
    }

    @Override
    protected void birth(ICell<MonoLife> cell, Random random) {
        cell.putLife(MonoLife.ONE);
    }

    @Override
    public void rotateCellState(ICell<MonoLife> cell) {
        if (!cell.hasLife()) {
            // セルに生命体が存在しない場合、セルに新しい生命体を配置する
            cell.putLife(MonoLife.ONE);
        } else {
            // セルに生命体が存在する場合、セルから生命体を取り除く
            cell.removeLife();
        }
    }
}
