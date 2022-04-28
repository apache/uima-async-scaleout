Apache UIMA Asynchronous Scaleout (UIMA-AS) Version 2.10.3
==========================================================

What's New in 2.10.3
--------------------

- Modified client code to assign unique ClientID to broker connection
- Fixed ClassCastException when async aggregate initializes delegate with JMS Service Descriptor
- Fixed broken classpath and logging for UIMA-AS run configurations

Click [issuesFixed/jira-report.hmtl](issuesFixed/jira-report.html) for
the list of issues fixed in this release.


Contents of Apache UIMA-AS binary distribution
----------------------------------------------

The Apache UIMA-AS binary distribution includes
  
- Apache UIMA Java SDK version 2.10.2
- Apache UIMA Asynchronous Scaleout extensions
- Saxon
- Apache ActiveMQ version 5.15.2
- Spring Framework version 4.3.9
- XMLBeans


Known problems/limitations with Release 2.10.3
----------------------------------------------

* When connecting to an AMQ broker behind a firewall, avoid using the 
  `maxInactivityDuration=0` decoration on the brokerURL 
  (see: http://activemq.apache.org/configuring-wire-formats.html)
  as it turns off AMQ 'keep alive' messaging. Without these, a firewall may assume 
  a connection has become stale and close its ports. 
* To monitor multiple UIMA AS services on the same machine, each must be assigned a
  unique JMX port (see section 3.8).
* JCAS caching Enable/Disable not supported in the Deployment Editor GUI.   
* In cases where Analysis Engine deployed in UIMA AS service throws a user-defined
  exception, an application hosting UIMA AS client must include such exception 
  class in its classpath. Otherwise, ClassNotFoundException is thrown in the UIMA 
  AS client while deserializing a reply. 
 
For up-to-date information on UIMA-AS issues, see our issue tracker:
https://issues.apache.org/jira/browse/UIMA-5385?filter=12317266&jql=project%20%3D%20UIMA%20AND%20component%20%3D%20%22Async%20Scaleout%22%20AND%20fixversion%3D2.10.2AS%20and%20status%3DClosed


Crypto Notice
-------------

This distribution includes cryptographic software.  The country in 
which you currently reside may have restrictions on the import, 
possession, use, and/or re-export to another country, of 
encryption software.  BEFORE using any encryption software, please 
check your country's laws, regulations and policies concerning the
import, possession, or use, and re-export of encryption software, to 
see if this is permitted.  See <http://www.wassenaar.org/> for more
information.

The U.S. Government Department of Commerce, Bureau of Industry and
Security (BIS), has classified this software as Export Commodity 
Control Number (ECCN) 5D002.C.1, which includes information security
software using or performing cryptographic functions with asymmetric
algorithms.  The form and manner of this Apache Software Foundation
distribution makes it eligible for export under the License Exception
ENC Technology Software Unrestricted (TSU) exception (see the BIS 
Export Administration Regulations, Section 740.13) for both object 
code and source code.

The following provides more details on the included cryptographic
software:

This distribution includes portions of Apache ActiveMQ, which, in
turn, is classified as being controlled under ECCN 5D002.

