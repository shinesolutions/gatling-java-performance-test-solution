package simulations.PostCode;

/* This class is only an example to show how a basic simulation looks without using the framework.
This is for demo purpose only. Do not write the tests in this form. Use the PostCodeSimulation as an example.
 */

import io.gatling.javaapi.core.Choice;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.http.HttpDsl.http;

public class BasicSimulationWithoutFramework extends Simulation {

    // Define headers
    static Map<String,String> headers = new HashMap<>() {{
        put("Content-Type", "application/json");
    }};

    // Scenario Definition - Actions performed by users
    ScenarioBuilder PostcodeScenario = scenario("PostcodeScenario")
            .forever().on(
                    randomSwitch().on(

                            Choice.withWeight(55,
                                    exec(http("GET_Postcode_Random")
                                            .get("http://api.postcodes.io/random/postcodes")
                                            .headers(headers)))
                            ,

                            Choice.withWeight(45,
                                    exec(http("GET_Postcode")
                                            .get("http://api.postcodes.io/postcodes/OX495NU")
                                            .headers(headers)))
                    )
            );

    ScenarioBuilder PostcodeScenario1 = scenario("PostcodeScenario")
            .forever().on(
                    randomSwitch().on(

                            Choice.withWeight(100,
                                    exec(http("GET_Postcode")
                                            .get("http://api.postcodes.io/postcodes/OX495NU")
                                            .headers(headers)))
                    )
            );


    // Simulation - Define simulation values such as number of users, rampup time etc
    {
        {
            setUp(
                    PostcodeScenario1.injectOpen(rampUsers(20).during(Duration.ofSeconds(30)))
                            .throttle(
                                    reachRps(2).in(30),
                                    holdFor(60)
                            )
            );
        }
    }

}
