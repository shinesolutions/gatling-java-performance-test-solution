# AWS SDK project for starting loadtest on AWS

### Prerequisites
The code assumes the required infra is available, so make sure that it is created using `gatling-infra` project.

### Commands

* `mvn clean compile exec:exec` compile and run

### Parameters
Params are set using env vars.  
Required:  
* `VPC_ID`: Vpc to get subnets from  
* `CLUSTER_NAME`: Name of the Fargate cluster to run task(s) on  
* `TASK_DEFINITION`: Name of the Fargate task definition to run on the cluster  
* `REPORT_BUCKET`: S3 bucket that the Docker container will write its result too
* `SIMULATION`: Name of the simulation to run
* `ENVIRONMENT`: Name of the test environment where simulation needs to run

Optional:
* `SIMULATION_TYPE`: Type of the simulation
* `NUM_OF_LOAD_GENERATORS`: The number of ECS tasks (Docker containers) that will be started. Default `1`
* `USERS`: The number of users per ECS task (Docker container). Default `0`
* `RAMP_UP_DURATION`: Override the rampup duration of the Gatling test.
* `PEAK_LOAD_DURATION`: Override the peak load duration of the Gatling test.
* `THROUGHPUT`: Override the throughput of the Gatling test.
* `UOM`: Override the unit of measure of the Gatling test. This could either be "MINUTES" or "SECONDS".


#### Example command
Set the environment variables when running locally (MacOS):
* `VPC_ID=vpc-abc123; export VPC_ID \
CLUSTER_NAME=gatling-performance-test-cluster; export CLUSTER_NAME\
TASK_DEFINITION=gatling-tests; export TASK_DEFINITION \
REPORT_BUCKET=s3-gatling-results-prashant; export REPORT_BUCKET \
NUM_OF_LOAD_GENERATORS=3; export NUM_OF_LOAD_GENERATORS \
SIMULATION=simulations.PostCode.PostCodeSimulation; export SIMULATION \
SIMULATION_TYPE=loadtest; export SIMULATION_TYPE \
ENVIRONMENT=stg; export ENVIRONMENT \
USERS=0; export USERS \
PEAK_LOAD_DURATION=0; export PEAK_LOAD_DURATION\
RAMP_UP_DURATION=0; export RAMP_UP_DURATION \
TARGET_RPM=0; export TARGET_RPM`

Run the test:
* `mvn clean compile exec:exec`