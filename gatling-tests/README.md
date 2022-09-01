# gatling-tests

## Run
### Using Maven
`mvn clean gatling:test`

### From Jar file
```shell script
USER_ARGS=""
COMPILATION_CLASSPATH=`find -L ./target -maxdepth 1 -name "*.jar" -type f -exec printf :{} ';'`
JAVA_OPTS="-server -Xmx1G -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:+HeapDumpOnOutOfMemoryError -XX:MaxInlineLevel=20 -XX:MaxTrivialSize=12 -Djava.net.preferIPv4Stack=true -Djava.net.preferIPv6Addresses=false ${JAVA_OPTS}"
java $JAVA_OPTS $USER_ARGS -cp $COMPILATION_CLASSPATH io.gatling.app.Gatling -s nl.codecontrol.gatling.simulations.BasicSimulation
```

### Using Docker

#### Create the package 
`mvn clean package`

#### Creating docker image
`docker build -t gatling-tests .`     

#### Test docker image locally
Use docker volume to add your AWS credentials that has permission to write to the S3 bucket. You can also optionally provide the AWS profile:  
`docker run --rm -v ${HOME}/.aws/credentials:/root/.aws/credentials:ro -e SIMULATION=simulations.PostCode.PostCodeSimulation -e REPORT_BUCKET=<S3_BUCKET> [-e AWS_PROFILE=<AWS_PROFILE>] gatling-tests `  

### Running on AWS
See `gatling-aws-test-runner` module.
