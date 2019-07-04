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

        createFieldPanel();

        createControlPanel();

        lifeGame.addFieldChangedEventHandler(this::fieldChangedEventHandlerFunc);
        lifeGame.addCellChangedEventHandler(this::cellChangedEventHandlerFunc);

        final var frame = new JFrame("ライフゲーム：" + getName());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocation(20, 20);
        frame.setResizable(false);

        final var contentPane = frame.getContentPane();
        contentPane.setLayout(new FlowLayout());
        contentPane.add(fieldPanel);
        contentPane.add(controlPanel);

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

    private void fieldChangedEventHandlerFunc(LifeGame<L, F> sender, LifeGame.FieldChangeEventArgs eventArgs) {

        SwingUtilities.invokeLater(() -> {
            generateLifeButton.setEnabled(sender.isInitialState());
            if (!autoNextButton.isSelected()) {
                previousButton.setEnabled(!sender.isFirstGeneration());
            }

            currentGenerationLabel.setText(createCurrentGenerationText());
            calculatedGenerationLabel.setText(createCalculatedGenerationText());

            cellComponents.entrySet().stream()
                .forEach(entry -> entry.getValue().updateToolTipText());

            fieldPanel.repaint();
        });
    }

    private void cellChangedEventHandlerFunc(LifeGame<L, F> sender, LifeGame.CellChangeEventArgs eventArgs) {
        var cellComponent = cellComponents.get(eventArgs.getPosition());

        SwingUtilities.invokeLater(() -> {
            cellComponent.updateToolTipText();
            cellComponent.repaint();
        });
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
