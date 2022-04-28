Apache UIMA Asynchronous Scaleout (UIMA-AS)
===========================================

What is UIMA-AS?
----------------

UIMA Asynchronous Scaleout (AS) is an augmented version of Apache UIMA
that additionally provides a set of capabilities for achieving flexible
scaleout. It is a second generation design, replacing the CPM and Vinci
Services. The CPM and Vinci are still available and are not being
deprecated, but new designs are encouraged to use UIMA-AS for
scalability, and current designs reaching limitations may want to move
to UIMA-AS.

UIMA-AS is integrated with the new flow controller architecture, and can
be applied to both primitive and aggregate analysis engines. It fully
supports CAS Multipliers.


Building from the Source Distribution
-------------------------------------

We use Maven 3.3.+ for building; download this if needed, 
and set the environment variable `MAVEN_OPTS to -Xmx800m -XX:MaxPerSize-256m`.

Then build from the directory containing this README by issuing the command

    mvn clean install.
   
This builds everything except the ...source-release.zip file. If you want that,
change the command to 

    mvn clean install -Papache-release
   
Look for the result in the two artifacts: 

    target/uima-as-[version]-source-release.zip (if run with -Papache-release) and
    target/uima-as-[version]-bin.zip (or  ...tar.gz) 

If your maven build fails with strange errors try running maven with an update flag
like this:

    mvn -U clean install
   
Note that a build with -U option takes much longer to finish.

For more details, please see http://uima.apache.org/building-uima.html


It includes the base UIMA binary distribution which it depends on.

UIMA-AS components, in addition to those that come with base UIMA include:

#### Shell scripts

* `bin/startBroker.sh/bat`: starts the ActiveMQ broker, which must be running before
  UIMA AS services can be deployed.
* `bin/deployAsyncService.sh/bat`: deploys an AnalysisEngine as a UIMA-AS
  service.  Takes one or more UIMA-AS Deployment Descriptors as arguments.
* `bin/runRemoteAsyncAE.sh/bat`: Calls a UIMA-AS service. Takes arguments 
  specifying the location of the service, and an optional `CollectionReader` 
  descriptor file used to obtain the CASes to be processed by the service.

#### Documentation
 
* `docs/d/uima_async_scaleout.pdf`: UIMA-AS documentation, including the 
  specification for the deployment descriptor file syntax.

#### Examples
 
* `examples/deploy/as/...`  (Sample Deployment Descriptors)
  * `Deploy_RoomNumberAnnotator.xml`: Deploys Room Number Annotator Primitive AE
  * `Deploy_MeetingDetectorTAE.xml`: Deploys Meeting Detector Aggregate AE with all
    delegates in the same JVM.
  * `Deploy_MeetingDetectorTAE_Whiteboard.xml`: Deploys Meeting Detector Aggregate 
     AE using the whiteboard Flow Controller.
  * `Deploy_MeetingDetectorTAE_RemoteRoomNumber.xml`: Deploys Meeting Detector 
    Aggregate AE that uses remotely deployed RoomNumberAnnotator.
  * `Deploy_MeetingDetectorTAE_3MeetingAnnotator.xml`: Deploys Meeting Detector 
    Aggregate AE with three instances of the MeetingAnnotator component.
  * `Deploy_MeetingDetectorTAE_Sync_3Instances.xml`: Deploys 3 instances of the
    Meeting Detector as a Synchronous Aggregate (meaning the delegate AEs do not 
    each get their own input queue).
  * `Deploy_MeetingAnnotator.xml`: Deploys C++ Meeting Annotator. Note: requires
    installation of uimacpp SDK into `$UIMA_HOME`.
  * `MeetingFinderAggregate.xml`: Aggregate descriptor that use the same components 
     as the CPE examples MeetingFinderCPE* in base UIMA.
  * `Deploy_MeetingFinder.xml`: Deploys MeetingFinderAggregate illustrating 
    scalability and error handling similar to the CPM examples; see the notes on migration below.

#### Descriptors:
 
