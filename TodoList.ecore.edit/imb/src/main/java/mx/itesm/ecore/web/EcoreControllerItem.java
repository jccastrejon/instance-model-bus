package mx.itesm.ecore.web;

import java.beans.Introspector;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Scanner;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import mx.itesm.ecore.domain.ImbNotificationType;
import mx.itesm.ecore.domain.ImbNotification;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder; 
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

@RequestMapping("/item" + "s")
@Controller
public class EcoreControllerItem {

	private static XMLOutputter out = new XMLOutputter();

	@Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private Marshaller marshaller;
    
    @Autowired
    private Unmarshaller unmarshaller;
    
    // http://localhost:8080/ecore/" + entityName + "s/addNotification/{url}/{entityId}/{entityName}/{notificationType}/{returnTypeClass}
    @RequestMapping(value = "/addNotification/{url}/{entityId}/{entityName}/{notificationType}/{returnTypeClass}", method = RequestMethod.PUT)
    public boolean update(@PathVariable("url") String url, @PathVariable("entityId") Long entityId, @PathVariable("entityName") String entityName,
							@PathVariable("notificationType") String notificationType, @PathVariable("returnTypeClass") String returnTypeClass, 
							@RequestBody imb.domain.todolist.itesm.mx.Item object) {
		ImbNotification notification;
		
		notification = new ImbNotification();
		notification.setUrl(url.replace("_", ":").replace("-", "/").replace("<", "{").replace(">", "}"));
		notification.setEntityId(entityId);
		notification.setEntityName(entityName);
		notification.setType(ImbNotificationType.valueOf(notificationType));
		notification.setReturnTypeClass(returnTypeClass);
					
		if(object != null) {
			try {
				notification.setEntity(this.marshal(object));
			} catch(Exception marshalException) {
				System.out.println("Unable to marshall entity for notification: " + marshalException.getMessage());
			}			
		}
		notification.persist();
		
		return true;
    }
    
    @RequestMapping(value = "/create/item", method = RequestMethod.PUT)
    public void create(@RequestBody imb.domain.todolist.itesm.mx.Item object) {
    	File xmlFile;
        Element instance;
        Document rootDocument;
        SAXBuilder saxBuilder;
        Object attributeValue;
        
        try {
        	//TODO: Fix file resolution!!:.
        	xmlFile = new File("/Users/jccastrejon/java/runtime-EclipseApplication/TodoList/My.todolist");
        	saxBuilder = new SAXBuilder();
        	rootDocument = saxBuilder.build(xmlFile);
        	
        	instance = new Element("Item");
        	for(Method method : object.getClass().getDeclaredMethods()) {
        		if(method.getName().startsWith("get")) {
        			attributeValue = method.invoke(object);
        			if(attributeValue != null) {
        				instance.setAttribute(Introspector.decapitalize(method.getName().replace("get", "")), attributeValue.toString());
        			}
        		}
        	}
        	
        	// Add instance
        	rootDocument.getRootElement().addContent(instance);
        	
        	// Save changes
    		out.output(rootDocument, new FileWriter(xmlFile));
        } catch(Exception e) {
        	System.out.println("Unable to create instance: " + object + " : " + e.getMessage());
        }
    }
    
