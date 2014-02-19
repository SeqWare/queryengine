## Web Service (And Associated Swagger Documentation)

The web service can be started by running 

    mvn tomcat7:run

Then access the root of the Swagger documentation [here](http://localhost:8889/seqware-queryengine-webservice/api/api-docs)
This json file describes all the available web resources. 
However, you will probably be more interested in the Swagger UI which presents this in human readable form 
[here](http://localhost:8889/seqware-queryengine-webservice/ui/)

For update purposes (and credit), this is a copy of [swagger-ui](https://github.com/wordnik/swagger-ui).

Note that if your swagger documents are hosted on a remote server (such as a VM) you will need to change the value of 'localhost' to something more appropriate such as a hostname/ip address in  src/main/webapp/WEB-INF/web.xml and  src/main/webapp/ui/index.html 

## Database connectivity

If you want your web service to talk to a query engine back-end, please ensure that your .seqware/settings file is setup as described in seqware-queryengine/README.md

## IDE 

For quicker debugging, it is possible to setup an Apache Tomcat server in NetBeans. This way you will be able to run, debug, and quickly apply code changes from within your IDE. 
