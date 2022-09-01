package config;

import Utils.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import models.SimulationConfig;
import models.SimulationValues;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class SimulationConfigDetails {
    private static String uom = "";
    private static SimulationConfig simulationConfig = new SimulationConfig();
    private static final SimulationValues simulationValues = new SimulationValues();
    private static SimulationConfig.Simulations.Simulation simulation = new SimulationConfig.Simulations.Simulation();

    /**
     * Opens the Json file containing the simulation configuration parameters such as TargetRPM
     * @param configFileName Name of the config file excluding extension (.json) to be used for simulation
     *  @return An object that contains the elements from the Json file
     */
    public static SimulationConfig getSimulationConfigJson(String configFileName) throws IOException {

        InputStream inputStream = Objects.requireNonNull(JsonUtils.class.getClassLoader().getResourceAsStream("config/" + configFileName + ".json"));
        JsonNode json = JsonUtils.readJsonFromInputStream(inputStream);
        return JsonUtils.nodeToObject(json, SimulationConfig.class);

    }

    /**
     * Helper function for getting environment variable
     * @param name Name of the environment variable
     * @param defaultValue Default value for the specified environment variable to be used when valid value isn't available
     * @return value
     */
    public static String getEnvVarOrDefault(String name, String defaultValue) {
        if (System.getenv(name) == null || Objects.equals(System.getenv(name), ""))
            return defaultValue;
        else
            return System.getenv(name);

    }

    /**
     * Get the simulation values from the config json
     * @param simulationName Name of the running simulation
     * @param simulationConfigFileName Name of the simulation's configuration file name
     * @param simulationType Type of the simulation
     * @return noOfUsers, rampUpDuration, peakLoadDuration
     */
    public static SimulationValues getSimulationValues(String simulationName, String simulationConfigFileName, String simulationType) throws Exception {

        StringBuilder summary = new StringBuilder("\n******************** SIMULATION VALUES ********************");

        summary.append("\nSimulation: ").append(simulationName);
        summary.append("\nSimulation Configuration File Name: ").append(simulationConfigFileName);
        summary.append("\nSimulation Type: ").append(simulationType);
        summary.append("\n**********************************************************");

        //Open the Json which contains simulation details
        simulationConfig = getSimulationConfigJson(simulationConfigFileName);

        SimulationConfig.Simulations simulations = simulationConfig
                .getSimulations()
                .stream().filter(sims -> sims.getSimulation() != null)
                .findFirst()
                .orElse(null);

        if (simulations == null)
            throw new Exception("ERROR - Simulation configs do not exist in - " + simulationConfigFileName);

        simulation = simulations
                .getSimulation()
                .stream()
                .filter(sim -> sim.getSimulationType().equals(simulationType))
                .findFirst()
                .orElse(null);

        if (simulation == null)
            throw new Exception("ERROR - Invalid Simulation type provided - " + simulationType + "This simulation type does not exist in Simulation");

        getEnv();
        summary.append("\nEnvironment: ").append(simulationValues.getEnvironment());

        getUom();
        getNoOfUsers();
        summary.append("\nUsers: ").append(simulationValues.getNoOfUsers());

        // Get the durations
        getDurations();
        summary.append("\nRamp-up Duration: ").append(simulationValues.getRampUpDuration()).append(" second(s)");
        summary.append("\nPeak Duration: ").append(simulationValues.getPeakLoadDuration()).append(" second(s)");

        getThroughput();
        summary.append("\nThroughput: ").append(simulationValues.getTargetRps()).append(" requests per second");

        List<SimulationConfig.Simulations.ActionWeighting> actionWeighting = getActionWeighting();

        double userDistSum = 0;
        HashMap<String, Double> userDistribution = new HashMap<>();

        for (SimulationConfig.Simulations.ActionWeighting weighting : actionWeighting) {

            // Get sum of all user distributions
            userDistSum = userDistSum + weighting.getUserDistribution();

            // Create a map with user distribution name and the distribution
            userDistribution.put(weighting.getUserDistributionName(), weighting.getUserDistribution());
        }

        // Check sum of all weightings from the simulation type config equals to 100
        if (userDistSum != 100)
            throw new Exception ("ERROR - Sum of User Distribution must be 100. Current sum is - " + userDistSum);

        simulationValues.setUserDistribution(userDistribution);

        summary.append("\n***********************************************************");

        System.out.println(summary);
        return simulationValues;
    }

    /***
     * Calculates the iteration pacing required to meet the input RPS (target RPS) based on number of users and requests
     * This function should be called only after getSimulationValues has been called to set the simulationValues.
     */
    public static SimulationValues calculateIterationPacing() throws Exception {

        StringBuilder summary = new StringBuilder("\n******************** ITERATION PACING VALUES ********************");
        double weightedRequestsPerIteration = 0;

        List<SimulationConfig.Simulations.ActionWeighting> actionWeighting = getActionWeighting();

        for (SimulationConfig.Simulations.ActionWeighting weighting : actionWeighting) {
            // Get the requests per iteration
            weightedRequestsPerIteration = weightedRequestsPerIteration + (weighting.getUserDistribution() * weighting.getRequestsPerIteration() * 0.01);

        }

        summary.append("\nRequests Per Iteration: ").append(weightedRequestsPerIteration);

        int pacingMin = 0;
        int pacingMax = 0;

        if (simulationValues.getTargetRps() > 0) {
            int requiredPacing = (int) (((simulationValues.getNoOfUsers() / simulationValues.getTargetRps()) * weightedRequestsPerIteration) * 1000);
            pacingMin = (int) (requiredPacing - (requiredPacing * 0.1));
            pacingMax = (int) (requiredPacing + (requiredPacing * 0.1));

            //Check pacing is set to at least 2 seconds per request to ensure it will take effect
            if((requiredPacing/1000)  < (weightedRequestsPerIteration * 2)) {
                throw new Exception("ERROR - Calculated pacing too low - Pacing value of " + requiredPacing +
                        " ms calculated to reach target of " + simulationValues.getTargetRps() + " RPS with " + simulationValues.getNoOfUsers() +
                        " user(s). Increase the number of users or reduce the Target RPM. ");
            }
        }

        simulationValues.setPacingMin(pacingMin);
        simulationValues.setPacingMax(pacingMax);

        summary.append("\nPacing Average: ").append(simulationValues.getPacingMin() + ((simulationValues.getPacingMax() - simulationValues.getPacingMin()) / 2)).append(" ms");
        summary.append("\nPacing Minimum: ").append(simulationValues.getPacingMin()).append(" ms");
        summary.append("\nPacing Maximum: ").append(simulationValues.getPacingMax()).append(" ms");

        int pacingMaxSeconds = simulationValues.getPacingMax()/1000;

        //Throw an error if pacing greater than the ramp up duration
        if (pacingMaxSeconds/1000 > simulationValues.getRampUpDuration())
            throw new Exception("ERROR - Calculated pacing is greater than ramp up duration. Pacing of " + pacingMaxSeconds +
                    " seconds is greater than the ramp up duration of " + simulationValues.getRampUpDuration() + " seconds. Decrease the pacing by decreasing the number of users or increasing the ramp up duration"

            );

        summary.append("\n***********************************************************\n");

        System.out.println(summary);
        return simulationValues;
    }

    /**
     * Get the noOfUsers for the simulation
     */
    private static void getNoOfUsers() {
        // Get the environment values if available or set a default value
        simulationValues.setNoOfUsers(Integer.parseInt(getEnvVarOrDefault("USERS", "0")));

        // Get the number of users from simulation config if it is 0
        if (simulationValues.getNoOfUsers() == 0)
            simulationValues.setNoOfUsers(simulation.getNumberOfUsers());
    }

    /**
     * Get the ramp up duration and peak load duration for the simulation
     */
    private static void getDurations() {

        // Get the environment values if available or set a default value
        int rampUpDuration = Integer.parseInt(getEnvVarOrDefault("RAMP_UP_DURATION", "0"));
        int peakLoadDuration = Integer.parseInt(getEnvVarOrDefault("PEAK_LOAD_DURATION", "0"));

        // Get rampUpDuration from simulation config if not set in environment
        if (rampUpDuration == 0)
            rampUpDuration = simulation.getRampUpDuration();

        // Get peakLoadDuration from simulation config if not set in environment
        if (peakLoadDuration == 0)
            peakLoadDuration = simulation.getPeakLoadDuration();

        // Convert rampUpDuration & peakLoadDuration to seconds if it is provided in minutes
        if (uom.equalsIgnoreCase("minutes")) {
            rampUpDuration = rampUpDuration * 60;
            peakLoadDuration = peakLoadDuration * 60;
        }

        simulationValues.setRampUpDuration(rampUpDuration);
        simulationValues.setPeakLoadDuration(peakLoadDuration);
    }

    /**
     * Get the throughput (targetRPS) for the simulation
     */
    private static void getThroughput(){
        // Get the environment values if available or set a default value
        int throughput = Integer.parseInt(getEnvVarOrDefault("THROUGHPUT", "0"));

        // Get throughput from simulation config if not set in environment
        if (throughput == 0)
            throughput = simulation.getThroughput();

        // Convert throughput to per second if it is provided in minutes
        if (uom.equalsIgnoreCase("minutes"))
            throughput = throughput / 60;

        simulationValues.setTargetRps(throughput);
    }

    /**
     * Get the test environment for the simulation
     */
    private static void getEnv() {
        // Get the environment values if available or set a default value
        simulationValues.setEnvironment(getEnvVarOrDefault("environment", ""));

        // Get the environment from simulation config if it is blank
        if (Objects.equals(simulationValues.getEnvironment(), ""))
            simulationValues.setEnvironment(simulation.getEnvironment());
    }

    /**
     * Get the Unit of Measure for the simulation
     */
    private static void getUom() {
        // Get the environment values if available or set a default value
        uom = getEnvVarOrDefault("UOM", "");

        // Get uom from simulation config if not set in environment
        if (Objects.equals(uom, ""))
            uom = simulation.getUom();
    }

    private static List<SimulationConfig.Simulations.ActionWeighting> getActionWeighting() throws Exception {
        List<SimulationConfig.Simulations.ActionWeighting> actionWeighting = new ArrayList();
        // If simulation specific weighting exist then use it. Otherwise, use the default weightings.
        if(simulation.getActionWeighting().size() > 0)
            actionWeighting.addAll(simulation.getActionWeighting());
        else {
            SimulationConfig.Simulations defaultWeightingSimulations = simulationConfig
                    .getSimulations()
                    .stream().filter(sims -> sims.getDefaultWeighting() != null)
                    .findFirst()
                    .orElse(null);

            if (defaultWeightingSimulations == null)
                throw new Exception("ERROR - Default weighting and the Simulation weighting are not provided.");

            actionWeighting.addAll(defaultWeightingSimulations.getDefaultWeighting());

        }

        return actionWeighting;
    }

}
