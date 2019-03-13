package domain;

public class AcsRowData {
    String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "AcsRowData{" +
                "value='" + value + '\'' +
                '}';
    }
}
