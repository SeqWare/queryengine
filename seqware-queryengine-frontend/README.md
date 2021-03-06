# SeqWare QueryEngine Web GUI

This is currently a simple web GUI for the SeqWare QueryEngine. It is currently run separate 
from the web service and allows the user to query data and upload to and from the web service.
This was built on top of [angular-seed](https://github.com/angular/angular-seed) and currently being tested with node v0.10.26 and npm 1.4.3.

## How to Install

* Install node.js (in Ubuntu, this can be done with apt-get install node)
* Install npm (in Ubuntu, this can be done with apt-get install npm)
* Head to the seqware-queryengine-frontend directory and install dependencies with the command:

<pre>npm install</pre>

* Change the URL to the appropriate Web Service API path in seqware-queryengine-frontend/app/js/app.js
* Run the node web application server using the command:

<pre>node scripts/web-server.js</pre>

* Head to [http://localhost:8000/app/index.html](http://localhost:8000/app/index.html)

## To Do:

* Integrate deployment along with the web service in Tomcat
* Remove visibility to the whole directory
* Write unit tests
* Write integration tests

## Note: You may have to enable CORS in the web service application server

You can enable CORS in the web service by adding this filter in the file at seqware-queryengine-webservice/target/tomcat/conf/web.xml

```xml 
<filter>
  <filter-name>CorsFilter</filter-name>
  <filter-class>org.apache.catalina.filters.CorsFilter</filter-class>
  <init-param>
    <param-name>cors.allowed.origins</param-name>
    <param-value>*</param-value>
  </init-param>
  <init-param>
    <param-name>cors.allowed.methods</param-name>
    <param-value>GET,POST,HEAD,OPTIONS,PUT</param-value>
  </init-param>
   <init-param>
    <param-name>cors.allowed.headers</param-name>
    <param-value>Content-Type,X-Requested-With,accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers</param-value>
  </init-param>
  <init-param>
    <param-name>cors.exposed.headers</param-name>
    <param-value>Access-Control-Allow-Origin,Access-Control-Allow-Credentials</param-value>
  </init-param>
  <init-param>
    <param-name>cors.support.credentials</param-name>
    <param-value>true</param-value>
  </init-param>
</filter>
<filter-mapping>
  <filter-name>CorsFilter</filter-name>
  <url-pattern>/*</url-pattern>
</filter-mapping>
```
