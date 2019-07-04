package com.hs2n.exercise.lifegame.model.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * ライフゲームの二次元平面を表す共通の抽象クラスです。
 *
 * <p>
 * セルの集合を表す内部表現や、世代更新のテンプレート処理は本クラスに持たせて、
 * サブクラス側でルールを記述するだけで任意のライフゲームが作成できることを目指します。
 * </p>
 *
 * @author Juno NISHIZAKI
 *
 * @param <L> 生命体の型
 */
public abstract class AbstractLifeGameField<L> implements ILifeGameField<L> {
    /**
     * 初期化時の行列サイズの範囲を示す条件式（述語）です。
     */
    private static final Predicate<Integer> SIZE_RANGE = (v -> v >= 3 && v <= 100);

    /**
     * 無効セルを表す唯一のインスタンスです。
     */
    private final ICell<L> nullCell = new NullCell<>();

    /**
     * 行サイズです。
     */
    private int rowSize;

    /**
     * 列サイズです。
     */
    private int columnSize;

    /**
     * 行インデックスの範囲を示す条件式（述語）です。
     */
    private Predicate<Integer> rowRange = (v -> v >= 0 && v < rowSize);

    /**
     * 列インデックスの範囲を示す条件式（述語）です。
     */
    private Predicate<Integer> columnRange = (v -> v >= 0 && v < columnSize);

    /**
     * セルの集合を表す内部表現です。
     * 二次元平面の位置をキーにしたマップで扱います。
     * 近傍を探索する際の境界判定を簡素化するために、有効セルの周囲を無効セルで敷き詰めています。
     * （番兵の役割）
     */
    private Map<Position, ICell<L>> cells;

    /**
     * 指定されたパラメータでライフゲーム二次元平面を構築します。
     *
     * <p>
     * このコンストラクタはインスタンスの複製用で、本クラスまたはサブクラスでのみ使用します。
     * </p>
     *
     * @param rowSize 行サイズ
     * @param columnSize 列サイズ
     * @param cells セルの集合を表す内部表現
     */
    protected AbstractLifeGameField(int rowSize, int columnSize, Map<Position, ICell<L>> cells) {
        // 行サイズを範囲チェックする
        if (!SIZE_RANGE.test(rowSize)) {
            throw new IllegalArgumentException();
        }
        // 列サイズを範囲チェックする
        if (!SIZE_RANGE.test(columnSize)) {
            throw new IllegalArgumentException();
        }
        this.rowSize = rowSize;
        this.columnSize = columnSize;
        this.cells = cells;
    }

    /**
     * 指定されたパラメータでライフゲーム二次元平面を構築します。
     *
     * @param rowSize 行サイズ
     * @param columnSize 列サイズ
     */
    public AbstractLifeGameField(int rowSize, int columnSize) {
        // セルの集合はいったん空のままで構築する
        this(rowSize, columnSize, new HashMap<>());

        // セルの集合を初期化する
        initializeCells();
    }

    /**
     * セルの集合を初期化します。
     */
    public void initializeCells() {
        // 行列サイズの範囲内を新規セルのインスタンスで敷き詰める
        IntStream.range(0, rowSize).boxed()
            .flatMap(rowIndex -> IntStream.range(0, columnSize).boxed()
                .map(columnIndex -> new Position(rowIndex, columnIndex)))
            .forEach(position -> cells.put(position, new Cell<>()));

        // 周囲を無効セルで敷き詰める
        IntStream.rangeClosed(-1, columnSize).boxed().forEach(columnIndex -> {
            cells.put(new Position(-1, columnIndex), nullCell);
            cells.put(new Position(rowSize, columnIndex), nullCell);
        });

        // 下記の範囲指定では四隅の処理が無駄になるが、可読性を優先した
        IntStream.rangeClosed(-1, rowSize).boxed().forEach(rowIndex -> {
            cells.put(new Position(rowIndex, -1), nullCell);
            cells.put(new Position(rowIndex, columnSize), nullCell);
        });

        // 上記の処理を伝統的な for 文で記述するとしたら、以下のようになる
        //
        //        for (int rowIndex = 0; rowIndex < rowSize; rowIndex++) {
        //            for (int columnIndex = 0; columnIndex < columnSize; columnIndex++) {
        //                var position = new Position(rowIndex, columnIndex);
        //                cells.put(position, new Cell<>());
        //            }
        //        }
        //
        //        for (int columnIndex = -1; columnIndex <= columnSize; columnIndex++) {
        //            cells.put(new Position(-1, columnIndex), nullCell);
        //            cells.put(new Position(rowSize, columnIndex), nullCell);
        //        }
        //
        //        for (int rowIndex = -1; rowIndex <= rowSize; rowIndex++) {
        //            cells.put(new Position(rowIndex, -1), nullCell);
        //            cells.put(new Position(rowIndex, columnSize), nullCell);
        //        }
        //
        // 今回は Stream API を学習するために敢えてがんばって記述してみたが、
        // この程度の内容であれば伝統的な for 文の方が可読性が高いのかもしれない
        // （そもそも Stream API の使い方として正しいのか若干自信がない…）
    }