    @RequestMapping(value = "/update/item/{id}", method = RequestMethod.POST)
    public boolean update(@PathVariable("id") Long id, @RequestBody imb.domain.todolist.itesm.mx.Item object) {
    	File localState;
    	Scanner scanner;
    	boolean beingEdited;
    	FileWriter remoteWriter;
    	Object unmarshalledObject;
    	StringBuilder localContent;
    	imb.domain.todolist.itesm.mx.Item localObject;
    	
    	try {
    		// Determine if this instance is currently being edited
    		beingEdited = false;
    		localState = this.getLocalState();
    		if(localState.exists()) {
    			unmarshalledObject = this.getObject(localState);
    			if((unmarshalledObject != null) && (imb.domain.todolist.itesm.mx.Item. class.isAssignableFrom(unmarshalledObject.getClass()))) {
    				localObject = (imb.domain.todolist.itesm.mx.Item) unmarshalledObject;
    				beingEdited = (localObject.getImbId().equals(id));
    			}
    		}
    		
    		// This instance is not being edited, can save changes without problems
    		if(!beingEdited) {
				//TODO: Fix file resolution!!:.
    			this.mergeWithLocal(id, object, new File("/Users/jccastrejon/java/runtime-EclipseApplication/TodoList/My.todolist"));	
    		}
    		
    		// This instance is being edited, save remote state to compare when
    		// the instance is saved locally or the user leaves the edition page
    		else {
    			remoteWriter = new FileWriter(this.getRemoteState());
    			remoteWriter.write(this.marshal(object));
				remoteWriter.flush();
				remoteWriter.close();
    		}
    	} catch(Exception e) {
    		System.out.println("Error while updating: " + e.getMessage());
    	}
    	return true;
    }
    
