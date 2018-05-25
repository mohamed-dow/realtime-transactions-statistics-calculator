# Real-Time Transactions Statistics Calculator

### Application Overview
As part of this RESTFUL API Expose 3 Endpoints :
* The main use case for our API is to calculate realtime statistic from the last 60 seconds.
* Second end point is to be called every time a transaction is made. It is also the sole input of this rest API
* Third end point used for manual in-memory cache data refresh to flush all cache data but the last 60 seconds.
* This API also provide a configurable cron springframework scheduling for each 10 minutes. For cron schedule expressions go to `https://crontab.guru/#*_*/10_*_*_*`   
 
 ### Specs
  #####  GET​ ​/statistics 
  Endpoint:` [PROTOCOL]://[HOST]:[PORT]/statistics ` -- `.GET`
  1. Header: 
          ` Accept: application/json `
          ` Content-Type: application/json `
  
   2. Returns: Empty body with either 201 or 204.
 `       {
         "sum": 1000,
         "avg": 100,
         "max": 200,
         "min": 50,
         "count": 10
         }`
  ##### Where:
  * sum is a double specifying the total sum of transaction value in the last 60 seconds
  * avg is a double specifying the average amount of transaction value in the last 60
  seconds
  * max is a double specifying single highest transaction value in the last 60 seconds
  * min is a double specifying single lowest transaction value in the last 60 seconds
  * count is a long specifying the total number of transactions happened in the last 60
  seconds
 
 ##### POST /transactions 
 Endpoint:` [PROTOCOL]://[HOST]:[PORT]/transactions ` -- `.POST`
 1. Header: 
         ` Accept: application/json `
         ` Content-Type: application/json `
 2. Payload:
   `{
  "amount": 12.3,
  "timestamp": 1478192204000
  }`
  3. Returns: Empty body with either 201 or 204.
       *  201 - in case of success
       *  204 - if transaction is older than 60 seconds
       
 ##### Where:
 * amount - transaction amount
 * timestamp - transaction time in epoch in millis in UTC time zone (this is not current
 timestamp)
 
##### GET /flush 
  *Endpoint:` [PROTOCOL]://[HOST]:[PORT]/flush ` -- `.GET`
    * Returns: HttpStatus.OK on successful execution.

### Technologies

- Spring Boot
- Spring Integration
- cron 
- JDK Java 8
- Maven 3.x.x
 
### Steps to build the Application Locally

mvn clean install

### Steps to Deploy  the Application Locally
`$JAVA_HOME/bin/java $JAVA_OPTS -jar [project.home.directory]/target/realtime-transactions-statistics-calculator*.jar`

 
### Point Of Contacts
 
- Mohamed, I Mohamed `<dow00700@gmail.com>`
