package com.hs2n.exercise.lifegame.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

import com.hs2n.exercise.lifegame.model.LifeGame;
import com.hs2n.exercise.lifegame.model.core.AbstractLifeGameField;
import com.hs2n.exercise.lifegame.model.core.Position;
import com.hs2n.exercise.lifegame.view.component.AbstractCellComponent;

/**
 * ライフゲームの画面を生成する共通の抽象クラスです。
 *
 * @author Juno NISHIZAKI
 *
 * @param <L> 生命体の型
 * @param <F> 二次元平面の型
 */
public abstract class AbstractLifeGameView<L, F extends AbstractLifeGameField<L>> {

    private static final int DEFAULT_CELL_SIZE = 12;
    private static final double DEFAULT_BIRTH_RATE = 0.3;

    protected LifeGame<L, F> lifeGame;

    private int cellSize = DEFAULT_CELL_SIZE;
    private double birthRate = DEFAULT_BIRTH_RATE;

    private boolean isLaunched = false;

    private JPanel fieldPanel;
    private Map<Position, AbstractCellComponent<L, F>> cellComponents;

    private JPanel controlPanel;
    private JButton resetButton;
    private JButton generateLifeButton;
    private JButton previousButton;
    private JButton nextButton;
    private JToggleButton autoNextButton;

    private JLabel currentGenerationLabel;
    private JLabel calculatedGenerationLabel;

    private void createAndShow() {
        // 二次元平面のパネルを生成する
        createFieldPanel();
        // コントロールパネルを生成する
        createControlPanel();

        // モデルから通知されるイベントを受け取るためイベントハンドラーを登録する
        lifeGame.addFieldChangedEventHandler(this::fieldChangedEventHandlerFunc);
        lifeGame.addCellChangedEventHandler(this::cellChangedEventHandlerFunc);

        // 外枠となるフレームを生成する
        final var frame = new JFrame("ライフゲーム：" + getName());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocation(20, 20);
        frame.setResizable(false);

        // フレームにパネルを追加する
        final var contentPane = frame.getContentPane();
        contentPane.setLayout(new FlowLayout());
        contentPane.add(fieldPanel);
        contentPane.add(controlPanel);

        // フレームのサイズを調整して表示する
        frame.pack();
        frame.setVisible(true);
        isLaunched = true;
    }

    private void createFieldPanel() {
        int rowSize = lifeGame.getRowSize();
        int columnSize = lifeGame.getColumnSize();

        fieldPanel = new JPanel();
        fieldPanel.setBorder(LineBorder.createBlackLineBorder());
        fieldPanel.setLayout(new GridLayout(rowSize, columnSize));

        cellComponents = new HashMap<>();
        IntStream.range(0, rowSize).boxed()
            .flatMap(rowIndex -> IntStream.range(0, columnSize).boxed()
                .map(columnIndex -> new Position(rowIndex, columnIndex)))
            .forEach(position -> {
                var cellComponent = createCellComponent(position);
                cellComponent.setPreferredSize(new Dimension(cellSize, cellSize));
                fieldPanel.add(cellComponent);
                cellComponents.put(position, cellComponent);
            });
    }

    protected abstract AbstractCellComponent<L, F> createCellComponent(Position position);

    private void createControlPanel() {
        resetButton = new JButton("リセット");
        resetButton.addActionListener(event -> lifeGame.reset());

        generateLifeButton = new JButton("ランダム生成");
        generateLifeButton.addActionListener(event -> lifeGame.generateLife(birthRate));

        previousButton = new JButton("前の世代");
        previousButton.addActionListener(event -> lifeGame.previous());
        previousButton.setEnabled(false);

        nextButton = new JButton("次の世代");
        nextButton.addActionListener(event -> lifeGame.next());

        final var nextTimer = new Timer(500, event -> lifeGame.next());
        autoNextButton = new JToggleButton("自動で次の世代に送る");
        autoNextButton.addActionListener(event -> {
            if (autoNextButton.isSelected()) {
                nextTimer.start();
                setButtonsEnabledForAutoNext(false);
            } else {
                nextTimer.stop();
                setButtonsEnabledForAutoNext(true);
            }
        });

        currentGenerationLabel = new JLabel(createCurrentGenerationText());
        calculatedGenerationLabel = new JLabel(createCalculatedGenerationText());

        controlPanel = new JPanel();
        controlPanel.setPreferredSize(new Dimension(200, 500));
        controlPanel.add(resetButton);
        controlPanel.add(generateLifeButton);
        controlPanel.add(previousButton);
        controlPanel.add(nextButton);
        controlPanel.add(autoNextButton);
        controlPanel.add(currentGenerationLabel);
        controlPanel.add(calculatedGenerationLabel);
    }

