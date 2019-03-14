package domain;

import java.util.List;

public class USDataResponse {
    private String year;
    private List<StateDataResponse> states;

    public USDataResponse() {
    }

    public USDataResponse(String year,List<StateDataResponse> states) {

        this.year = year;
        this.states = states;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<StateDataResponse> getStates() {
        return states;
    }

    public void setStates(List<StateDataResponse> states) {
        this.states = states;
    }

    @Override
    public String toString() {
        return "USDataResponse{" +
                "year='" + year + '\'' +
                ", states=" + states +
                '}';
    }
}