    @RequestMapping(value = "/delete/item/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") Long id) {
    	File xmlFile;
    	Element instance;
        XPath instancePath;
        Document rootDocument;
        SAXBuilder saxBuilder;
        
        try {
	        // TODO: Fix file resolution!!:.
    	    xmlFile = new File("/Users/jccastrejon/java/runtime-EclipseApplication/TodoList/My.todolist");
        	saxBuilder = new SAXBuilder();
	        rootDocument = saxBuilder.build(xmlFile);
    	    instancePath = XPath.newInstance("//*[@imbId='" + id + "']");
        	instance = (Element) instancePath.selectSingleNode(rootDocument);
        
	        // Check this behavior
    	    rootDocument.getRootElement().removeContent(instance);
    	    
    	    // Save changes
    		out.output(rootDocument, new FileWriter(xmlFile));
        } catch(Exception e) {
        	System.out.println("Unable to delete instance: " + id + " : " + e.getMessage());
        }
    }
    
    
    /**
    * Merge the changes made to an instance
    */
    private void mergeWithLocal(Long imbId, imb.domain.todolist.itesm.mx.Item object, final File xmlFile) throws Exception {
    	Element instance;
        XPath instancePath;
        Document rootDocument;
        SAXBuilder saxBuilder;
    	boolean valueSet;
    	Object localValue;
    	Object remoteValue;
    	Object currentValue;
    	String propertyName;
    	Object unmarshalledObject;
    	imb.domain.todolist.itesm.mx.Item localObject;
    	imb.domain.todolist.itesm.mx.Item remoteObject;
    	
    	// Locate the corresponding instance in the XML file
        saxBuilder = new SAXBuilder();
        rootDocument = saxBuilder.build(xmlFile);
        instancePath = XPath.newInstance("//*[@imbId='" + imbId + "']");
        instance = (Element) instancePath.selectSingleNode(rootDocument);
    	
    	// The instance should already exist, otherwise this wouldn't be a merge!
    	if (instance != null) {
    		localObject = null;
    		unmarshalledObject = this.getObject(this.getLocalState());
    		if(unmarshalledObject != null) {
    			if(imb.domain.todolist.itesm.mx.Item. class.isAssignableFrom(unmarshalledObject.getClass())) {
    				localObject = (imb.domain.todolist.itesm.mx.Item) unmarshalledObject;
    				if(!localObject.getImbId().equals(imbId)) {
    				    localObject = null;
    				}
    			}
    		}
    		 
	    	
	    	remoteObject = null;
    		unmarshalledObject = this.getObject(this.getRemoteState());
    		if(unmarshalledObject != null) {
    			if(imb.domain.todolist.itesm.mx.Item. class.isAssignableFrom(unmarshalledObject.getClass())) {
    				remoteObject = (imb.domain.todolist.itesm.mx.Item) unmarshalledObject;
    				if(!remoteObject.getImbId().equals(imbId)) {
    				    remoteObject = null;
    				}
    			}
    		}
    	
    		for(Method method : imb.domain.todolist.itesm.mx.Item. class.getDeclaredMethods()) {
    			if(method.getName().startsWith("get")) {
    				// Corresponding setter method
    				propertyName = Introspector.decapitalize(method.getName().replace("get", ""));
    				
    				// Get the different values for the current property
    				currentValue = method.invoke(object);
    				localValue = null;
    				remoteValue = null;
    				
    				if(currentValue != null) {
    				    currentValue = method.invoke(object);
    				}
    				
    				if(localObject != null) {
    					localValue = method.invoke(localObject);
    				}
    				
            	    if(remoteObject != null) {
            	    	remoteValue = method.invoke(remoteObject);
            	    }
            	    
            	    // Concurrency controls
            	    
            	    // Only one valid value
            	    valueSet = false;
            	    if((currentValue != null) && (localValue == null) && (remoteValue == null)) {
            	    	valueSet = true;
            	    	instance.setAttribute(propertyName, currentValue.toString());
            	    } else if((currentValue == null) && (localValue != null) && (remoteValue == null)) {
            	    	valueSet = true;
            	    	instance.setAttribute(propertyName, localValue.toString());
            	    } else if((currentValue == null) && (localValue == null) && (remoteValue != null)) {
            	    	valueSet = true;
            	    	instance.setAttribute(propertyName, remoteValue.toString());
            	    }
            	    
            	    if(!valueSet) {
    	        	    // For this purpose, null and "" are equivalent
	            	    if(currentValue == null) {
            	    		currentValue = "";
            		    } if(localValue == null) {
            		    	localValue = "";
        	    	    } if(remoteValue == null) {
    	        	    	remoteValue = "";
	            	    }
            	    
            			// If the currentValue is different from the local one, 
            		    // we save it, overriding any possible change in the remote state
        	    	    // (override remote value)
    	        	    else if(!currentValue.equals(localValue)) {
    	        	    	instance.setAttribute(propertyName, currentValue.toString());	
            	    	}
            	    
            		    // If both the current and local values are the same, and there's 
        	    	    // a remote value, save it, this property was updated by another tool
    	        	    // (override local value)
	            	    else if(currentValue.equals(localValue)) {
	            	    	instance.setAttribute(propertyName, remoteValue.toString());
            		    }
            	    
            		    // There were not changes in the property value
        	    	    else {
        	    	    	instance.setAttribute(propertyName, localValue.toString());
	            	    }
            	    }
	    		}
    		}
    		
    		// Save changes
    		out.output(rootDocument, new FileWriter(xmlFile));
    	}
    }
    
    public String marshal(Object imbType) throws Exception {
    	String returnValue;
    	ByteArrayOutputStream outputStream;
    	
    	outputStream = new ByteArrayOutputStream();
	    marshaller.marshal(imbType, new StreamResult(outputStream));
	    
	    returnValue = new String(outputStream.toByteArray(), "UTF-8");
	    return returnValue;
    }
    
    
    public Object getObject(File state) throws Exception {
        Scanner scanner;
        Object returnValue;
        StringBuilder localContent;
        
        returnValue = null;
        if(state.exists()) {
        	localContent = new StringBuilder();
    	    scanner = new Scanner(new FileInputStream(state), "UTF-8");
	        try {
            	while(scanner.hasNextLine()) {
        	        localContent.append(scanner.nextLine()).append("\n");
    	        }
	        } finally {
        	    scanner.close();
    	    }
    
	        returnValue = unmarshaller.unmarshal(new StreamSource(new ByteArrayInputStream(localContent.toString().getBytes("UTF-8"))));
        }
        
        return returnValue;
    }
    
    
    // TODO: Change File resolution:.
	public File getLocalState() {
		return new File(new File("/Users/jccastrejon/java"), "localState.xml");
	}
	
    // TODO: Change File resolution:.
	public File getRemoteState() {
		return new File(new File("/Users/jccastrejon/java"), "remoteState.xml");
	}
}