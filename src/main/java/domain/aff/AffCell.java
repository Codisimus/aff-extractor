package domain.aff;

import java.util.Map;

public class AffCell {
    private String valueType;
    private String valueScale;
    private Map<String, AffCategory> categories;

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

    public Map<String, AffCategory> getCategories() {
        return categories;
    }

    public void setCategories(Map<String, AffCategory> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "AffCell{" +
                "valueType='" + valueType + '\'' +
                ", valueScale='" + valueScale + '\'' +
                ", categories=" + categories +
                '}';
    }
}
