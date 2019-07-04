package com.hs2n.exercise.lifegame.view.launcher;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.hs2n.exercise.lifegame.view.DefaultLifeGameView;
import com.hs2n.exercise.lifegame.view.TrilemmaLifeGameView;
import com.hs2n.exercise.lifegame.view.WithVitalityLifeGameView;

public class LifeGameLauncher {

    private static void createAndShow() {

        final var defaultLifeGameButton = new JButton("標準");
        defaultLifeGameButton.addActionListener(event -> {
            new DefaultLifeGameView()
                .newLifeGame(40, 40)
                .launch();
        });

        final var trilemmaLifeGameButton = new JButton("三すくみ");
        trilemmaLifeGameButton.addActionListener(event -> {
            new TrilemmaLifeGameView()
                .newLifeGame(40, 40)
                .birthRate(0.6)
                .launch();
        });

        final var withVitalityLifeGameButton = new JButton("体力あり");
        withVitalityLifeGameButton.addActionListener(event -> {
            new WithVitalityLifeGameView()
                .newLifeGame(30, 30)
                .birthRate(0.1)
                .cellSize(16)
                .launch();
        });

        final var frame = new JFrame("ライフゲーム");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(10, 10);
        frame.setResizable(false);

        final var contentPane = frame.getContentPane();
        contentPane.setLayout(new FlowLayout());
        contentPane.add(defaultLifeGameButton);
        contentPane.add(trilemmaLifeGameButton);
        contentPane.add(withVitalityLifeGameButton);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShow());
    }
}
