package domain;

public class ColumnInfo {
    String columnCode;
    String columnId;
    String columnDescription;

    public String getColumnCode() {
        return columnCode;
    }

    public void setColumnCode(String columnCode) {
        this.columnCode = columnCode;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getColumnDescription() {
        return columnDescription;
    }

    public void setColumnDescription(String columnDescription) {
        this.columnDescription = columnDescription;
    }

    @Override
    public String toString() {
        return "ColumnInfo{" +
                "columnCode='" + columnCode + '\'' +
                ", columnId='" + columnId + '\'' +
                ", columnDescription='" + columnDescription + '\'' +
                '}';
    }
}
