package config;

import Utils.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import models.EnvironmentConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class EnvironmentConfigDetails {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentConfigDetails.class);

    /**
     * Opens the Json file containing the environment configuration parameters such as hosts(API endpoints)
     *  @return An object that contains the elements from the Json file
     */
    public static EnvironmentConfig getEnvironmentConfigJson() throws IOException {

        InputStream inputStream = Objects.requireNonNull(JsonUtils.class.getClassLoader().getResourceAsStream("config/EnvironmentConfig.json"));
        JsonNode json = JsonUtils.readJsonFromInputStream(inputStream);

        //String filePath = Objects.requireNonNull(JsonUtils.class.getClassLoader().getResource("config/EnvironmentConfig.json")).getFile();
        //JsonNode json = JsonUtils.readJsonFromFilePath(filePath);
        return JsonUtils.nodeToObject(json, EnvironmentConfig.class);

    }

    /**
     * Get the base URL for the host in specified environment
     * @param environmentName The name of the environment
     * @param hostName The name of the host
     * @return The base URL for the host
     */
    public static String getHostBaseUrl(String environmentName, String hostName) throws Exception {

        EnvironmentConfig.Environments environment = new EnvironmentConfig.Environments();
        EnvironmentConfig.EnvironmentDetails environmentDetails = new EnvironmentConfig.EnvironmentDetails();

        try {
            // Look for mentioned environment in EnvironmentConfig json file and store it as environment object
            environment = EnvironmentConfigDetails.getEnvironmentConfigJson()
                    .getEnvironments()
                    .stream()
                    .filter(env -> env.getEnvironment().equals(environmentName))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e){
            LOGGER.error("ERROR - Exception encountered " + e.getMessage() + "'");
        }

        if(environment == null)
            throw new Exception("ERROR - '" + environmentName + "' is not defined in EnvironmentConfig.json");

        try {
            // Look for mentioned hostname in environment object and store it as environmentDetails object
            environmentDetails = environment.getEnvironmentDetails()
                    .stream()
                    .filter(ed -> ed.getName().equals(hostName))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            LOGGER.error("ERROR - Exception encountered " + e.getMessage() + "'");
        }

        if (environmentDetails == null)
             throw new Exception("ERROR - Endpoint is not defined for host '" + hostName + "' in EnvironmentConfig.json for environment '" + environmentName + "'");

        // Return the base url from environmentDetails object
        return environmentDetails.getUrl();


    }
}
