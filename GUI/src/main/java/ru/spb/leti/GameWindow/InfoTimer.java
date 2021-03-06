package ru.spb.leti.GameWindow;

import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.Timer;

public class InfoTimer extends JLabel {
    private int seconds;
    private int minutes;
    private Timer timer;

    public InfoTimer() {
        setFont(new Font("Arial", Font.BOLD, Toolkit.getDefaultToolkit().getScreenSize().height / 36));
        timer = new Timer(1000, e -> increase());
        display();
    }

    void start() {
        timer.start();
    }

    void stop() {
        timer.stop();
    }

    void restart() {
        stop();
        seconds = 0;
        minutes = 0;
        display();
        start();
    }

    private void increase() {
        if (seconds < 59) {
            seconds++;
        } else {
            seconds = 0;
            minutes++;
        }
        display();
    }

    private void display() {
        StringBuilder out = new StringBuilder();
        if (minutes < 10) {
            out.append('0');
        }
        out.append(minutes);
        out.append(':');
        if (seconds < 10) {
            out.append('0');
        }
        out.append(seconds);
        setText(out.toString());
    }

    int getTime() {
        return minutes * 60 + seconds;
    }

    void setTime(int timeInSeconds) {
        minutes = timeInSeconds / 60;
        seconds = timeInSeconds % 60;
        display();
    }

    String getTextTime() {
        StringBuilder out = new StringBuilder();
        out.append(" " + minutes + " минут " + seconds + " секунд ");
        return out.toString();
    }
}
