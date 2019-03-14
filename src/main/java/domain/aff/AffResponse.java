package domain.aff;

public class AffResponse {
    private String kind;
    private AffData data;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public AffData getData() {
        return data;
    }

    public void setData(AffData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AffResponse{" +
                "kind='" + kind + '\'' +
                ", data=" + data +
                '}';
    }
}
