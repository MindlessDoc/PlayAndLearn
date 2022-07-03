package ru.spb.leti.GameWindow;

import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JLabel;

import lombok.Setter;

public class InfoProgress extends JLabel {
    private int numberOfSteps = 1;
    @Setter
    private int currentStep;

    public InfoProgress() {
        currentStep = 1;
        setFont(new Font("Arial", Font.BOLD, Toolkit.getDefaultToolkit().getScreenSize().height / 50));
        display();
    }

    void increase() {
        if (currentStep != numberOfSteps) {
            currentStep++;
        }
        display();
    }

    public void setNumberOfSteps(int numberOfSteps) {
        this.numberOfSteps = numberOfSteps;
        display();
    }

    void display() {
        setText("Этап " + currentStep + " из " + numberOfSteps);
    }
}
