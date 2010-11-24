
package todolist.presentation;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.beans.Introspector;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.transform.stream.StreamSource;

import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.jdom.input.SAXBuilder; 
import org.jdom.output.XMLOutputter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;

public aspect SelectionAspect {
	
	private static Marshaller marshaller;
	private static Unmarshaller unmarshaller;
	private static XMLOutputter out = new XMLOutputter();

	static {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
        marshaller = applicationContext.getBean("jaxbMarshaller", Marshaller.class);
        unmarshaller = applicationContext.getBean("jaxbMarshaller", Unmarshaller.class);
    }

	after(SelectionChangedEvent event) returning: execution (* (todolist.presentation.TodolistActionBarContributor).selectionChanged(..)) && args(event) {
		File localState;
		String xmlContent;
		boolean typeSaved;
		Class<?> typeClass;
		String typesPackage;
		FileWriter localWriter;
		Object selectedElement;
		Object unmarshalledObject;
		List<String> validTypes;
		Method marshallMethod;
		Method idMethod;

		validTypes = new ArrayList<String>();
		typesPackage = "todolist";
		validTypes.add("Item");

		if(org.eclipse.jface.viewers.TreeSelection.class.isAssignableFrom(event.getSelection().getClass())) {
			localState = this.getLocalState();
			selectedElement = ((org.eclipse.jface.viewers.TreeSelection)event.getSelection()).getFirstElement();
		
			// Save local state
			typeSaved = false;
			for(String type : validTypes) {
				try {
					typeClass = Class.forName(typesPackage + "." + type);
					if(typeClass.isAssignableFrom(selectedElement.getClass())) {
						marshallMethod = Class.forName("todolist.provider" + ".EcoreAspect_" + type + "ItemProvider")
								.getDeclaredMethod("marshal", typeClass);
						xmlContent = (String) marshallMethod.invoke(null, selectedElement);
						localWriter = new FileWriter(localState);
						
						localWriter.write(xmlContent);
						localWriter.flush();
						localWriter.close();
						
						typeSaved = true;
						break;
					}
				} catch(Exception e) {
					// No-op
				}
			}
			
			// Discard original states if the user goes to another page besides edition
			if(!typeSaved) {
				try {
					// If there's a remote entity, that means we were editing an instance but didn't save the changes, 
    				// before continuing we need to save the pending remote state of the instance
					unmarshalledObject = this.getObject(this.getRemoteState());
					if(unmarshalledObject != null) {
						idMethod = unmarshalledObject.getClass().getDeclaredMethod("getImbId");
						
						//TODO: Fix file resolution!!:.
						this.mergeWithLocal((Long)idMethod.invoke(unmarshalledObject), unmarshalledObject, new File("/Users/jccastrejon/java/runtime-EclipseApplication/TodoList/My.todolist"));
					}
					
					localState.delete();
					this.getRemoteState().delete();
				} catch(Exception e) {
					// No-op
				}
			}
		}
	}
	
    /**
    * Merge the changes made to an instance
    */
    private void mergeWithLocal(Long imbId, Object object, final File xmlFile) throws Exception {
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
    	Object localObject;
    	Object remoteObject;
    	Method imbIdMethod;
    	
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
    			if(object.getClass().isAssignableFrom(unmarshalledObject.getClass())) {
    				imbIdMethod = unmarshalledObject.getClass().getDeclaredMethod("getImbId");
    				if(imbIdMethod.invoke(unmarshalledObject).equals(imbId)) {
    				    localObject = unmarshalledObject;
    				}
    			}
    		}
    		 
	    	
	    	remoteObject = null;
    		unmarshalledObject = this.getObject(this.getRemoteState());
    		if(unmarshalledObject != null) {
    			if(object.getClass().isAssignableFrom(unmarshalledObject.getClass())) {
    				imbIdMethod = unmarshalledObject.getClass().getDeclaredMethod("getImbId");
    				if(imbIdMethod.invoke(unmarshalledObject).equals(imbId)) {
    				    remoteObject = unmarshalledObject;
    				}
    			}
    		}
    	
    		for(Method method : object.getClass().getDeclaredMethods()) {
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