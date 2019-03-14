package domain.aff;

import java.util.Map;

public class AffHeader {
    private Map<String, AffCell> cells;

    public Map<String, AffCell> getCells() {
        return cells;
    }

    public void setCells(Map<String, AffCell> cells) {
        this.cells = cells;
    }

    @Override
    public String toString() {
        return "AffHeader{" +
                "cells=" + cells +
                '}';
    }
}
