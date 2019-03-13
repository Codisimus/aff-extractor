package domain;

import java.util.List;

public class Data {
    AcsHeader header;
    List<AcsRow> rows;

    public AcsHeader getHeader() {
        return header;
    }

    public void setHeader(AcsHeader header) {
        this.header = header;
    }

    public List<AcsRow> getRows() {
        return rows;
    }

    public void setRows(List<AcsRow> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "Data{" +
                "header=" + header +
                ", rows=" + rows +
                '}';
    }
}