    private void setButtonsEnabledForAutoNext(boolean isEnabled) {
        SwingUtilities.invokeLater(() -> {
            resetButton.setEnabled(isEnabled);
            nextButton.setEnabled(isEnabled);

            boolean isGenerateLifeButtonEnabled = isEnabled;
            isGenerateLifeButtonEnabled &= lifeGame.isInitialState();
            generateLifeButton.setEnabled(isGenerateLifeButtonEnabled);

            boolean isPreviousButtonEnabled = isEnabled;
            isPreviousButtonEnabled &= !lifeGame.isFirstGeneration();
            previousButton.setEnabled(isPreviousButtonEnabled);
        });
    }

    private String createCurrentGenerationText() {
        return String.format("現在表示中の世代： %4d", lifeGame.getGenerationIndex() + 1);
    }

    private String createCalculatedGenerationText() {
        return String.format("計算済みの世代数： %4d", lifeGame.getCalculatedGenerationSize());
    }

    /**
     * 平面全体が変化したときのイベントハンドラー
     *
     * @param sender イベント通知元となるライフゲームのモデル
     * @param eventParams イベント通知時に渡されるパラメーター
     */
    private void fieldChangedEventHandlerFunc(LifeGame<L, F> sender, LifeGame.FieldChangeEventParams eventParams) {
        // ボタンの有効／無効を制御する
        generateLifeButton.setEnabled(sender.isInitialState());
        if (!autoNextButton.isSelected()) {
            previousButton.setEnabled(!sender.isFirstGeneration());
        }

        // ラベルを更新する
        currentGenerationLabel.setText(createCurrentGenerationText());
        calculatedGenerationLabel.setText(createCalculatedGenerationText());

        // 全セルのツールチップを更新する
        cellComponents.entrySet().stream()
            .forEach(entry -> entry.getValue().updateToolTipText());

        // 二次元平面のパネル全体を再描画する
        fieldPanel.repaint();
    }

    /**
     * セルが変化したときのイベントハンドラー
     *
     * @param sender イベント通知元となるライフゲームのモデル
     * @param eventParams イベント通知時に渡されるパラメーター
     */
    private void cellChangedEventHandlerFunc(LifeGame<L, F> sender, LifeGame.CellChangeEventParams eventParams) {
        var cellComponent = cellComponents.get(eventParams.getPosition());

        // 該当セルのツールチップを更新する
        cellComponent.updateToolTipText();

        // 該当セルを再描画する
        cellComponent.repaint();
    }

    protected abstract String getName();

    public final AbstractLifeGameView<L, F> newLifeGame(int rowSize, int columnSize) {
        validateReadyState();

        var lifeGameField = createLifeGameField(rowSize, columnSize);
        lifeGame = new LifeGame<>(lifeGameField);
        return this;
    }

    protected abstract F createLifeGameField(int rowSize, int columnSize);

    public final AbstractLifeGameView<L, F> cellSize(int cellSize) {
        validateReadyState();

        this.cellSize = cellSize;
        return this;
    }

    public final AbstractLifeGameView<L, F> birthRate(double birthRate) {
        validateReadyState();

        this.birthRate = birthRate;
        return this;
    }

    public final void launch() {
        validateReadyState();

        if (lifeGame == null) {
            throw new IllegalStateException();
        }
        SwingUtilities.invokeLater(() -> createAndShow());
    }

    private void validateReadyState() {
        if (isLaunched) {
            throw new IllegalStateException();
        }
    }
}
