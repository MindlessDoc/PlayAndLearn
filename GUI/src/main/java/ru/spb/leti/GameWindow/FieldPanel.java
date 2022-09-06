package ru.spb.leti.GameWindow;

import com.google.common.eventbus.EventBus;
import java.awt.GridLayout;

import java.io.File;

import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import lombok.Getter;
import lombok.Setter;

import ru.spb.leti.pal.Cell;
import ru.spb.leti.pal.DictionaryPair;
import ru.spb.leti.pal.events.UndoProgressEvent;

public class FieldPanel extends JPanel {
    private int rows;
    private int columns;
    private int numberOfCorrectCells;

    @Getter
    @Setter
    private Square selected = null;
    @Getter
    private Game game;
    private Square[][] field;
    private GameWindow window;

    private boolean inProcess = false;
    private boolean doubleMistake = false;
    private LinkedList<DictionaryPair> rightMistakeList;

    private final EventBus eventBus;

    public FieldPanel(GameWindow window, int rows, int columns, EventBus eventBus) {
        this.window = window;
        this.rows = rows;
        this.columns = columns;
        this.game = new Game(rows, columns);
        this.eventBus = eventBus;

        init();
    }

    private void init() {
        System.setProperty("file.encoding", "UTF-8");
        setLayout(new GridLayout(rows, columns));

        field = new Square[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Square square = new Square(this, null, eventBus);
                add(square);
                field[i][j] = square;
            }
        }
    }

    public boolean isSelectedExists() {
        return selected != null;
    }

    public void checkTwoSquares(Square checked) {
        if (game.compareCell(selected.getPosition(), checked.getPosition())) {
            selected.setFinal();
            checked.setFinal();
            numberOfCorrectCells += 2;
        } else {
            selected.setWrong();
            checked.setWrong();
            window.getInfoPanel().getMistakeCounter().setNumberOfMistakes(game.getNumMistakes());
        }
        setSelected(null);
        if (numberOfCorrectCells == rows * columns) {
            if (game.nextField()) {
                displayField();
                window.getInfoPanel().getProgress().increase();
            } else {
                window.getInfoPanel().getTimer().stop();
                this.inProcess = false;
                if (doubleMistake) {
                    if (game.getNumMistakes() > 0) {

                        Object[] options = {"Сохранить ошибки и продолжитть",
                                "Продолжить без сохранения"};
                        int n = JOptionPane.showOptionDialog(window,
                                "Первый список ошибок пройден!",
                                "Ошибки",
                                JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                options,
                                options[1]);
                        if (n == 0) {
                            JFileChooser fileChooser;
                            while (true) {
                                fileChooser = new JFileChooser(System.getProperty("user.dir"));
                                FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
                                fileChooser.addChoosableFileFilter(filter);
                                int approval = fileChooser.showSaveDialog(null);
                                if (approval != JFileChooser.APPROVE_OPTION) {
                                    return;
                                }
                                File file1 = new File(fileChooser.getCurrentDirectory() + File.separator + fileChooser.getSelectedFile().getName() + "-" + getGame().getCurLesson().getLessonName().subSequence(0, getGame().getCurLesson().getLessonName().length() - 4) + "(err1).txt");
                                File file2 = new File(fileChooser.getCurrentDirectory() + File.separator + fileChooser.getSelectedFile().getName() + "-" + getGame().getCurLesson().getLessonName().subSequence(0, getGame().getCurLesson().getLessonName().length() - 4) + "(err2).txt");
                                if (file1.exists() || file2.exists()) {
                                    Object[] options1 = {"Да",
                                            "Нет"};
                                    int n2 = JOptionPane.showOptionDialog(this,
                                            "Вы действительно хотите перезаписать файлы?",
                                            "Подтверждение",
                                            JOptionPane.YES_NO_CANCEL_OPTION,
                                            JOptionPane.QUESTION_MESSAGE,
                                            null,
                                            options1,
                                            options1[1]);
                                    if (n2 == 0) {
                                        break;
                                    }
                                } else
                                    break;
                            }
                        }
                    }
                    game.MistakesToLesson(rightMistakeList, Game.NumMistakeType.SECOND);
                    startMistakeGame(false, null);
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    ResultWindow resultWindow = new ResultWindow(this, game.getNumMistakes(), window.getInfoPanel().getTimer().getTextTime());
                    resultWindow.setVisible(true);
                });
            }
        }
    }

    private void displayField() {
        numberOfCorrectCells = 0;
        Cell[][] cellField = game.getField();
        if (cellField == null) {
            return;
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (cellField[i][j] == null) {
                    numberOfCorrectCells++;
                }
                field[i][j].setCell(cellField[i][j]);
            }
        }

    }

    public void mixField() {
        game.mixField();
        if (game.getField() == null) {
            return;
        }
        displayField();
    }


    public void startGame() {
        this.selected = null;

        JFileChooser fileDialog = new JFileChooser(System.getProperty("user.dir"));
        int approval = fileDialog.showOpenDialog(this);
        if (approval != JFileChooser.APPROVE_OPTION) {
            return;
        }
        this.inProcess = true;
        if (window.getInfoPanel().getMixCheckBox().isSelected()) {
            game.setMixFlag(true);
        }

        if (!game.newLesson(fileDialog.getSelectedFile())) {
            return;
        }

        window.getInfoPanel().startAll();
        window.getInfoPanel().getProgress().setNumberOfSteps(game.getNumberOfSteps());
        game.nextField();

        displayField();
    }

    public void restartGame() {
        if (!game.newLesson()) {
            return;
        }

        window.getInfoPanel().startAll();
        window.getInfoPanel().getProgress().setNumberOfSteps(game.getNumberOfSteps());
        game.nextField();

        displayField();
    }

    public void continueGame(String filename) {
        this.selected = null;
        this.inProcess = true;
        int timer = game.LoadProgress(filename);
        rows = game.getFieldSize().getVertical();
        columns = game.getFieldSize().getHorizontal();
        //removeAll();
        //init();
        window.getInfoPanel().startAll();
        window.getInfoPanel().getTimer().setTime(timer);
        window.getInfoPanel().getProgress().setNumberOfSteps(game.getNumberOfSteps());
        window.getInfoPanel().getMistakeCounter().setNumberOfMistakes(game.getNumMistakes());
        window.getInfoPanel().getProgress().setCurrentStep(game.getNumOfCurStep());
        //game.nextField();
        displayField();

        //undo();
        updateUI();
    }

    public void startMistakeGame(boolean isDouble, LinkedList<DictionaryPair> right) {
        this.doubleMistake = isDouble;
        rightMistakeList = right;
        this.selected = null;
        this.inProcess = true;

        window.getInfoPanel().startAll();
        window.getInfoPanel().getProgress().setNumberOfSteps(game.getNumberOfSteps());
        game.nextField();

        displayField();
    }


    public void undo() {
        eventBus.post(new UndoProgressEvent());
        int numberBefore = game.getNumMistakes();
        game.undo();
        if (game.getNumMistakes() < numberBefore) {
            window.getInfoPanel().getMistakeCounter().setNumberOfMistakes(game.getNumMistakes());
        } else {
            displayField();
        }
    }

    public GameWindow getWindow() {
        return window;
    }

    void resizeField(int fieldSizeX, int fieldSizeY) {
        this.rows = fieldSizeY;
        this.columns = fieldSizeX;
        this.game = new Game(rows, columns);

        removeAll();
        init();
    }

    public void redraw() {
        removeAll();

        field = new Square[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Square square = new Square(this, null, eventBus);
                add(square);
                field[i][j] = square;
            }
        }

        displayField();
        updateUI();
    }

    public boolean isInProcess() {
        return inProcess;
    }

    public void stopGame() {
        if (!inProcess) {
            return;
        }
        removeAll();
        setSelected(null);
        window.getInfoPanel().getTimer().stop();
        game = new Game(rows, columns);
        numberOfCorrectCells = 0;
        inProcess = false;
        doubleMistake = false;
        rightMistakeList = null;

        displayField();
        init();
        updateUI();
    }
}
