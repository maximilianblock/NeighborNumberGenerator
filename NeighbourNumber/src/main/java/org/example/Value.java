package org.example;

import java.util.Objects;

public class Value {
    Coord pos;
    int val;

    public Value(int x, int y, int val) {
        this.val = val;
        pos = new Coord(x, y);
    }

    public Coord getPos() {
        return pos;
    }

    public int getVal() {
        return val;
    }

    public String getString() {
        String str = this.pos.getX() + "," + this.pos.getY() + "," + this.val;
        return str;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Value value = (Value) o;
        return val == value.val && pos.equals(value.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos, val);
    }
}
