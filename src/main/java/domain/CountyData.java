package domain;

import java.util.LinkedHashMap;
import java.util.Map;

public class CountyData {
    private String geoId;
    private String label;
    //tableId, <columnId, <description, value>>
    //eg: "DP03_HC03_VC96",<"description", "1.0">
    private Map<String, DataPoint> dataPoints = new LinkedHashMap<>();

    public String getGeoId() {
        return geoId;
    }

    public void setGeoId(String geoId) {
        this.geoId = geoId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Map<String, DataPoint> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(Map<String, DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }

    @Override
    public String toString() {
        return "CountyData{" +
                "geoId='" + geoId + '\'' +
                ", label='" + label + '\'' +
                ", dataPoints=" + dataPoints +
                '}';
    }
}
