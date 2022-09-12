package simulations.PostCode;

import static io.gatling.javaapi.core.CoreDsl.*;

import config.SimulationConfigDetails;
import config.EnvironmentConfigDetails;
import io.gatling.javaapi.core.*;
import models.SimulationValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenarios.PostCodeScenario;

import java.time.Duration;


public class PostCodeSimulation extends Simulation {

    public PostCodeSimulation() throws Exception {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentConfigDetails.class);

    // Provide Simulation Name
    String simulationName = "PostCode";

    // Define simulation type to be used for the test
    String simulationType = SimulationConfigDetails.getEnvVarOrDefault("SIMULATION_TYPE", "loadtest");

    SimulationValues simulationValues = SimulationConfigDetails.getSimulationValues(simulationName, "PostCodeConfig", simulationType);

    // Define the Scenario
    ScenarioBuilder PostcodeScenario = PostCodeScenario.PostCodeScn_GetPostCodes(simulationValues);

    // Define the Simulation
    {
        setUp(
                PostcodeScenario.injectOpen(rampUsers(simulationValues.getNoOfUsers()).during(Duration.ofSeconds(simulationValues.getRampUpDuration())))
                        .throttle(
                                reachRps(simulationValues.getTargetRps()).in(simulationValues.getRampUpDuration()),
                                holdFor(simulationValues.getPeakLoadDuration())
                        )
        );//.maxDuration(Duration.ofSeconds(simulationValues.getRampUpDuration() + simulationValues.getPeakLoadDuration()));
    }
}
