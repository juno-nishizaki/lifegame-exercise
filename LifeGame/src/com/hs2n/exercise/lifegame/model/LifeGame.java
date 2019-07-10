package com.hs2n.exercise.lifegame.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import com.hs2n.exercise.lifegame.model.core.AbstractLifeGameField;
import com.hs2n.exercise.lifegame.model.core.ICell;
import com.hs2n.exercise.lifegame.model.core.ILifeGameField;
import com.hs2n.exercise.lifegame.model.core.Position;
import com.hs2n.exercise.lifegame.util.EventNotifier;

/**
 * ライフゲームのモデルとなるクラスです。
 * コンストラクタで設定した二次元平面を最新の状態として、世代を進めたり戻したりすることができます。
 * 過去の世代は履歴として管理します。
 *
 * <p>
 * 初期状態（計算済みの世代が 1 世代のみ）の場合に限り、
 * 二次元平面のセルや生命体の配置を編集することができます。
 * </p>
 *
 * <p>
 * 二次元平面全体やセルの状態が変化したときにモデル内でイベントが発生します。
 * イベント発生の通知を受けるためのイベントハンドラーをモデルに登録することができます。
 * イベント通知を契機にビューの再描画処理を呼び出すなどして、モデルとの同期をとることができます。
 * </p>
 *
 * @author Juno NISHIZAKI
 *
 * @param <L> 生命体の型
 * @param <F> 二次元平面の型
 */
public class LifeGame<L, F extends AbstractLifeGameField<L>> {

    /**
     * モデルが管理する二次元平面の最新の状態です。
     */
    private F latestLifeGameField;

    /**
     * モデルが管理する二次元平面の履歴です。
     * 最新の状態の 1 世代前からの情報をすべて保持します。
     * 初期状態は空です。
     */
    private List<ILifeGameField<L>> history;

    /**
     * モデル内で現在選択されている世代の番号です。
     */
    private int generationIndex;

    /**
     * 世代の番号の範囲を示す条件式（述語）です。
     */
    private Predicate<Integer> generationRange = (v -> v >= 0 && v < getCalculatedGenerationSize());

    /**
     * 平面全体の状態が変化したときに使用するイベント通知オブジェクトです。
     */
    private EventNotifier<LifeGame<L, F>, FieldChangeEventParams> fieldChangeEventNotifier = new EventNotifier<>(this,
        true);

    /**
     * 平面全体の状態が変化したときにイベント通知先に渡すパラメーターのクラスです。
     * 現状では渡すパラメーターがないですが、セルの状態変化と形を合わせるために用意しています。
     *
     * @author Juno NISHIZAKI
     *
     */
    public static class FieldChangeEventParams {
        private FieldChangeEventParams() {}
    }

    /**
     * セルの状態が変化したときに使用するイベント通知オブジェクトです。
     */
    private EventNotifier<LifeGame<L, F>, CellChangeEventParams> cellChangeEventNotifier = new EventNotifier<>(this,
        true);

    /**
     * セルの状態が変化したときにイベント通知先に渡すパラメーターのクラスです。
     * どの位置のセルが変化したかを表すために使用します。
     *
     * @author Juno NISHIZAKI
     *
     */
    public static class CellChangeEventParams {
        private Position position;

        private CellChangeEventParams(Position position) {
            this.position = position;
        }

        public Position getPosition() {
            return position;
        }
    }

    /**
     * 指定された二次元平面を扱うライフゲームのモデルを構築します。
     *
     * @param lifeGameField 二次元平面
     */
    public LifeGame(F lifeGameField) {
        latestLifeGameField = lifeGameField;
        initializeHistory();
    }

    /**
     * ライフゲームを初期状態にリセットします。
     */
    public void reset() {
        latestLifeGameField.initializeCells();
        initializeHistory();

        // リセットにより全体の状態が変化するため、平面変化のイベントを発生させる
        fieldChangeEventNotifier.fire(new FieldChangeEventParams());
    }

    /**
     * 履歴を空にして、世代番号を初期状態にします。
     */
    private void initializeHistory() {
        history = new ArrayList<>();
        generationIndex = 0;
    }

