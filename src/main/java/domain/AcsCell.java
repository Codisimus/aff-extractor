package domain;

import java.util.Map;

public class AcsCell {
    String valueType;
    String valueScale;
    Map<String, AcsCategory> categories;

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getValueScale() {
        return valueScale;
    }

    public void setValueScale(String valueScale) {
        this.valueScale = valueScale;
    }

    public Map<String, AcsCategory> getCategories() {
        return categories;
    }

    public void setCategories(Map<String, AcsCategory> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "AcsCell{" +
                "valueType='" + valueType + '\'' +
                ", valueScale='" + valueScale + '\'' +
                ", categories=" + categories +
                '}';
    }
}
