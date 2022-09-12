package scenarios;

import config.EnvironmentConfigDetails;

import io.gatling.javaapi.core.Choice;
import io.gatling.javaapi.core.ScenarioBuilder;
import models.SimulationValues;
import requests.PostCodeRequests;

import static io.gatling.javaapi.core.CoreDsl.*;

public class PostCodeScenario {

    public static ScenarioBuilder PostCodeScn_GetPostCodes(SimulationValues simulationValues) throws Exception {
        String pcHost = EnvironmentConfigDetails.getHostBaseUrl(simulationValues.getEnvironment(), "PostCode");

        return scenario("PostcodeScenario")
                .forever().on(
                        //pace(Duration.ofMillis(simulationValues.getPacingMin()), Duration.ofMillis(simulationValues.getPacingMax()))
                                randomSwitch().on(

                                Choice.withWeight(simulationValues.getUserDistribution().get("Get_Postcode_Random"),
                                        exec(PostCodeRequests.GET_Postcode_Random(pcHost))),

                                Choice.withWeight(simulationValues.getUserDistribution().get("Get_Postcode"),
                                        exec(PostCodeRequests.GET_Postcode(pcHost, "OX495NU")))
                        )
                );
    }

}