* `descriptors/as/...`  (Other Sample Descriptors for use with UIMA AS)
  * `MeetingDetectorAsyncAE.xml:` Specifier that can be used to call a UIMA AS
    Service from an existing UIMA application; see below.


Installation and Setup
----------------------

### Supported Platforms

Apache UIMA-AS requires Java version 8 or later; it has been tested with Sun/Oracle Java 8 and IBM 8.

Running the Eclipse plugin tooling for UIMA requires you start Eclipse using a Java 8 or later, as well.

The supported platforms are: Windows, Linux, and Mac OS X. Other platforms and Java (8+) 
implementations should work, but have not been significantly tested.


### Environment Variables

After you have unpacked the UIMA AS distribution, you must perform the following
environment variable settings (the same as for normal Apache UIMA setup):

* Set `JAVA_HOME` to the directory of your JRE installation you would like to use for UIMA.
* Set `UIMA_HOME` to the apache-uima-as directory of your unpacked Apache UIMA distribution
* Append `UIMA_HOME/bin` to your `PATH`

NOTE: The Mac OS X operating system has special procedures for setting up global 
    environment variables; see http://developer.apple.com/qa/qa2001/qa1067.html for 
    how to do this.


### Running the Setup Script

You must run the script `UIMA_HOME/bin/adjustExamplePaths.bat` (or `.sh`).  This 
updates paths in the examples based on the actual UIMA_HOME directory path.


### Setting up Eclipse

Eclipse users should install the UIMA Eclipse Plugins and UIMA Examples Project 
using the procedure described in Chapter 3 of the Apache UIMA Overview and Setup 
guide, which you can find online at http://uima.apache.org; click on Documentation 
-> HTML Online Version -> Overview and Setup -> 3. Eclipse IDE setup for UIMA.
 
NOTE: The minimal version of Eclipse for UIMA-AS plugins is Mars (v.4.5.2).

Since UIMA AS requires Java 8, you must be sure to set up your `uimaj-examples` 
Eclipse project to use a version 8 (or later) JRE, and you must set your compiler 
compliance level to 8.0. To do this go to **Window->Preferences** and navigate to the Java->Compiler page. Remember to run the base Eclipse using Java 8 (or later), as well.


Getting Started
---------------

### Starting the ActiveMQ Broker

UIMA AS includes a partial distribution of Apache ActiveMQ to support core 
functionality of the broker. This includes a single broker support, network of 
brokers with fixed URLs, stomp, persistence, http, ssl, openwire, springframework 
integration, and jetty. If you need additional broker functionality you need to 
download and install a complete version of ActiveMQ from 
http://activemq.apache.org/download.html.

UIMA AS services require an ActiveMQ broker to be available with which to 
create/register the service request queue. If no broker is available, start a new 
broker on the same machine the services will run on or another machine; this is 
done by first setting an env parameter `ACTIVEMQ_BASE` pointing at a writable 
directory, or simply by cd'ing to a writable directory, and running:

    startBroker.sh/bat

The first time run this script will create a new directory `$ACTIVEMQ_BASE/amq` (or 
`./amq`) and default configuration files will be copied there. The configuration 
files can then be customized to modify broker behavior for subsequent startups.

NOTE: only one broker can be started at a time on the same machine with the same
      configuration file, or on different machines from the same writable directory.

When the broker starts it will print a message such as:

    INFO TransportServerThreadSupport - Listening for connections at: tcp://yourHostname:61616

Note this URL since you will need it to run services and clients.

The tcp listening port must be exposed to any clients or services using the broker.
To connect to a broker running behind a firewall using HTTP tunneling, see below.

### Deploying an Analysis Engine as a UIMA AS Asynchronous Service

* Create a Deployment Descriptor.
  Examples can be found in the `examples/deploy/as` directory,
  and the syntax is documented in `docs/d/uima_async_scaleout.pdf`.
  
  NOTE: One of the things that the deployment descriptor may contain is a broker 
  placeholder with this syntax: `${defaultBrokerURL}`. The placeholder is replaced 
  at runtime with an actual broker URL. The value for the placeholder comes from 
  System properties. The `brokerURL` attribute of `<inputQueue ...>` element is 
  optional. If not present, a default of `tcp://localhost:61616` will be used.

  The examples assume the broker is listening on `tcp://localhost:61616`.

