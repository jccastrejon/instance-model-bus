1. Plugins that should be loaded:

	- Apache Velocity Engine (1.6.2)
	- felix shell start file:///Users/jccastrejon/.m2/repository/org/apache/velocity/com.springsource.org.apache.velocity/1.6.2/com.springsource.org.apache.velocity-1.6.2.jar

	- Apache Commons Collections (3.2.1)
	- felix shell start file:///Users/jccastrejon/.m2/repository/org/apache/commons/com.springsource.org.apache.commons.collections/3.2.1/com.springsource.org.apache.commons.collections-3.2.1.jar
	
	- Apache Commons Lang (2.4.0)
	- felix shell start file:///Users/jccastrejon/.m2/repository/org/apache/commons/com.springsource.org.apache.commons.lang/2.4.0/com.springsource.org.apache.commons.lang-2.4.0.jar
		
	- mx.itesm.imb (0.1.0.BUILD-SNAPSHOT)
	- felix shell start file:///Users/jccastrejon/java/workspace_AgoDic2010/InstanceModelBus/target/mx.itesm.imb-0.1.0.BUILD-SNAPSHOT.jar


2. IMB command: imb update controllers

	- For each registered controller:
		- An aspect [Entity]_Controller_Roo_Imb.aj is created
		- This aspect modifies the create, update and delete methods to invoke the IMB before returning
		- The associated entities are processed by Jaxb Schemagen
		- The marshalling info is registered in Spring files