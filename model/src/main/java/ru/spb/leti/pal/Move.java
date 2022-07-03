package ru.spb.leti.pal;

import lombok.Getter;
import lombok.Setter;

public class Move {
    @Getter @Setter
    Cell first;
    @Getter @Setter
    Cell second;

    public Move() {
    }

    public Move(Cell a, Cell b) {
        first = a;
        second = b;
    }

    public Move(Move a) {
        first = a.first;
        second = a.second;
    }
}