* Run the command:

      deployAsyncService.sh/cmd [testDD.xml] [-brokerURL url]

  The argument to the command is the deployment descriptor you created in step (a). 
  An optional argument `-brokerURL` specifies a URL of the broker that the service 
  will use to create connections to queues. This argument takes effect only if your 
  deployment descriptor does not explicitly name the broker URL in the 
  `<inputQueue ...>` xml element *or* the `brokerURL` attribute is set to a 
  placeholder `${defaultBrokerURL}`. 
  Omitting brokerURL or using a placeholder is a way to keep your deployment 
  descriptors portable. You don't need to edit your deployment descriptors when 
  switching brokers.

  NOTE: If you use import by name in your deployment descriptor, UIMA AS searches 
  the `CLASSPATH` as well as directories on `UIMA_DATAPATH` to resolve the import.
 
  NOTE: `deployAsyncService.sh/cmd` scripts launch `UimaBootstrap` main program w
  which loads UIMA jars dynamically from `UIMA_HOME/lib`, 
  `UIMA_HOME/apache-activemq`, and `UIMA_HOME/apache-activemq/lib` directories. If 
  you want to use a different version of ActiveMQ, set the `ACTIVEMQ_HOME`
  environment variable to the location of ActiveMQ you intend to use. When using 
  different version of ActiveMQ ( older than version 5.4), start
  the broker using a startup script located in `ACTIVEMQ_HOME/bin` directory 
  instead of `UIMA_HOME/bin/startBroker.[cmd|sh]` script provided in this release. 
  This is due to the fact that the 5.4 branch of ActiveMQ xml schema has changed. The `activemq_nojournal.xml` provided in this release two optimizations: `schedulerSupport=false` to disable broker message scheduler, and `producerFlowControl="false"` to turn off producer flow control.
  
  Also, if you want to deploy your own annotator that is installed in a different 
  directory than `UIMA_HOME/lib`, set the `UIMA_CLASSPATH` environment variable to 
  point to one or more Classpath entries;  these entries can either be
  directories of Java class files, Jar files, or directories of Jar files (in which 
  case, all the Jars are added in an arbitrary order). Separate multiple entries 
  using `File.pathSeparator`.
  
  NOTE: Both UIMA AS client and UIMA AS service by default add a time-to-live (TTL) 
  to every request message. This enables expiration of messages that are not 
  consumed. Currently, the UIMA AS client multiplies the Process Timeout value by 
  10 and uses this as TTL. The UIMA AS service sets TTL to the Timeout value 
  multiplied by the number of outstanding requests. To disable TTL, add a system 
  property `-DNoTTL`. A convenient way to set this parameter is by adding `-DNoTTL` 
  to the env parameter `UIMA_JVM_OPTS` before running deployAsyncService and/or `runRemoteAsyncAE`.  
  

### Calling a UIMA AS Asynchronous Service

To test a remote UIMA service you can use the script:

    runRemoteAsyncAE.sh/cmd brokerUrl endpoint