    /**
     * 二次元平面の各セルに対してランダムで生命体を生成します。
     *
     * @param birthRate 生命体の発生率
     * @param random 乱数オブジェクト
     */
    public void generateLife(double birthRate, Random random) {
        // セルの集合をいったん初期化する
        initializeCells();

        // 有効セルのそれぞれに対して、0 から 1 までの乱数の値が発生率を下回った場合、
        // サブクラス側のルールに従って生命体を誕生させる
        cells.entrySet().stream()
            .filter(entry -> isEnableCell(entry.getValue()))
            .forEach(entry -> {
                if (random.nextDouble() < birthRate) {
                    birth(entry.getValue(), random);
                }
            });
    }

    /**
     * 指定されたセルに対してランダムで生命体を誕生させます。
     *
     * @param cell 対象のセル
     * @param random 乱数オブジェクト
     */
    protected abstract void birth(ICell<L> cell, Random random);

    /**
     * 行サイズを取得します。
     *
     * @return 行サイズ
     */
    public int getRowSize() {
        return rowSize;
    }

    /**
     * 列サイズを取得します。
     *
     * @return 列サイズ
     */
    public int getColumnSize() {
        return columnSize;
    }

    /**
     * 指定された位置に対応するセルを取得します。
     */
    @Override
    public ICell<L> getCellAt(Position position) {
        validatePosition(position);
        return cells.get(position);
    }

    /**
     * 二次元平面の世代を更新します。
     *
     * <p>
     * Template Method パターンを適用しております。
     * 処理の流れは以下のとおりです。
     * </p>
     *
     * <ol>
     *   <li>事前にセルの集合を表す内部表現のみコピーして、更新直前の世代として扱う</li>
     *   <li>更新対象の各セルを走査して、更新直前の世代から近傍を取得する</li>
     *   <li>更新対象のセルとその近傍の状態を元に、サブクラス側で定めたルールに従ってセルを次状態に更新する</li>
     *   <li>すべてのセルの更新が終われば、更新直前の世代を元に新しい二次元平面インスタンスを構築して返す</li>
     * </ol>
     *
     */
    @Override
    public ILifeGameField<L> update() {
        // 事前にセルの集合を表す内部表現のみコピーして、更新直前の世代として扱う
        var previousCells = copyCells(cells);

        cells.entrySet().stream()
            .filter(entry -> isEnableCell(entry.getValue()))
            .forEach(entry -> {
                // 更新対象の各セルを走査して、更新直前の世代から近傍を取得する
                var neiborCells = getNeiborCells(entry.getKey(), previousCells);

                // 更新対象のセルとその近傍の状態を元に、
                // サブクラス側で定めたルールに従ってセルを次状態に更新する
                updateCell(entry.getValue(), neiborCells);
            });

        // 更新直前の世代を元に新しい二次元平面インスタンスを構築して返す
        return copyLifeGameField(rowSize, columnSize, previousCells);
    }

    /**
     * セルの集合を表す内部表現をコピーします。
     *
     * @param <L> 生命体の型
     * @param sourceCells コピー元
     * @return コピーした結果
     */
    private static <L> Map<Position, ICell<L>> copyCells(Map<Position, ICell<L>> sourceCells) {
        // 似た名前の変数が多いので、誤ってフィールドを参照してしまわないように
        // 自衛の意味で static メソッドにしている（他の static メソッドも同じ理由）

        // キーはイミュータブルのため浅いコピー、値は深いコピーにする
        return sourceCells.entrySet().stream()
            .collect(Collectors.toMap(
                entry -> entry.getKey(),
                entry -> entry.getValue().copyCell(),
                (oldValue, newValue) -> newValue,
                HashMap::new));
    }

