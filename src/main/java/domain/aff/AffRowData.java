package domain.aff;

public class AffRowData {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "AffRowData{" +
                "value='" + value + '\'' +
                '}';
    }
}
