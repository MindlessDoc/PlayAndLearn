package ru.spb.leti.GameWindow;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import lombok.Getter;

import ru.spb.leti.pal.events.ProgressDoneEvent;
import ru.spb.leti.pal.events.UndoProgressEvent;

public class InfoPanel extends JPanel {
    @Getter
    private GameWindow window;
    @Getter
    private InfoTimer timer;
    @Getter
    private InfoMistakeCounter mistakeCounter;
    @Getter
    private InfoProgress progress;
    private JCheckBox mixCheckBox;
    private JButton undoButton;

    private final int fontSize = Toolkit.getDefaultToolkit().getScreenSize().height / 72;

    private final EventBus eventBus;

    public JCheckBox getMixCheckBox() {
        return mixCheckBox;
    }

    public InfoPanel(GameWindow window, EventBus eventBus) {
        this.window = window;
        this.eventBus = eventBus;

        eventBus.register(this);

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(layout);

        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.gridy = 0;
        constraints.weighty = 0;
        timer = new InfoTimer();
        add(timer, constraints);

        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.gridy++;
        constraints.weighty = 0.5;
        progress = new InfoProgress();
        progress.setVisible(true);
        add(progress, constraints);


        constraints.anchor = GridBagConstraints.PAGE_END;
        constraints.gridy++;
        constraints.weighty = 0.5;
        mistakeCounter = new InfoMistakeCounter();
        add(mistakeCounter, constraints);

        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.gridy++;
        constraints.weighty = 0.5;
        undoButton = new JButton("Отменить ход");
        undoButton.setEnabled(false);
        undoButton.setVisible(false);
        undoButton.setFont(new Font("Arial", Font.BOLD, Toolkit.getDefaultToolkit().getScreenSize().height / 75));
        undoButton.addActionListener(e -> undo());
        add(undoButton, constraints);


        constraints.anchor = GridBagConstraints.PAGE_END;
        constraints.gridy++;
        constraints.weighty = 0.5;
        JCheckBox mixCheckBox = new JCheckBox("Перемешивать", true);
        this.mixCheckBox = mixCheckBox;
        mixCheckBox.setFont(new Font("Arial", Font.BOLD, Toolkit.getDefaultToolkit().getScreenSize().height / 83));
        mixCheckBox.addActionListener(e -> {
                    if (window.getInfoPanel().getMixCheckBox().isSelected()) window.getFieldPanel().getGame().setMixFlag(true);
                    else window.getFieldPanel().getGame().setMixFlag(false);
                }
        );//добавлено
        add(mixCheckBox, constraints);


        constraints.gridy++;
        constraints.weighty = 0;
        JButton mixButton = new JButton("Перемешать");
        mixButton.setFont(new Font("Arial", Font.BOLD, fontSize));
        mixButton.addActionListener(e -> getWindow().getFieldPanel().mixField());
        add(mixButton, constraints);

        constraints.gridy++;
        constraints.weighty = 0.2;
        JButton restartButton = new JButton("Начать сначала");
        restartButton.setFont(new Font("Arial", Font.BOLD, fontSize));
        restartButton.addActionListener(e -> getWindow().restartGame());
        add(restartButton, constraints);//добавлено

        constraints.gridy++;
        constraints.weighty = 0.05;
        JButton startButton = new JButton("Новая игра");
        startButton.setFont(new Font("Arial", Font.BOLD, fontSize));
        startButton.setPreferredSize(restartButton.getPreferredSize());
        startButton.addActionListener(e -> getWindow().getFieldPanel().startGame());
        add(startButton, constraints);

        progress.setVisible(false);
        mistakeCounter.setVisible(false);
        setMinimumSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().height / 8, Toolkit.getDefaultToolkit().getScreenSize().height / 2));
    }

    public void startAll() {
        undoButton.setEnabled(false);
        undoButton.setVisible(true);
        progress.setVisible(true);
        progress.setCurrentStep(1);
        mistakeCounter.setNumberOfMistakes(0);
        mistakeCounter.setVisible(true);
        timer.restart();
    }

    private void undo() {
        window.getFieldPanel().undo();
    }

    public void resetSettings() {
        undoButton.setEnabled(false);
    }

    @Subscribe
    private void progressDone(ProgressDoneEvent event) {
        undoButton.setEnabled(true);
    }

    @Subscribe
    private void progressUndo(UndoProgressEvent event) {
        undoButton.setEnabled(false);
    }
}
