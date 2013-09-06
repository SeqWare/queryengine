## Web Service (And Associated Swagger Documentation)

The web service can be started by running 

    mvn tomcat6:run

Then access the root of the Swagger documentation [here](http://localhost:8889/seqware-queryengine-webservice/api/api-docs)
This json file describes all the available web resources. 
However, you will probably be more interested in the Swagger UI which presents this in human readable form 
[here](http://localhost:8889/seqware-queryengine-webservice/ui/)

For update purposes (and credit), this is a copy of [swagger-ui](https://github.com/wordnik/swagger-ui).

## Database connectivity

If you want your web service to talk to a query engine back-end, please ensure that your .seqware/settings file is setup as described in seqware-queryengine/README.md
