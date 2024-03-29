package ru.spb.leti.StartWindow;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import ru.spb.leti.GameWindow.GameWindow;
import ru.spb.leti.GameWindow.Game;


public class StartWindow extends JFrame {
    public int tableSizeX = 6;
    public int tableSizeY = 6;
    public int cellSizeX = 200;
    public int cellSizeY = 75;

    public StartWindow(List<Integer> params) {
        initStartParams(params);
        init();
    }

    private void initStartParams(List<Integer> params) {
        if (params.size() > 0) {
            tableSizeX = params.get(0);
        }

        if (params.size() > 1) {
            tableSizeY = params.get(1);
        }

        if (params.size() > 2) {
            cellSizeX = params.get(2);
        }

        if (params.size() > 3) {
            cellSizeY = params.get(3);
        }
    }
    private void init() {
        System.setProperty("file.encoding", "UTF-8");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //получаем разрешение экрана
        JButton startButton = new JButton("Начать игру");
        JButton continueButton = new JButton("Продолжить игру");
        JButton exitButton = new JButton("Выйти");
        JLabel tableSizeLabel = new JLabel("Размер поля:");
        JLabel cellSizeLabel = new JLabel("Размер ячейки:");
        JTextField tableSizeFieldX = new JTextField();
        JTextField cellSizeFieldX = new JTextField();
        JTextField tableSizeFieldY = new JTextField();
        JTextField cellSizeFieldY = new JTextField();
        JPanel panel = new JPanel();
        GridBagLayout gb = new GridBagLayout();
        panel.setLayout(gb);
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridheight = 1;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10, 5, 5, 0);
        c.ipadx = 0;
        c.ipady = 0;
        c.weightx = 0.0;
        c.weighty = 0.0;
        gb.setConstraints(startButton, c);
        startButton.setFont(new Font("Verdana", Font.PLAIN, 20));
        panel.add(startButton);
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 1;
        c.ipadx = 0;
        c.ipady = 0;
        c.weightx = 0.0;
        c.weighty = 0.0;
        gb.setConstraints(tableSizeLabel, c);
        tableSizeLabel.setFont(new Font("Verdana", Font.PLAIN, 20));
        panel.add(tableSizeLabel);

        c.anchor = GridBagConstraints.NORTH;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 1;
        c.gridy = 1;
        c.ipadx = 0;
        gb.setConstraints(tableSizeFieldX, c);
        tableSizeFieldX.setFont(new Font("Verdana", Font.PLAIN, 20));
        tableSizeFieldX.setText(String.valueOf(tableSizeX));
        tableSizeFieldX.setToolTipText("Укажите ширину поля (>=3)");
        panel.add(tableSizeFieldX);

        c.anchor = GridBagConstraints.NORTH;
        c.gridx = 2;
        c.gridy = 1;
        gb.setConstraints(tableSizeFieldY, c);
        tableSizeFieldY.setFont(new Font("Verdana", Font.PLAIN, 20));
        tableSizeFieldY.setText(String.valueOf(tableSizeY));
        tableSizeFieldY.setToolTipText("Укажите высоту поля (>=3) ");
        panel.add(tableSizeFieldY);

        c.anchor = GridBagConstraints.NORTH;
        c.gridx = 0;
        c.gridy = 2;
        gb.setConstraints(cellSizeLabel, c);
        cellSizeLabel.setFont(new Font("Verdana", Font.PLAIN, 20));
        panel.add(cellSizeLabel);

        c.anchor = GridBagConstraints.NORTH;
        c.gridx = 1;
        c.gridy = 2;
        c.ipadx = 20;
        gb.setConstraints(cellSizeFieldX, c);
        cellSizeFieldX.setFont(new Font("Verdana", Font.PLAIN, 20));
        cellSizeFieldX.setText(String.valueOf(cellSizeX));
        cellSizeFieldX.setToolTipText("Укажите ширину ячейки (>0)");
        panel.add(cellSizeFieldX);

