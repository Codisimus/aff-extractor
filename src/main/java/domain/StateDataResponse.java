package domain;

import java.util.ArrayList;
import java.util.List;

public class StateDataResponse {
    String state;
    List<CountyData> counties = new ArrayList<>();

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<CountyData> getCounties() {
        return counties;
    }

    public void setCounties(List<CountyData> counties) {
        this.counties = counties;
    }

    public void addCountyData(CountyData countyData){
        getCounties().add(countyData);
    }

    @Override
    public String toString() {
        return "StateDataResponse{" +
                "state='" + state + '\'' +
                ", counties=" + counties +
                '}';
    }
}
