# SeqWare QueryEngine Web GUI

This is currently a simple web GUI for the SeqWare QueryEngine. It is currently run separate 
from the web service and allows the user to query data and upload to and from the web service.
This was built on top of [angular-seed](https://github.com/angular/angular-seed).

## How to Install

* Install node.js
* Install dependencies with:

<pre>npm install</pre>

* Run the node web application server

<pre>node scripts/web-server.js</pre>

* Head to [http://localhost:8000/app/index.html](http://localhost:8000/app/index.html)

## To Do:

* Integrate deployment along with the web service in Tomcat?
* Remove visibility to the whole directory
* Refactor code, current URLs are hard-coded.. Should centralize to a config
* Extend features to the remainder of the web service REST API endpoints
* Write unit tests
* Write integration tests