    /**
     * 指定された位置の近傍を取得します。
     *
     * @param <L> 生命体の型
     * @param position 位置
     * @param sourceCells セルの集合を表す内部表現（更新直前の世代）
     * @return 近傍を表すセルのリスト
     */
    private static <L> List<ICell<L>> getNeiborCells(Position position, Map<Position, ICell<L>> sourceCells) {
        // 指定された位置からプラスマイナス 1 の範囲（指定位置自体は除く）を走査する
        return IntStream.rangeClosed(-1, 1).boxed()
            .flatMap(rowDelta -> IntStream.rangeClosed(-1, 1).boxed()
                .filter(columnDelta -> !(rowDelta == 0 && columnDelta == 0))
                .map(columnDelta -> {
                    // 近傍の位置を算出する
                    int rowIndex = position.getRow() + rowDelta;
                    int columnIndex = position.getColumn() + columnDelta;
                    // 近傍セルを取得する（終端操作でリストに集約する）
                    return sourceCells.get(new Position(rowIndex, columnIndex));
                }))
            .collect(Collectors.toList());
    }

    /**
     * 指定されたセルと近傍の状態を元に、定めたルールに従ってセルを次状態に更新します。
     * ルールはサブクラス側で自由に定めてよいです。
     *
     * @param selfCell 更新対象のセル
     * @param neiborCells 更新対象のセルの近傍
     */
    protected abstract void updateCell(ICell<L> selfCell, List<ICell<L>> neiborCells);

    /**
     * 指定されたパラメータで新しい二次元平面インスタンスを構築して返します。
     *
     * <p>
     * 構築対象のインスタンスの型がサブクラス側でしか決定できないため、抽象メソッドにしています。
     * 必要な処理はサブクラス側で複製用のコンストラクタを呼び出すだけです。
     * （柔軟性を持たせようとしすぎて、やや失敗している感も否めないです）
     * </p>
     *
     * @param rowSize 行サイズ
     * @param columnSize 列サイズ
     * @param sourceCells セルの集合を表す内部表現（更新直前の世代）
     * @return 新しい二次元平面のインスタンス
     */
    protected abstract ILifeGameField<L> copyLifeGameField(int rowSize, int columnSize,
        Map<Position, ICell<L>> sourceCells);

    /**
     * 指定された位置を無効セルにします。
     *
     * @param position 位置
     */
    public void disableCellAt(Position position) {
        validatePosition(position);
        cells.put(position, nullCell);
    }

    /**
     * 指定された位置を有効セルにします。
     *
     * @param position 位置
     */
    public void enableCellAt(Position position) {
        validatePosition(position);
        cells.put(position, new Cell<>());
    }

    /**
     * 指定された位置に対応するセルが有効かどうかを判定します。
     *
     * @param position 位置
     * @return 有効セルの場合 true、無効セルの場合 false
     */
    public boolean isEnableCellAt(Position position) {
        return isEnableCell(getCellAt(position));
    }

    /**
     * 指定されたセルが有効かどうかを判定します。
     *
     * @param cell 対象のセル
     * @return 有効セルの場合 true、無効セルの場合 false
     */
    private boolean isEnableCell(ICell<L> cell) {
        return cell != nullCell;
    }

    /**
     * 指定されたセルの状態をローテーションで変更します。
     *
     * <p>
     * 本メソッドは、二次元平面の状態を編集する際の処理を簡素化するために用意しています。
     * </p>
     *
     * @param cell 対象のセル
     */
    public abstract void rotateCellState(ICell<L> cell);

    /**
     * 指定された位置が範囲内かどうかチェックして、範囲外の場合には例外をスローします。
     * （範囲内の場合は何もしません）
     *
     * @param position 位置
     * @throws IndexOutOfBoundsException 指定された位置が範囲外の場合
     */
    private void validatePosition(Position position) throws IndexOutOfBoundsException {
        if (!rowRange.test(position.getRow())) {
            throw new IndexOutOfBoundsException("Row index out of range: " + position.getRow());
        }
        if (!columnRange.test(position.getColumn())) {
            throw new IndexOutOfBoundsException("Column index out of range: " + position.getColumn());
        }
    }

    /**
     * 二次元平面の状態を文字列表現に変換します。
     * この文字列表現は、主にデバッグの目的で使用します。
     */
    @Override
    public String toString() {
        var sb = new StringBuilder();
        IntStream.range(0, rowSize).boxed()
            .flatMap(rowIndex -> IntStream.range(0, columnSize).boxed()
                .map(columnIndex -> new Position(rowIndex, columnIndex)))
            .forEach(position -> {
                sb.append(getCellAt(position));
                // Stream API で単純な二重ループを記述して
                // 内側と外側のループの間に改行を差し込むために
                // 苦し紛れでこのような形にしている
                // このコメントを書いているうちに、よくよく考えたら
                // map の段階で文字列の断片を作って終端操作で集約すれば
                // うまくいくような気もしてきたが、
                // 検証する時間がないのでいったんこのままにしておく
                if (position.getColumn() == columnSize - 1) {
                    sb.append(String.format("%n"));
                }
            });
        return sb.toString();
    }

}
