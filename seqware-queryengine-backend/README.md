## Build

We can start by getting your development environment setup with the appropriate prerequisites. The [Installing with a Local VM](/docs/2-installation/) guide will give you access to a VM which has these setup correctly. However, if you wish to set this up yourself and you have git and mvn installed please continue.

If you already have HBase setup (or are using the VM), it is also worth double-checking the web interface for HBase which is usually at [http://localhost:60010/master-status](http://localhost:60010/master-status) before going through our tutorials in order to confirm that HBase is setup correctly (replace localhost with the name of your master node if working on a distributed Hadoop install).

At this point, the Query Engine is built and unit tested as with any other SeqWare component, using:

	mvn clean install (builds and runs unit tests)

### Prerequisites ON Mac OS

We use [protobuf](http://code.google.com/p/protobuf/) to handle serialization and de-serialization.

On Mac OS, Protobuf requires the following installation steps:

    wget http://protobuf.googlecode.com/files/protobuf-2.4.1.tar.gz
    tar xzf protobuf-2.4.1.tar.gz
    cd protobuf-2.4.1
    ./configure
    make
    make install

### Integration Testing 

However, if you wish to do some development or run the integration tests, there are usually a number of constants that have to be set, particularly for a developer. These are currently in the <code>com.github.seqware.queryengine.Constants</code> file although can be overridden by ~/.seqware/settings. In particular, you should set your <code>NAMESPACE</code> to avoid collisions with other developers and if you wish for your distribution jar to be automatically copied to the cluster when launching MapReduce tasks, you will need to correct the <code>DEVELOPMENT_DEPENDENCY</code>.
<p class="warning"><strong>Note:</strong>
	   It is important that you check your <code>NAMESPACE</code>, <code>HBASE_REMOTE_TESTING</code>, and <code>HBASE_PROPERTIES</code> variables. They currently control the prefix for your tables, whether you connect to a local install of HBase, and which remote install of HBase you want to connect to respectively. These settings can also be controlled via the <code>~/.seqware/settings</code> settings file and in this case, the settings file will override the hard-coded variables. Instructions on how to create these key-values are available inside <code>com.github.seqware.queryengine.Constants</code>.
</p>

For our tutorial, use the following values in your ~/.seqware/settings

    #
    # SEQWARE QUERY ENGINE SETTINGS
    #
    QE_NAMESPACE=batman
    QE_DEVELOPMENT_DEPENDENCY=file:/home/<your user>/seqware_github/seqware-distribution/target/seqware-distribution-0.13.6-qe-full.jar
    QE_PERSIST=true
    QE_HBASE_REMOTE_TESTING=false
    # Connect to either HBOOT, SQWDEV, or an implicit localhost
    QE_HBASE_PROPERTIES=localhost

Note that since QE_HBASE_REMOTE_TESTING is set to false, the variables under the heading SEQWARE QUERY ENGINE AND GENERAL HADOOP SETTINGS will be used. If QE_REMOTE_TESTING is set to true and QE_HBASE_PROPERTIES is set to something different, then corresponding families of these variables have to be setup in your .seqware/settings file in order to connect to a different server. For example, if it was set to SQWDEV then you require the following:

    QE_SQWDEV_HBASE_ZOOKEEPER_QUORUM=sqwdev.res.oicr.on.ca
    QE_SQWDEV_HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT=2181
    QE_SQWDEV_HBASE_MASTER=sqwdev.res.oicr.on.ca:60000
    QE_SQWDEV_MAPRED_JOB_TRACKER=sqwdev.res.oicr.on.ca:8021
    QE_SQWDEV_FS_DEFAULT_NAME=hdfs://sqwdev.res.oicr.on.ca:8020
    QE_SQWDEV_FS_DEFAULTFS=hdfs://sqwdev.res.oicr.on.ca:8020
    QE_SQWDEV_FS_HDFS_IMPL=org.apache.hadoop.hdfs.DistributedFileSystem

The QE_DEVELOPMENT_DEPENDENCY variable is used for development time. When running map/reduce tasks, we need to upload our code to the Hadoop cluster. When running from a jar, we simply have the jar upload itself. However, when running in an IDE, we need to specify a (up-to-date) jar file for upload that contains the code for M/R. Normally, we upload the full jar. 

1. 	Refresh the code for the query engine by doing a <code>git pull</code> in the seqware_github directory. On the VM, you may need to merge changes or simply discard changes with a command such as <code>git checkout seqware-queryengine/src/main/java/com/github/seqware/queryengine/Constants.java</code>
2. 	If the [web interface](http://localhost:60010/master-status) for HBase stalls or is inactive, you may need to restart the HBase processes. This can be done by the following commands:
	<pre title="Title of the snippet">
	sudo - root (or sudo bash)
	/etc/init.d/hbase-regionserver stop
	/etc/init.d/hbase-master stop
	/etc/init.d/zookeeper-server stop
	/etc/init.d/hbase-regionserver start
	/etc/init.d/hbase-master start
	/etc/init.d/zookeeper-server start
	jps	
	</pre>
3. 	When setup of Hadoop and HBase is complete, you can go into the query-engine directory, compile it, and run the tests. Note that the integration tests will spin-up a mini-HBase cluster and perform MapReduce tasks (~10 minutes for integration tests). Please note that the web-service and legacy directories in the root have additional dependencies and may not necessarily compile following only these instructions.
	<pre title="Title of the snippet">
	mvn clean install -DskipITs=false
	mvn javadoc:javadoc
	mvn javadoc:test-javadoc
	</pre>
This will generate javadoc documentation for both the main code and the testing code in <code>seqware-queryengine/target/site/apidocs/index.html</code> and <code>seqware-queryengine/target/site/testapidocs/index.html</code> respectively. 

## Installation

By default, our integration test suite runs tests against the [hbase-maven-plugin](https://github.com/wibidata/hbase-maven-plugin). You can, however, run the full test suite against a real Hadoop and HBase cluster; for setup, a good start is to follow Cloudera's [quick start guide](https://ccp.cloudera.com/display/CDH4DOC/CDH4+Quick+Start+Guide). You will then need to set the HBase configuration in `seqware-queryengine/src/main/java/com/github/seqware/queryengine/Constants.java` by turning on HBASE_REMOTE_TESTING and completing a family of terms for HBASE\_PROPERTIES. You can also set these in an external ~/.seqware/settings file.

Note that when you setup a real Hadoop/HBase cluster and point the query engine toward it using ~/.seqware/settings, this is essentially the installation procedure for HBase.

If you run into the following error when the hbase-plugin starts up, please check for an incorrect entry in your <code>/etc/hosts</code> file.

    org.apache.hadoop.hbase.client.NoServerForRegionException: Unable to find region for  after 10 tries.
    at org.apache.hadoop.hbase.client.HConnectionManager$HConnectionImplementation.locateRegionInMeta(HConnectionManager.java:908)
    at org.apache.hadoop.hbase.client.HConnectionManager$HConnectionImplementation.locateRegion(HConnectionManager.java:814)
    at org.apache.hadoop.hbase.client.HConnectionManager$HConnectionImplementation.locateRegion(HConnectionManager.java:782)
    at org.apache.hadoop.hbase.client.HTable.finishSetup(HTable.java:249)
    at org.apache.hadoop.hbase.client.HTable.<init>(HTable.java:213)
    at org.apache.hadoop.hbase.HBaseTestingUtility.startMiniHBaseCluster(HBaseTestingUtility.java:526)

In particular, recent versions of Debian (including Ubuntu and Linux Mint) have on the second line <code>127.0.1.1  \<your hostname\></code> which should be modified to <code>127.0.0.1  \<your hostname\></code>

You can find the original bug report showing that this was done on purpose here: http://bugs.debian.org/cgi-bin/bugreport.cgi?bug=316099

I don't think RedHat-based distributions use this same convention.

If you run into the following error when the hbase-plugin starts up, please check for an incorrect entry in your <code>/etc/hosts</code> file.

    org.apache.hadoop.hbase.client.NoServerForRegionException: Unable to find region for  after 10 tries.
    at org.apache.hadoop.hbase.client.HConnectionManager$HConnectionImplementation.locateRegionInMeta(HConnectionManager.java:908)
    at org.apache.hadoop.hbase.client.HConnectionManager$HConnectionImplementation.locateRegion(HConnectionManager.java:814)
    at org.apache.hadoop.hbase.client.HConnectionManager$HConnectionImplementation.locateRegion(HConnectionManager.java:782)
    at org.apache.hadoop.hbase.client.HTable.finishSetup(HTable.java:249)
    at org.apache.hadoop.hbase.client.HTable.<init>(HTable.java:213)
    at org.apache.hadoop.hbase.HBaseTestingUtility.startMiniHBaseCluster(HBaseTestingUtility.java:526)

In particular, the latest (v. 13) version of Linux Mint has on the second line <code>127.0.1.1  \<your hostname\></code> which should be modified to <code>127.0.0.1  \<your hostname\></code>  

## Performance Toggles

You will find several performance and metic related toggles in com.github.seqware.queryengine.Constants which allow you to turn on and off versioning, tracking of tagsets, and output various metrics on the size of serialized objects. 

## Basic Commands

For a full tutorial, please see http://seqware.github.io/docs/8-query-engine/

However, some basic commands to get you started follow (replace UUID's with UUID's relevant to your run of the utilities):

Create a reference: 

    java -classpath seqware-distribution-1.0.4-SNAPSHOT-qe-full.jar com.github.seqware.queryengine.system.ReferenceCreator hg_19 keyValue_ref.out

Import a VCF file:

    java -Xmx1024m -classpath seqware-distribution-1.0.4-SNAPSHOT-qe-full.jar com.github.seqware.queryengine.system.importers.SOFeatureImporter -i ~/VariantAnnotation_0.10.4_LS1155.annotated.vcf -o keyValueVCF.out -r hg_19  -w VCFVariantImportWorker -b 5000
    
Dump a feature set to VCF file:

    java -Xmx1024m -classpath seqware-distribution-1.0.4-SNAPSHOT-qe-full.jar com.github.seqware.queryengine.system.exporters.VCFDumper 91583362-9d4d-4040-bc36-e2b457ed883e test_out.vcf

Dump a feature set to elastic search compatible JSON file:

    java -Xmx1024m -classpath seqware-distribution-1.0.4-SNAPSHOT-qe-full.jar com.github.seqware.queryengine.system.exporters.JSONDumper 3063ff4e-c206-4099-b99d-2fa5f0526ba7 test_out.json

## In testing

Run a count of all variants across all FeatureSets. Variants are identified with the tuple [chromosome, position, referenceBase, calledBase].
Ensure there first exists a destination table/family for the results, e.g., `create 'variant_aggregates', {NAME=>'hg19', VERSIONS=>1}`.

    java -cp seqware-distribution-1.0.4-SNAPSHOT-qe-full.jar  demo.VariantFreq \
    ns.hbaseTestTable_v2.Feature d \
    variant_aggregates hg19 counts

The above emits the results back into HBase.  To print the results to stdout:

    java -cp seqware-distribution-1.0.4-SNAPSHOT-qe-full.jar  demo.VariantFreqPrinter \
    variant_aggregates hg19 counts

##RUNNING BRIANS CO-OP CODE 
###(this is just for testing purposes)

####Steps to import data (these are the steps I used from http://seqware.github.io/docs/8-query-engine/#loading-data):

After provisioning from the branch MRPluginLayerTest:
````
cd ~/gitroot/seqware
mvn clean install
cd seqware-distribution/target
````

Create a reference in the HBase backend
````
java -cp seqware-distribution-1.0.7-SNAPSHOT-qe-full.jar com.github.seqware.queryengine.system.ReferenceCreator hg_19
````

Naive import of the feature from smallTest.vcf in the test reources to the HBase Backend
````
java -cp seqware-distribution-1.0.7-SNAPSHOT-qe-full.jar com.github.seqware.queryengine.system.importers.SOFeatureImporter -i ../../seqware-queryengine-backend/src/test/resources/com/github/seqware/queryengine/system/FeatureImporter/smallTest.vcf -r hg_19 -w VCFVariantImportWorker
````

At this point you can open up Hbase shell and listing the imported data by running :

````
hbase shell
list
scan 'batman.hbaseTestTable_v2.Feature.hg_19'
````
There should be 4 rows of data as it was a 3 base deletion that was specified in the smallTest.vcf.

####Steps to run a dumper plugin:

````
java -cp seqware-distribution-1.0.7-SNAPSHOT-qe-full.jar com.github.seqware.queryengine.system.exporters.ArbitraryPluginRunner hg_19 YOUR_CUSTOM_PLUGIN OUT_PUT_PARAMETERS
````

In this case you may try to run a plugin under com.github.seqware.queryengine.plugins.contribs as they all seem to run without breaking, albeit not returning information in the txt output. 

````
com.github.seqware.queryengine.plugins.contribs.TestOutputPlugin
````

Unfortunately this "TestOutputPlugin" that I have written does not output what is expected as of yet (a simple map and reduce of the naively imported data in the HBase backend). I am to clear this up on Monday.
