package ru.spb.leti.GameWindow;

import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JLabel;

public class InfoMistakeCounter extends JLabel {
    private int mistakeCounter;

    public InfoMistakeCounter() {
        setFont(new Font("Arial", Font.BOLD, Toolkit.getDefaultToolkit().getScreenSize().height / 50));
        display();
    }

    private void display() {
        setText("Ошибок: " + mistakeCounter);
    }

    public void setNumberOfMistakes(int numberOfMistakes) {
        this.mistakeCounter = numberOfMistakes;
        display();
    }
}