This connects to a remote AE at specified brokerUrl and endpoint (which must match
the inputQueue endpoint in the remote AE service's deployment descriptor).

A subset of the optional arguments to runRemoteAsyncAE are:
* `-c`: Specifies a CollectionReader descriptor.  The client will obtain CASes from 
  the `CollectionReader` and send them to the service for processing. If this 
  option is omitted, one empty CAS will be sent to the service (useful for services
  containing a CAS Multiplier acting as a collection reader).

* `-d`: Specifies a deployment descriptor. The specified service will be deployed 
  before processing begins, and the service will be undeployed after processing 
  completes. Multiple `-d` entries can be given.

* `-o`: Specifies an Output Directory.  All CASes received by the client's
  `CallbackListener` will be serialized to XMI in the specified `OutputDir`.
  If omitted, no XMI files will be output.
        
The full set of arguments are documented if you type the command with no arguments.


### Quick Test of an async service

Start two terminal windows, each with an environments setup as described earlier

* In the first terminal window start the broker, by running the commands:
  
      cd some-writable-directory     
      startBroker.sh/bat

* In the second terminal window, deploy a sample service and send it some CASes:

      cd $UIMA_HOME/examples/deploy/as
      runRemoteAsyncAE.sh/cmd tcp://localhost:61616 MeetingDetectorTaeQueue \
          -d Deploy_MeetingDetectorTAE.xml \
          -c $UIMA_HOME/examples/descriptors/collection_reader/FileSystemCollectionReader.xml

  If you get an `UnsupportedClassVersionError`, Java 8 is probably not being used.
  If the driver fails to find the input data, adjustExamplePaths was probably not run.

  To target specific service instance, use these steps:

      cd $UIMA_HOME/examples/deploy/as
      deployAsyncService.sh/cmd Deploy_MeetingDetectorTAE.xml
  
  Note this process PID. Assuming the process is launched on a machine with 
  IP `1.1.1.1` and the PID is `2222`, launch runRemoteAsyncAE as follows:

      runRemoteAsyncAE.sh/cmd tcp://localhost:61616 MeetingDetectorTaeQueue \
            -c $UIMA_HOME/examples/descriptors/collection_reader/FileSystemCollectionReader.xml
            -TargetServiceId 1.1.1.1:2222
  
  When the process completes you should see multiple lines of output that look like this:
  
        CAS received by service with selector: 1.1.1.1:2222
          

### Calling a UIMA AS Asynchronous Service from an Existing UIMA Application

You can also call a UIMA AS Service from the `DocumentAnalyzer` or any other UIMA
application using a new JMS client Service Descriptor
(see section 1.7 in the UIMA Asynchronous Scaleout documentation). 
However, note that this is a synchronous interface, that is, it will process only 
one CAS at a time, so it will not take advantage of the scalability that UIMA AS 
provides.  To process more than one CAS at a time, you can use the Asynchronous 
UIMA AS Client, or write your own application using the UIMA AS Client APIs.

An example JMS client service descriptor is provided in

    examples/descriptors/as/MeetingDetectorAsyncAE.xml

The JMS service makes use of the customResourceSpecifier capability in Apache UIMA.
For more information on the customResourceSpecifier see the "Custom Resource 
Specifiers" section in the Apache UIMA Reference manual.


### Firewalls between clients and services

A service running behind a firewall can be accessed as long as its input queue
is on a broker that is accessable. For example, the service can register with a
public broker running outside the firewall. Alternatively the broker may be 
configured to tunnel over HTTP. For details see 
http://activemq.apache.org/configuring-transports.html

The UIMA-AS ships with http support enabled. This is configured in broker configuration file found in `$UIMA_HOME/as_config/activemq-nojournal.xml`. By 
default, the broker listens for http based connections on port 8080. If you need to 
change the port, please modify broker  configuration file by changing the following 
line:

    <transportConnector name="http" uri="http://0.0.0.0:8080"/>

    
### Monitoring a broker and its queues

When the broker starts it will print a message such as:

    INFO  ManagementContext  - JMX consoles can connect to service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi

Connect a JMX console to this service with:

    $JAVA_HOME/bin/jconsole service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi
         
NOTE: jconsole is available in Java SDK (not JRE) distributions from Sun

If your console is not on the same machine as the broker, replace localhost by
the name of the broker's machine.  The default ports for the broker (61616) and 
for the JMX server (1099) can be overridden in the broker configuration file 
created when the broker is first started. For more details see 
http://activemq.apache.org/jmx.html


### Monitoring UIMA AS service

UIMA AS service monitoring is available via JMX and jConsole. To enable this, 
please set the following before starting a service:

    set UIMA_JVM_OPTS=-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=<uniquePortNumber, e.g. 8009> 
                      -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false

Connect a jConsole to this JMX service as described in 3.7 above using the appropriate port, e.g.

    $JAVA_HOME/bin/jconsole service:jmx:rmi:///jndi/rmi://localhost:8009/jmxrmi

Under the MBeans tab, expand org.apache.uima in the left panel to view UIMA components enabled for JMX monitoring.

NOTE: In case when opening an RMI port for JMX monitoring is not possible due to 
security concerns, you can disable JMX via optional argument 
`-Duima.as.enable.jmx=false`. With this setting, the UIMA-AS will start with no
JMX support and an RMI port will not be opened.


### Stopping UIMA AS service

A service can be stopped from a command line or remotely using jConsole and JMX.
When the service is launched it displays a prompt on stdout:

    Enter 'q' to quiesce and stop the service or 's' to stop it now: 

It reads stdin expecting either `q` or `s`, ignoring all other characters. When `q` 
is typed, the service will quiesce and stop. As part of this, the service closes 
its input queue and waits until all CASes still "in-play" are finished. When the 
input CAS is returned the service stops. When 's' is typed the service
closes its input queue and immediately releases all CASes being processed and stops.

To stop UIMA AS process remotely or if the process runs in a background use 
jConsole and JMX. Using approach described in 3.8 above launch jConsole. Once the 
connection is created, in the left pane open:

    org.apache.uima
        ee.jms.service
            <Your Annotator Name> Uima EE Service
                  Controller
                      Operations
                           
Here you will find two buttons labeled:

    CompleteProcessingAndStop
    StopNow

CompleteProcessingAndStop will initiate quiesce while StopNow will initiate a hard stop.

### Migration from CPM to UIMA-AS

Migrating a collection processing engine from the CPM to UIMA-AS is straightforward.

First, migrate the CPE descriptor to a standard UIMA aggregate descriptor:  
create a UIMA aggregate that includes all the components specified in the CPE 
descriptor. Transfer any parameter overrides in the CPE descriptor to the aggregate
descriptor. Note that the aggreate descriptor must set 
`<multipleDeploymentAllowed>` to `false` to be consistent with collection reader 
and CAS consumer delegates.

Second, test this aggregate descriptor by instantiating the aggregate and sending 
it a single CAS. The contents of the CAS are not important; its purpose is to start 
the collection reader delegate which will then create the actual CASes to be 
processed by the other aggregate components. The CAS Visual Debugger, CVD, is a 
useful tool for doing this test.

Next, create a UIMA-AS deployment descriptor that specifies desired scaleout and error handling. Vinci services are still supported, although it is recommended to replace them with UIMA-AS services to enable more efficient load balancing and greater scaleout capability.

An example of this kind of migration is embodied by the sample descriptors:

    Original:  
      $UIMA_HOME/examples/descriptors/collection_processing_engine/MeetingFinderCPE_Integrated.xml
    Migrated:
      $UIMA_HOME/examples/deploy/as/MeetingFinderAggregate.xml
      $UIMA_HOME/examples/deploy/as/Deploy_MeetingFinder.xml


### Service Targeting (New Feature)

Service targeting allows an application client to send CASes to a specific instance 
of UIMA-AS service. This feature can be used to determine if a service is viable or 
not. When a service starts, it creates a new listener on its input queue which 
handles messages containing property `TargetServiceId`. By default, the
property value has a format `<IP>:<PID>`. If an incoming message contains the 
property with a value matching service `<IP>:<PID>`, the listener will dequeue the 
message and process a CAS contained therein. Optionally, the UIMA-AS service 
deployer may choose a different value for the `TargetServiceId` property. To 
override the default include `-DTargetServiceId=<value>` on the service command 
line. The `<value>` may be an arbitrary string with no spaces.

To target a specific service instance, an application client must use UIMA-AS client API. For async calls, use

    sendCAS(CAS cas, String serviceId)
 
and for synchronous calls, use 

    sendAndReceiveCAS(CAS aCAS, List<AnalysisEnginePerformanceMetrics> componentMetricsList, String serviceId)

where `serviceId` is a unique service identifier as described above.


How to Get Involved
-------------------

The Apache UIMA project really needs and appreciates any contributions,
including documentation help, source code and feedback. If you are
interested in contributing, please visit
<http://uima.apache.org/get-involved.html>.


How to Report Issues
--------------------

The Apache UIMA project uses JIRA for issue tracking. Please report any
issues you find at <http://issues.apache.org/jira/browse/uima>
