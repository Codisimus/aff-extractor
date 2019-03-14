package domain.aff;

import java.util.Map;

public class AffRow {
    private Map<String, AffCategory> categories;
    private Map<String, AffRowData> cells;

    public Map<String, AffCategory> getCategories() {
        return categories;
    }

    public void setCategories(Map<String, AffCategory> categories) {
        this.categories = categories;
    }

    public Map<String, AffRowData> getCells() {
        return cells;
    }

    public void setCells(Map<String, AffRowData> cells) {
        this.cells = cells;
    }

    @Override
    public String toString() {
        return "AffRow{" +
                "categories=" + categories +
                ", cells=" + cells +
                '}';
    }
}
