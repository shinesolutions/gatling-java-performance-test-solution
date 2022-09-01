package models;

import java.util.HashMap;

public class SimulationValues {

    private int noOfUsers = 1;
    private int rampUpDuration; //seconds
    private int peakLoadDuration; //seconds
    private int targetRps;
    private int pacingMin; //seconds
    private int pacingMax; //seconds
    private String environment;

    private HashMap<String, Double> userDistribution;

    public int getNoOfUsers() {
        return noOfUsers;
    }

    public void setNoOfUsers(int noOfUsers) {
        this.noOfUsers = noOfUsers;
    }

    public int getRampUpDuration() {
        return rampUpDuration;
    }

    public void setRampUpDuration(int rampUpDuration) {
        this.rampUpDuration = rampUpDuration;
    }

    public int getPeakLoadDuration() {
        return peakLoadDuration;
    }

    public void setPeakLoadDuration(int peakLoadDuration) {
        this.peakLoadDuration = peakLoadDuration;
    }

    public int getTargetRps() {
        return targetRps;
    }

    public void setTargetRps(int targetRps) {
        this.targetRps = targetRps;
    }

    public int getPacingMin() {
        return pacingMin;
    }

    public void setPacingMin(int pacingMin) {
        this.pacingMin = pacingMin;
    }

    public int getPacingMax() {
        return pacingMax;
    }

    public void setPacingMax(int pacingMax) {
        this.pacingMax = pacingMax;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public HashMap<String, Double> getUserDistribution() {
        return userDistribution;
    }

    public void setUserDistribution(HashMap<String, Double> userDistribution) {
        this.userDistribution = userDistribution;
    }
}
