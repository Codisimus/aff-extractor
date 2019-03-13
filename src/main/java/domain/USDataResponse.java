package domain;

import java.util.List;

public class USDataResponse {
    private String dataset;
    private String year;
    private String program;
    private List<StateDataResponse> states;

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
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
                "dataset='" + dataset + '\'' +
                ", year='" + year + '\'' +
                ", program='" + program + '\'' +
                ", states=" + states +
                '}';
    }
}
