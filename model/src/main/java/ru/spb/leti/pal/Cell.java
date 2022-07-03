package ru.spb.leti.pal;

import lombok.Getter;
import lombok.Setter;

public class Cell {
    @Getter
    private DictionaryPair pair;// пара - объект из словаря
    @Getter @Setter
    private Position position;// координаты ячейки на поле
    @Getter
    private boolean flag;// флаг для обозначения отображаемого слова

    public Cell(DictionaryPair pair, Position position, boolean flag) {
        this.pair = pair;
        this.flag = flag;
        this.position = position;
    }

    @Override
    protected Cell clone() {
        return new Cell(pair, position, flag);
    }
}

