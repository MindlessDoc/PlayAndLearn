package ru.spb.leti.GameWindow;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import lombok.Getter;

import ru.spb.leti.pal.events.ProgressDoneEvent;
import ru.spb.leti.pal.events.UndoProgressEvent;

public class GameWindow extends JFrame {
    @Getter
    private FieldPanel fieldPanel;
    private InfoPanel infoPanel;
    private JMenu gameMenu;

    private int vertical;
    private int horizontal;
    private int realWidth;
    private int realHeight;

    private int cellSizeX;
    private int cellXizeY;

    private JMenuItem undoMenuItem;

    private final EventBus eventBus = new EventBus();

    public InfoPanel getInfoPanel() {
        return infoPanel;
    }

    public GameWindow(int vertical, int horizontal, int cellSizeX, int cellSizeY) {
        this.vertical = vertical;
        this.horizontal = horizontal;
        this.cellSizeX = cellSizeX;
        this.cellXizeY = cellSizeY;

        this.realWidth = cellSizeX * horizontal + Toolkit.getDefaultToolkit().getScreenSize().height / 8;
        this.realHeight = cellXizeY * vertical;

        eventBus.register(this);

        init();
        setMenuBars();
    }

    private void setMenuBars() {
        System.setProperty("file.encoding", "UTF-8");
        JMenuBar menuBar = new JMenuBar();
        JMenu infoMenu = new JMenu("Справка");
        gameMenu = new JMenu("Игра");
        JMenuItem loadGameMenuItem = new JMenuItem("Продолжить игру");
        loadGameMenuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Save files", "savepr");
            fileChooser.addChoosableFileFilter(filter);
            int approval = fileChooser.showOpenDialog(null);
            if (approval != JFileChooser.APPROVE_OPTION) {
                return;
            }
            Game game = new Game(2, 2);
            game.LoadProgress(fileChooser.getCurrentDirectory() + File.separator + fileChooser.getSelectedFile().getName());
            if (game.getFieldSize().getHorizontal() == horizontal && game.getFieldSize().getVertical() == vertical) {
                continueGame(fileChooser.getCurrentDirectory() + File.separator + fileChooser.getSelectedFile().getName());
            } else {
                JOptionPane.showMessageDialog(null, "Размер поля сохранения не совпадает с текущим.\nИзмените рамер поля на " + game.getFieldSize().getHorizontal() + " x " + game.getFieldSize().getVertical(), "Несоответсвие размера поля", JOptionPane.ERROR_MESSAGE);
            }

        });
        loadGameMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));

        JMenuItem saveGameMenuItem = new JMenuItem("Coxранить игру");
        saveGameMenuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Save files", "savepr");
            fileChooser.addChoosableFileFilter(filter);
            int approval = fileChooser.showSaveDialog(null);
            if (approval != JFileChooser.APPROVE_OPTION) {
                return;
            }
            if (!fieldPanel.getGame().SaveProgress(fileChooser.getCurrentDirectory() + File.separator + fileChooser.getSelectedFile().getName(), infoPanel.getTimer().getTime())) {
                JOptionPane.showMessageDialog(null, "Не удалось сохранить файл", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });
        saveGameMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));

        JMenuItem saveMistakesMenuItem = new JMenuItem("Сохранить ошибки");
        saveMistakesMenuItem.addActionListener(e -> {
            JFileChooser fileChooser;
            while (true) {
                fileChooser = new JFileChooser(System.getProperty("user.dir"));
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
                fileChooser.addChoosableFileFilter(filter);
                int approval = fileChooser.showSaveDialog(null);
                if (approval != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                File file1 = new File(fileChooser.getCurrentDirectory() + File.separator + fileChooser.getSelectedFile().getName() + "-" + fieldPanel.getGame().getCurLesson().getLessonName().subSequence(0, fieldPanel.getGame().getCurLesson().getLessonName().length() - 4) + "(err1).txt");
                File file2 = new File(fileChooser.getCurrentDirectory() + File.separator + fileChooser.getSelectedFile().getName() + "-" + fieldPanel.getGame().getCurLesson().getLessonName().subSequence(0, fieldPanel.getGame().getCurLesson().getLessonName().length() - 4) + "(err2).txt");
                if (file1.exists() || file2.exists()) {
                    Object[] options = {"Да",
                            "Нет"};
                    int n = JOptionPane.showOptionDialog(this,
                            "Вы действительно хотите перезаписать файлы?",
                            "Подтверждение",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[1]);
                    if (n == 0) {
                        break;
                    }

                } else
                    break;
            }
            fieldPanel.getGame().SaveMistakes(fileChooser.getCurrentDirectory() + File.separator + fileChooser.getSelectedFile().getName());
        });

        JMenuItem exitMenuItem = new JMenuItem("Выйти");
        exitMenuItem.addActionListener(e -> dispose());

        JMenuItem restartGameMenuItem = new JMenuItem("Начать сначала");
        restartGameMenuItem.addActionListener(e -> fieldPanel.restartGame());

        JMenuItem newGameMenuItem = new JMenuItem("Новая игра");
        newGameMenuItem.addActionListener(e -> fieldPanel.startGame());

        undoMenuItem = new JMenuItem("Отменить ход");
        undoMenuItem.setEnabled(false);
        undoMenuItem.addActionListener(e -> {
            fieldPanel.undo();
        });

        JMenuItem mixMenuItem = new JMenuItem("Перемешать");
        mixMenuItem.addActionListener(e -> fieldPanel.mixField());

        JMenuItem stopGameMenuItem = new JMenuItem("Прервать игру");
        stopGameMenuItem.addActionListener(e -> {
            Object[] options = {"Да",
                    "Отмена"};
            int n = JOptionPane.showOptionDialog(this,
                    "Вы действительно хотите прервать игру?",
                    "Подтверждение",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[1]);
            if (n == 0) {
                stopGame();
            }

        });

        JMenuItem infoMenuItem = new JMenuItem("Об авторах");
        infoMenuItem.addActionListener(e -> JOptionPane.showMessageDialog(null, "Версия 1.1\nИгра создана студентами гр. 6303 СПбГЭТУ \"ЛЭТИ\":\n Иванов Д.В.\n Ваганов Н.А.\n Ильяшук Д.И.\n 2018г.\n\n Модификации: Фирсов М.А.\nhttps://rutracker.org/forum/viewtopic.php?t=5587237"));

        JMenuItem settingsMenuItem = new JMenuItem("Настройки");
        settingsMenuItem.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                if (fieldPanel.isInProcess()) {
                    JOptionPane.showMessageDialog(null, "Нельзя изменить параметры размер поля/ячейки пока идет игра!", "Ошибка", JOptionPane.ERROR_MESSAGE, null);
                } else {
                    SettingsWindow settingsWindow = new SettingsWindow(this, horizontal, vertical, cellSizeX, cellXizeY);
                    settingsWindow.setVisible(true);
                }
            });
        });

        JMenuItem fontMenuItem = new JMenuItem("Задать шрифт");
        fontMenuItem.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                if (fieldPanel.isInProcess()) {
                    JOptionPane.showMessageDialog(null, "Нельзя изменить шрифт пока идет игра!", "Ошибка", JOptionPane.ERROR_MESSAGE, null);
                } else
                    StrTransform.setFontName(JOptionPane.showInputDialog("Введите название шрифта"));
            });
        });

        gameMenu.add(newGameMenuItem);
        gameMenu.add(restartGameMenuItem);
        gameMenu.add(loadGameMenuItem);
        gameMenu.add(saveGameMenuItem);
        gameMenu.add(settingsMenuItem);
        gameMenu.add(fontMenuItem);
        gameMenu.addSeparator();
        gameMenu.add(saveMistakesMenuItem);
        gameMenu.add(undoMenuItem);
        gameMenu.add(mixMenuItem);
        gameMenu.add(stopGameMenuItem);
        gameMenu.addSeparator();
        gameMenu.add(exitMenuItem);

        infoMenu.add(infoMenuItem);

        menuBar.add(gameMenu);
        menuBar.add(infoMenu);
        setJMenuBar(menuBar);
    }

    private void init() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        fieldPanel = new FieldPanel(this, vertical, horizontal, eventBus);
        panel.add(fieldPanel, BorderLayout.CENTER);

        infoPanel = new InfoPanel(this, eventBus);
        panel.add(infoPanel, BorderLayout.EAST);

        setContentPane(panel);
        setMinimumSize(new Dimension(realWidth, realHeight));
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Угадайка");
        Image image = Toolkit.getDefaultToolkit().createImage(getClass().getClassLoader().getResource("icon.png"));
        setIconImage(image);
        setLocationRelativeTo(null);
    }

    void resize(int fieldSizeX, int fieldSizeY, int cellSizeX, int cellSizeY) {
        boolean fieldSizeChanged = (vertical != fieldSizeY || horizontal != fieldSizeX);

        this.vertical = fieldSizeY;
        this.horizontal = fieldSizeX;
        this.cellSizeX = cellSizeX;
        this.cellXizeY = cellSizeY;

        this.realWidth = cellSizeX * horizontal + Toolkit.getDefaultToolkit().getScreenSize().height / 8;
        this.realHeight = cellXizeY * vertical;

        setResizable(true);
        setMinimumSize(new Dimension(realWidth, realHeight));

        setSize(new Dimension(realWidth, realHeight));
        setResizable(false);
        if (fieldSizeChanged) {
            fieldPanel.resizeField(fieldSizeX, fieldSizeY);
        }
        fieldPanel.redraw();
    }

    public void restartGame() {
        fieldPanel.restartGame();
    }

    public void resetProgress() {
        undoMenuItem.setEnabled(false);
        getInfoPanel().resetProgress();
    }

    private void stopGame() {
        resetProgress();
        fieldPanel.stopGame();
    }

    public void continueGame(String filename) {
        fieldPanel.continueGame(filename);
    }

    @Subscribe
    private void progressDone(ProgressDoneEvent event) {
        undoMenuItem.setEnabled(true);
    }

    @Subscribe
    private void progressUndo(UndoProgressEvent event) {
        undoMenuItem.setEnabled(false);
    }
}
