package domain;

import java.util.Map;

public class AcsRow {
    Map<String, AcsCategory> categories;
    Map<String, AcsRowData> cells;

    public Map<String, AcsCategory> getCategories() {
        return categories;
    }

    public void setCategories(Map<String, AcsCategory> categories) {
        this.categories = categories;
    }

    public Map<String, AcsRowData> getCells() {
        return cells;
    }

    public void setCells(Map<String, AcsRowData> cells) {
        this.cells = cells;
    }

    @Override
    public String toString() {
        return "AcsRow{" +
                "categories=" + categories +
                ", cells=" + cells +
                '}';
    }
}
