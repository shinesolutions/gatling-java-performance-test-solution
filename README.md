# Performance Test Solution using Gatling
A Performance test solution covering - 
* A performance test framework built using Gatling with Java programming language and Maven build tool
* Enables distributed load testing by using Dockers on AWS ECS Clusters

## Prerequisites  

### Install  
* aws-cli  
* Docker  
* Maven >= 3.5  
* Java >= 8  
* Node >= 10.3.0  

## Projects in this repository

### gatling-aws-test-runner
AWS SDK project for running the loadtest on AWS.  

See [README.md](gatling-aws-test-runner/README.md) in project. 

### gatling-infra
Contains AWS CDK project for creating needed infra for running loadtest on AWS.  

See [README.md](gatling-infra/README.md) in project. 

### gatling-tests
Maven Gatling project containing loadtest code.  
Contains Dockerfile to build image to run on AWS.  

See [README.md](gatling-tests/README.md) in project. 

## How to use

### 1. Build gatling-tests project
Build the `gatling-tests` project, so it creates the required jars needed for the Docker image.  

### 2. Build gatling-infra project  
Build the `gatling-infra` project before calling `cdk deploy`. The cdk tooling will not compile the code.  

### 3. Deploy the infra
Now deploy the infra from the `gatling-infra` project:  
`VPC_ID=<id> REPORT_BUCKET=<bucket> cdk deploy GatlingMonitoringEcsStack --profile <profile>`  
`VPC_ID=<id> REPORT_BUCKET=<bucket> cdk deploy GatlingRunnerEcsStack --profile <profile>`  

### 4. Run the test
Now run the loadtest on AWS using the `gatling-aws-test-runner` project:  
`AWS_PROFILE=<profile> VPC_ID=<id> REPORT_BUCKET=<bucket> CLUSTER=gatling-performance-test-cluster TASK_DEFINITION=gatling-tests SIMULATION=simulations.PostCode.PostCodeSimulation CONTAINERS=10 USERS=10  mvn clean compile exec:exec`

### 5. (optionally) Destroy deployed infra
When done with loadtesting the loadtest infra can easily be destroyed, saving on costs:
`VPC_ID=<id> REPORT_BUCKET=<bucket> cdk destroy GatlingEcsStack --profile <profile>`  
Make sure the S3 bucket is empty before running `cdk destroy` or it will fail since CloudFormation cannot delete S3 buckets that aren't empty.

### Important
When making changes to the Gatling code in the `gatling-tests` project, don't forget to:  
 1. build your `gatling-tests` project using Maven  
 2. and re-deploy your `GatlingEcsStack` from `gatling-infra` so AWS CDK will update your Docker image containing the Gatling code