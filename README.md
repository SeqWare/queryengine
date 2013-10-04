## Introduction 

This README is just a quick overview of building the SeqWare Query Engine. See our
[project homepage](http://seqware.github.io/docs/8-query-engine/) for much more documentation.

This contains the 3 major components of the query engine project along with
documentation:

* seqware-queryengine-backend
* seqware-queryengine-legacy
* seqware-queryengine-webservice

The seqware-distribution sub-project provides a location for building a "fat" distribution jar. 

## Prerequisites 

###A Recent Linux Distribution

This pretty much goes without saying but the SeqWare project is targeted at
Linux.  You may be able to compile and use the software on MacOS X but, in all
honesty, we recommend you use a recent Linux distribution such as Debian
(Ubuntu, Linux Mint, etc) or RedHat (RedHat Enterprise, Fedora, etc).  This
software, although written in Java mostly, was never intended to work on
Windows. If you need to use Windows for development or deployment we recommend
you simply use our VirtualBox VM for both activities, see our extensive documentation
on http://seqware.github.com for more information. You can also use this same
approach on MacOS (or even another version of Linux).

###Java

SeqWare requires Oracle JDK 1.6 or greater, we primarily write and test with JDK 1.6.x.
An example of instructions on how to update your Linux installation can be found [here](https://ccp.cloudera.com/display/CDH4DOC/Before+You+Install+CDH4+on+a+Single+Node#BeforeYouInstallCDH4onaSingleNode-InstalltheOracleJavaDevelopmentKit). You will need to use the method appropriate to your distribution to install this.

## Building 

### Getting the Source Code 

Our source code is available from [GitHub](https://github.com/SeqWare/seqware) or the "Fork me on GitHub" banner at the upper right of our website

To get a copy of of our source code you will first need to install Git (<code>sudo apt-get install git</code> in Ubuntu) and then clone our repository.

<pre title="Cloning the git repository">
<span class="prompt">~$</span> <kbd>git clone git@github.com:SeqWare/queryengine.git</kbd>
Cloning into 'seqware'...
remote: Counting objects: 8984, done.
remote: Compressing objects: 100% (2908/2908), done.
remote: Total 8984 (delta 4308), reused 8940 (delta 4265)
Receiving objects: 100% (8984/8984), 33.57 MiB | 392 KiB/s, done.
Resolving deltas: 100% (4308/4308), done.
</pre>

By default, this will land you on the default branch. You will want to check-out the latest release. 

For example:

	~$ cd queryengine/
	~/seqware_github$ git checkout 1.0.4

### Building and Automated Testing 

We're moving to Maven for our builds, this is currently how
you build without running any tests in the trunk directory:

    mvn clean install -DskipTests

Maven now runs unit tests as follows (unit tests in the SeqWare context are quick tests that do not require the embedded HBase or Tomcat instance):

    mvn clean install  

When this is complete: 

    export MAVEN_OPTS="-Xmx1024m -XX:MaxPermSize=512m" 

(This ensures that enough memory is allocated for integration tests)

    mvn clean install -DskipITs=false

(This runs all unit tests and integration tests)

You can also build individual components such as the query engine web service with: 

    cd seqware-queryengine-webservice
    mvn clean install

###Problems with Maven

Sometimes we run into problems when building, strange missing dependency issues
and broken packages. A lot of the time this is an issue with Maven, try
deleting your ~/.m2 directory and running the build process again.


## Installing

See our [Installation Guide](http://seqware.github.com/docs/2-installation/) for detailed installation instructions
including links to a pre-configured virtual machine that can be used for
testing, development, and deployment.

## Copyright

Copyright 2008-2013 Brian D O'Connor, OICR, UNC, and Nimbus Informatics, LLC

## Contributors

Please see our [partners and contributors](http://seqware.github.com/partners/)

## License

SeqWare is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SeqWare is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SeqWare.  If not, see <http://www.gnu.org/licenses/>.


