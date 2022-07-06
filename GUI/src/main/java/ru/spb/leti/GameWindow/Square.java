package ru.spb.leti.GameWindow;

import com.google.common.eventbus.EventBus;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import lombok.Getter;

import ru.spb.leti.pal.Cell;
import ru.spb.leti.pal.Position;
import ru.spb.leti.pal.events.ProgressDoneEvent;

public class Square extends JButton {
    private boolean pressed;
    private boolean selected;
    private boolean wrong;

    @Getter
    private SquareType type;
    private FieldPanel parent;
    @Getter
    private Position position;
    private Timer timer;
    @Getter
    private Cell cell = null;

    private final EventBus eventBus;

    public Square(FieldPanel parent, Cell cell, EventBus eventBus) {
        this.parent = parent;
        this.eventBus = eventBus;

        init();
    }

    public void setCell(Cell cell) {
        this.selected = false;
        this.pressed = false;

        this.removeAll();
        if(cell != null) {
            this.cell = cell;
            this.position = cell.getPosition();
            this.type = cell.isRight() ? SquareType.RIGHT : SquareType.LEFT;

            //Без понятия зачем нужен этот множитель
            //TODO попробовать выяснить и убрать
            double multiplier = cell.isRight() ? 0.9 : 1;
            StrTransform.transform(this, cell.getDisplayedWord(), (int)(getHeight() * multiplier));
        } else {
            this.type = SquareType.FINAL;
        }

        redraw();
    }

    void redraw() {
        //TODO убрать захаркоженные цвета
        removeAll();
        if(type == SquareType.FINAL) {
            setBackground(new Color(34, 139, 34));
        } else if(type == SquareType.LEFT){
            setBackground(new Color(150, 238, 238));
            StrTransform.transform(this, cell.getPair().getFirst(), getHeight());
        } else {
            setBackground(Color.WHITE);
            StrTransform.transform(this, cell.getPair().getSecond(), (int)(getHeight() * 0.9));
        }

        updateUI();
        update(getGraphics());
    }

    private void init() {
        setCell(cell);

        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                            new StringSelection(cell.getDisplayedWord()), null);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(!(e.getX() < 0 || e.getY() < 0 || e.getX() > getSize().width
                        || e.getY() > getSize().height || type == SquareType.FINAL || wrong)) {
                    clicked();
                }
            }
        });
    }

    public void clicked() {
        if(selected) {
            unselect();
            parent.setSelected(null);
        } else {
            if(parent.isSelectedExists()) {
                if(parent.getSelected().getType() == this.type) {
                    parent.getSelected().unselect();
                    parent.setSelected(this);
                    select();
                } else {
                    parent.checkTwoSquares(this);
                }
            } else {
                parent.setSelected(this);
                select();
            }
        }
    }

    private void select() {
        selected = true;
        setBackground(Color.YELLOW);
    }

    private void unselect() {
        selected = false;
        if(type == SquareType.LEFT) {
            setBackground(new Color(150, 238, 238));
        } else {
            setBackground(Color.WHITE);
        }
    }

    public void setFinal() {
        type = SquareType.FINAL;
        removeAll();
        setBackground(new Color(34, 139, 34));
        setText("");
        cell.setDisplayedWord("");
        eventBus.post(new ProgressDoneEvent());
    }

    void setWrong() {
        setBackground(Color.RED);
        wrong = true;
        selected = false;
        eventBus.post(new ProgressDoneEvent());

        timer = new Timer(1000, e -> setNormal());
        timer.start();
    }

    private void setNormal() {
        timer.stop();
        wrong = false;
        if(type == SquareType.LEFT) {
            setBackground(new Color(150, 238, 238));
        } else {
            setBackground(Color.WHITE);
        }
    }

    private enum SquareType {
        LEFT,
        RIGHT,
        FINAL
    }
}
