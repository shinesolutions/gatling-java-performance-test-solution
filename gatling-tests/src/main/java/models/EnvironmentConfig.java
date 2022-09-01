package models;

import java.util.List;

public class EnvironmentConfig {

    private List<Environments> environments;

    public List<Environments> getEnvironments() {
        return environments;
    }

    public static class Environments {
        private String environment;
        private List<EnvironmentDetails> environmentDetails;

        public String getEnvironment() {
            return environment;
        }

        public List<EnvironmentDetails> getEnvironmentDetails() {
            return environmentDetails;
        }
    }

    public static class EnvironmentDetails {
        private String name;
        private String url;

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }
    }
}
