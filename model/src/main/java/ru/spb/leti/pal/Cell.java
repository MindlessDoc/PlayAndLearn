package ru.spb.leti.pal;

import lombok.Getter;
import lombok.Setter;

public class Cell {
    @Getter
    private DictionaryPair pair;// пара - объект из словаря
    @Getter @Setter
    private Position position;// координаты ячейки на поле
    @Getter @Setter
    private String displayedWord;
    /**
     * Является ли слово ячейки правым в {@code DictionaryPair}
     */
    @Getter
    private boolean right;

    public Cell(DictionaryPair pair, Position position, boolean right) {
        this.pair = pair;
        this.right = right;
        this.position = position;
        this.displayedWord = right ? pair.getSecond() : pair.getFirst();
    }

    @Override
    protected Cell clone() {
        return new Cell(pair, position, right);
    }
}

