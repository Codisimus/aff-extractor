package domain;

import java.util.Map;

public class AcsHeader {
    Map<String, AcsCell> cells;

    public Map<String, AcsCell> getCells() {
        return cells;
    }

    public void setCells(Map<String, AcsCell> cells) {
        this.cells = cells;
    }

    @Override
    public String toString() {
        return "AcsHeader{" +
                "cells=" + cells +
                '}';
    }
}
