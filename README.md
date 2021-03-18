# log-analyzer
Linux system log(/var/log/message) analyzer based on REST api using scala akka framework

# Requirements
sbt (1.4.7), 
scala (2.13.5), 
java 11

# Configuration
Clone the project
and open the file (src/main/resources/application.conf) and provide the log-file location and port.

And add the log messages in the file. 

There are sample log message format in file (src/main/resources/messages.log) where each line represents a log message

# Run
In a console, change directories to the top level of the project

For example, if you used the default project name, log-analyzer, and extracted the project to your root directory, from the root directory, 
enter: cd log-analyzer

Then enter: sbt run

# Test API's
Collection of postman requests can be found here: src/main/resources/log-analyzer.postman_collection.json
