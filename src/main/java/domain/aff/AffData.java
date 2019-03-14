package domain.aff;

import java.util.List;

public class AffData {
    private AffHeader header;
    private List<AffRow> rows;

    public AffHeader getHeader() {
        return header;
    }

    public void setHeader(AffHeader header) {
        this.header = header;
    }

    public List<AffRow> getRows() {
        return rows;
    }

    public void setRows(List<AffRow> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "AffData{" +
                "header=" + header +
                ", rows=" + rows +
                '}';
    }
}
