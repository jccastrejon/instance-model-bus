1. Plugins that should be loaded:

	osgi install --url file:///Users/jccastrejon/.m2/repository/org/apache/ant/com.springsource.org.apache.tools.ant.launch/1.7.1/com.springsource.org.apache.tools.ant.launch-1.7.1.jar
	osgi install --url  file:///Users/jccastrejon/.m2/repository/org/apache/ant/com.springsource.org.apache.tools.ant/1.7.1/com.springsource.org.apache.tools.ant-1.7.1.jar
	osgi install --url  file:///Users/jccastrejon/.m2/repository/org/apache/commons/com.springsource.org.apache.commons.lang/2.4.0/com.springsource.org.apache.commons.lang-2.4.0.jar
	osgi install --url  file:///Users/jccastrejon/.m2/repository/org/apache/commons/com.springsource.org.apache.commons.collections/3.2.1/com.springsource.org.apache.commons.collections-3.2.1.jar
	osgi install --url  file:///Users/jccastrejon/.m2/repository/org/apache/velocity/com.springsource.org.apache.velocity/1.6.2/com.springsource.org.apache.velocity-1.6.2.jar
	osgi install --url  file:///Users/jccastrejon/.m2/repository/javax/xml/stream/com.springsource.javax.xml.stream/1.0.1/com.springsource.javax.xml.stream-1.0.1.jar
	osgi install --url  file:///Users/jccastrejon/.m2/repository/com/sun/xml/com.springsource.com.sun.xml.fastinfoset/1.2.2/com.springsource.com.sun.xml.fastinfoset-1.2.2.jar
	osgi install --url  file:///Users/jccastrejon/.m2/repository/javax/activation/com.springsource.javax.activation/1.1.1/com.springsource.javax.activation-1.1.1.jar
	osgi install --url  file:///Users/jccastrejon/.m2/repository/javax/xml/bind/com.springsource.javax.xml.bind/2.2.0/com.springsource.javax.xml.bind-2.2.0.jar
	osgi install --url  file:///Users/jccastrejon/.m2/repository/com/sun/xml/com.springsource.com.sun.xml.bind/2.2.0/com.springsource.com.sun.xml.bind-2.2.0.jar
	osgi install --url  file:///Users/jccastrejon/.m2/repository/org/jvnet/staxex/com.springsource.org.jvnet.staxex/1.0.0/com.springsource.org.jvnet.staxex-1.0.0.jar
	osgi install --url  file:///Users/jccastrejon/.m2/repository/org/apache/commons/com.springsource.org.apache.commons.io/1.4.0/com.springsource.org.apache.commons.io-1.4.0.jar

	osgi install --url  file:///Users/jccastrejon/java/workspace_IMB/InstanceModelBus.Roo/net.tzolov.httprepresentations.roo.addon-1.1.2-SNAPSHOT.jar
	osgi install --url  file:///Users/jccastrejon/java/workspace_IMB/InstanceModelBus.Roo/net.tzolov.jaxb.roo.addon-1.1.2-SNAPSHOT.jar

2. To install add-on

	roo script -file install.roo


3. Update project
	
	perform eclipse


2. IMB command: imb update controllers

	- For each registered controller:
		- An aspect [Entity]_Controller_Roo_Imb.aj is created
		- This aspect modifies the create, update and delete methods to invoke the IMB before returning
		- The associated entities are processed by Jaxb Schemagen
		- The marshalling info is registered in Spring files