    /**
     * 初期状態の二次元平面の各セルに対してランダムで生命体を生成します。
     *
     * @param birthRate 生命体の発生率
     */
    public void generateLife(double birthRate) {
        generateLife(birthRate, new Random());
    }

    /**
     * 初期状態の二次元平面の各セルに対してランダムで生命体を生成します。
     *
     * @param birthRate 生命体の発生率
     * @param random 乱数オブジェクト
     */
    public void generateLife(double birthRate, Random random) {
        // 初期状態かチェックする
        validateInitialState();

        // 二次元平面のインスタンスに処理を委譲する
        latestLifeGameField.generateLife(birthRate, random);

        // 全体の状態が変化するため、平面変化のイベントを発生させる
        fieldChangeEventNotifier.fire(new FieldChangeEventParams());
    }

    /**
     * 初期状態の二次元平面の指定したセルに対して、有効／無効を入れ替えます。
     *
     * @param position セルの位置
     */
    public void toggleCellAt(Position position) {
        // 初期状態かチェックする
        validateInitialState();

        // 指定したセルが有効かどうかをチェックする
        if (latestLifeGameField.isEnableCellAt(position)) {
            // 有効の場合、無効にする
            latestLifeGameField.disableCellAt(position);
        } else {
            // 無効の場合、有効にする
            latestLifeGameField.enableCellAt(position);
        }

        // セル変化のイベントを発生させる
        cellChangeEventNotifier.fire(new CellChangeEventParams(position));
    }

    public void rotateCellStateAt(Position position) {
        // 初期状態かチェックする
        validateInitialState();

        latestLifeGameField.rotateCellState(latestLifeGameField.getCellAt(position));

        // セル変化のイベントを発生させる
        cellChangeEventNotifier.fire(new CellChangeEventParams(position));
    }

    public void next() {
        // 最新世代の場合、最新の平面を更新して、更新直前の世代を履歴に追加する
        if (generationIndex == history.size()) {
            history.add(latestLifeGameField.update());
        }
        // 世代の番号をインクリメントする
        generationIndex++;

        // 全体の状態が変化するため、平面変化のイベントを発生させる
        fieldChangeEventNotifier.fire(new FieldChangeEventParams());
    }

    public void previous() {
        if (isFirstGeneration()) {
            throw new IllegalStateException();
        }
        // 世代の番号をデクリメントする
        generationIndex--;

        // 全体の状態が変化するため、平面変化のイベントを発生させる
        fieldChangeEventNotifier.fire(new FieldChangeEventParams());
    }

    public void setGenerationIndex(int generationIndex) {
        if (!generationRange.test(generationIndex)) {
            throw new IndexOutOfBoundsException();
        }
        this.generationIndex = generationIndex;
        fieldChangeEventNotifier.fire(new FieldChangeEventParams());
    }

    public int getGenerationIndex() {
        return generationIndex;
    }

    public int getRowSize() {
        return latestLifeGameField.getRowSize();
    }

    public int getColumnSize() {
        return latestLifeGameField.getColumnSize();
    }

    public int getCalculatedGenerationSize() {
        return history.size() + 1;
    }

    public boolean isInitialState() {
        return history.size() == 0;
    }

    public boolean isFirstGeneration() {
        return generationIndex == 0;
    }

    public boolean isEnabledCellAt(Position position) {
        return latestLifeGameField.isEnableCellAt(position);
    }

    public ICell<L> getCurrentCellAt(Position position) {
        return getCurrentLifeGameField().getCellAt(position);
    }

    private ILifeGameField<L> getCurrentLifeGameField() {
        if (generationIndex == history.size()) {
            return latestLifeGameField;
        }
        return history.get(generationIndex);
    }

    private void validateInitialState() {
        if (!isInitialState()) {
            throw new IllegalStateException();
        }
    }

    public void addFieldChangedEventHandler(BiConsumer<LifeGame<L, F>, FieldChangeEventParams> eventHandler) {
        fieldChangeEventNotifier.addEventHandler(eventHandler);
    }

    public void addCellChangedEventHandler(BiConsumer<LifeGame<L, F>, CellChangeEventParams> eventHandler) {
        cellChangeEventNotifier.addEventHandler(eventHandler);
    }

}
