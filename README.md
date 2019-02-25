RateChanges
=====
RateChanges - application to display a list of changes for currency rates for the selected date.

Requires Java 8 to build and (in WAR case) Apache Tomcat 9.0.x to run.

**Application as JAR:**
* how to build: "mvnw clean install"
* how to run: "java -jar target/ratechanges-0.0.1-SNAPSHOT.jar"
* how to access: "http://localhost:8080/changes.html"
    
**Application as WAR:**
* how to build: "mvnw -f pom-war.xml clean install"
* how to run: copy "target/ratechanges-0.0.1-SNAPSHOT.war" file into Apache Tomcat 9.0.x "webapps" folder; start Apache Tomcat
* how to access: "http://localhost:8080/ratechanges-0.0.1-SNAPSHOT/changes.html"


**Application structure**

Solution is implemented as Spring Boot application.

Backend consists of several main components:
* `RateProvider` interface and "LB service based" implementation, responsible for providing currency rates for specified date
* `ChangesService`, responsible for calculating currency rates changes
* `ChangesRestController`, responsible for providing JSON formatted data through REST API

Frontend consists of simple HTML page with jQuery script to make AJAX request to REST backend and display the results.

Suggested JSF framework was turned down as too outdated and inflexible.