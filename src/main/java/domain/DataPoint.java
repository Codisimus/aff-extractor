package domain;

public class DataPoint {
    String description;
    String value;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "DataPoint{" +
                "description='" + description + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
