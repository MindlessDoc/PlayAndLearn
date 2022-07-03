package ru.spb.leti.pal;

public class Move {
    Cell first;
    Cell second;

    Move() {
    }

    Move(Cell a, Cell b) {
        first = a;
        second = b;
    }

    public Move(Move a) {
        first = a.first;
        second = a.second;
    }
}
