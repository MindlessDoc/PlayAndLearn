package ru.spb.leti.pal;

import lombok.Getter;
import lombok.Setter;

public class Position {
    @Getter @Setter
    int vertical;
    @Getter @Setter
    int horizontal;

    public Position(int a, int b) {
        vertical = a;
        horizontal = b;
    }

    public int getVertical() {
        return vertical;
    }

    public int getHorizontal() {
        return horizontal;
    }

    public boolean equals(Position coor) {
        return coor.horizontal == horizontal && coor.vertical == vertical;
    }

    @Override
    public String toString() {
        return vertical + " " + horizontal;
    }
}
