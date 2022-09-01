package models;

import java.util.List;

public class SimulationConfig {

    private List<Simulations> simulations;

    public List<Simulations> getSimulations() {
        return simulations;
    }

    public static class Simulations {

        private List<ActionWeighting> defaultWeighting;
        private List<Simulation> simulation;

        public List<ActionWeighting> getDefaultWeighting() {
            return defaultWeighting;
        }

        public List<Simulation> getSimulation() {
            return simulation;
        }

        public static class Simulation {

            private String simulationType;
            private String environment;
            private int numberOfUsers;
            private int rampUpDuration;
            private int peakLoadDuration;
            private int throughput;
            private String uom;
            private List<ActionWeighting> actionWeighting;

            public String getSimulationType() {
                return simulationType;
            }

            public String getEnvironment() {
                return environment;
            }

            public int getNumberOfUsers() {
                return numberOfUsers;
            }

            public int getRampUpDuration() {
                return rampUpDuration;
            }

            public int getPeakLoadDuration() {
                return peakLoadDuration;
            }

            public int getThroughput() {
                return throughput;
            }

            public String getUom() {
                return uom;
            }

            public List<ActionWeighting> getActionWeighting() {
                return actionWeighting;
            }
        }

        public static class ActionWeighting {
            private String userDistributionName;
            private double userDistribution;
            private int requestsPerIteration;

            public String getUserDistributionName() {
                return userDistributionName;
            }

            public double getUserDistribution() {
                return userDistribution;
            }

            public int getRequestsPerIteration() {
                return requestsPerIteration;
            }
        }
    }

}