        c.anchor = GridBagConstraints.NORTH;
        c.gridx = 2;
        c.gridy = 2;
        gb.setConstraints(cellSizeFieldY, c);
        cellSizeFieldY.setFont(new Font("Verdana", Font.PLAIN, 20));
        cellSizeFieldY.setText(String.valueOf(cellSizeY));
        cellSizeFieldY.setToolTipText("Укажите высоту ячейки (>0)");
        panel.add(cellSizeFieldY);

        c.gridheight = 1;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 3;
        gb.setConstraints(continueButton, c);
        continueButton.setFont(new Font("Verdana", Font.PLAIN, 20));
        panel.add(continueButton);

        c.gridheight = 1;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 4;
        gb.setConstraints(exitButton, c);
        exitButton.setFont(new Font("Verdana", Font.PLAIN, 20));
        panel.add(exitButton);

        setResizable(false);
        add(panel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //Для стартового окна используем 1/3 высоты экрана и 1/6 ширины, минимальный размер 300х100

        Dimension dimension = new Dimension(screenSize.width / 5, screenSize.height / 3);
        setSize(dimension);
        setPreferredSize(dimension);
        setMinimumSize(dimension);
        setMaximumSize(dimension);

        startButton.addActionListener(e -> {
            if (isNumeric(tableSizeFieldX.getText()) &&  isNumeric(tableSizeFieldY.getText())) {

                tableSizeX = Integer.parseInt(tableSizeFieldX.getText());
                cellSizeX = Integer.parseInt(cellSizeFieldX.getText());
                tableSizeY = Integer.parseInt(tableSizeFieldY.getText());
                cellSizeY = Integer.parseInt(cellSizeFieldY.getText());
                if(tableSizeX < 3 || tableSizeY < 3 || cellSizeX <= 0 || cellSizeY <= 0) {
                    JOptionPane.showMessageDialog(null, "Введите корректные данные.", "Ошибка данных", JOptionPane.ERROR_MESSAGE, null);
                    return;
                }
                SwingUtilities.invokeLater(() ->{
                    GameWindow gameWindow = new GameWindow(tableSizeY, tableSizeX, cellSizeX, cellSizeY);
                    gameWindow.setVisible(true);
                });
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Введите корректные данные.", "Ошибка данных", JOptionPane.ERROR_MESSAGE, null);
                tableSizeFieldX.setText("6");
                tableSizeFieldY.setText("6");
                cellSizeFieldX.setText("150");
                cellSizeFieldY.setText("75");
            }
        });

        continueButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
            int approval = fileChooser.showDialog(null, "Открыть файл");
            if(approval != JFileChooser.APPROVE_OPTION) {
                return;
            }

            Game game = new Game(2, 2);
            game.LoadProgress(fileChooser.getCurrentDirectory() + File.separator + fileChooser.getSelectedFile().getName());

            SwingUtilities.invokeLater(() -> {
                GameWindow gameWindow = new GameWindow(game.getFieldSize().getVertical(), game.getFieldSize().getHorizontal(), cellSizeX, cellSizeY);
                gameWindow.setVisible(true);
                gameWindow.getFieldPanel().continueGame(fileChooser.getCurrentDirectory() + File.separator + fileChooser.getSelectedFile().getName());
            });

            dispose();
        });

        exitButton.addActionListener(e -> this.dispose());
        setTitle("Угадайка");
        setLocationRelativeTo(null);

        Image image = Toolkit.getDefaultToolkit().createImage(getClass()
                .getClassLoader().getResource("icon.png"));
        setIconImage( image );
    }

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");

        List<Integer> params = new ArrayList<>();
        for (String arg : args) {
            params.add(Integer.valueOf(arg));
        }

        SwingUtilities.invokeLater(() -> {
            StartWindow startWindow = new StartWindow(params);
            startWindow.setVisible(true);
        });
    }

    public static boolean isNumeric(String str) {
        return str.matches("^?\\d+$");
    }
}
