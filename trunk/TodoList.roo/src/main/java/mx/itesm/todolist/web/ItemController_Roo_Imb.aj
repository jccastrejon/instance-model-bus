package mx.itesm.todolist.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import mx.itesm.todolist.domain.Item;
import mx.itesm.todolist.domain. LocalEntity;
import mx.itesm.todolist.domain. RemoteEntity;
import mx.itesm.todolist.domain. ImbNotification;
import mx.itesm.todolist.domain. ImbNotificationType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

@Configurable
public aspect ItemController_Roo_Imb {

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private Marshaller marshaller;
    
    @Autowired
    private Marshaller ItemController. marshaller;

    @Autowired
    private Unmarshaller unmarshaller;
    
    @Autowired
    private Unmarshaller ItemController. unmarshaller;
    
    
    // Imb Id
    private Long Item. imbId;
    
    public Long Item. getImbId() {
    	return this.imbId;
    }
    
    public void Item. setImbId(final Long imbId) {
    	this.imbId = imbId;
    }
    
    public static Item Item. findByImbId(Long imbId) {
        TypedQuery<Item> query;
        EntityManager entityManager;        
        
        entityManager = Item.entityManager();
        query = entityManager.createQuery("SELECT Item FROM Item AS item WHERE item. imbId = :imbId", Item. class);
        query.setParameter("imbId", imbId);
        return query.getSingleResult();
    }
    
    // Keep the original state of an instance before it is edited
    after(Long id, Model model) returning :
    	execution (* (mx.itesm.todolist.web.ItemController).updateForm(..)) && args(id, model) {
    	Item object;
    	boolean remoteMerged;
    	LocalEntity localEntity;
    	
    	try {
			// We only keep a local/remote copy of any type at any given time 
    		ItemController_Roo_Imb. removeLocalAndRemoteEntities();
    	
    		// Save new local copy
    		object = Item. findItem(id);
    		localEntity = new LocalEntity();
    		localEntity.setEntityName("Item");
    		localEntity.setEntityId(id);
    		localEntity.setImbId(object.getImbId());
	    	localEntity.setValidity(new Date());
    		localEntity.setEntity(new String(object.marshal(marshaller).toByteArray(), "UTF-8"));
    		localEntity.persist();
    	} catch(Exception e) {
    		System.out.println("Unable to register local copy of Item: " + e.getMessage());
    	}
    }
    
    // Discard original states if the user goes to another page besides edition
    after() returning :
    	execution (* (mx.itesm.todolist.web.ItemController).createForm(..)) {
    	// If there's a remote entity, that means we were editing an instance but didn't save the changes, 
    	// before continuing we need to save the pending remote state of the instance
    	for(RemoteEntity remote : RemoteEntity.findAllRemoteEntitys()) {
    		// The current state is the one that was last persisted
    		try {
    			ItemController_Roo_Imb. mergeWithLocal(Item. findByImbId(remote.getImbId()), unmarshaller);
    		} catch(Exception e) {
    			System.out.println("Unable to merge remote instance: " + remote + ": " + e.getMessage());
    		}
    	}
    	
    	ItemController_Roo_Imb. removeLocalAndRemoteEntities();
    }
    
    after() returning :
    	execution (* (mx.itesm.todolist.web.ItemController).list(..)) {
    	// If there's a remote entity, that means we were editing an instance but didn't save the changes, 
    	// before continuing we need to save the pending remote state of the instance
    	for(RemoteEntity remote : RemoteEntity.findAllRemoteEntitys()) {
    		// The current state is the one that was last persisted
    		try {
    			ItemController_Roo_Imb. mergeWithLocal(Item. findByImbId(remote.getImbId()), unmarshaller);
    		} catch(Exception e) {
    			System.out.println("Unable to merge remote instance: " + remote + ": " + e.getMessage());
    		}
    	}
    	
    	ItemController_Roo_Imb. removeLocalAndRemoteEntities();
    }
    

	// Communication from the Application to the Imb bus
	
	after(Item object, BindingResult result, Model model, HttpServletRequest request) returning : 
		execution (* (mx.itesm.todolist.web.ItemController).create(..)) && args(object, result, model, request) {
		try {
			// Set IMB Id before communicating with the IMB bus
			object.setImbId(System.currentTimeMillis() + new Random().nextInt());
			object.merge();
			
			new Thread(new NotificationThread(ImbNotificationType.CreateEntity, 
												"http://localhost:9090/todolistbus-0.1.0.BUILD-SNAPSHOT" + "/imb/create/{object}/Spring",
												ItemController_Roo_Imb. transformToImbItem (object),
												"item",
												object,
												marshaller)).start();
		} catch(Exception e) {
			System.out.println("Unable to create entity: " + e.getMessage());
		}
	}
	
	after(Item object, BindingResult result, Model model, HttpServletRequest request) returning : 
		execution (* (mx.itesm.todolist.web.ItemController).update(..)) && args(object, result, model, request) {
		LocalEntity localEntity;
		Item persistentObject;
		
		try {
			persistentObject = null;
			try {
				// Restore IMB Id, since it wasn't send in the Spring Roo editing page
				localEntity = ItemController_Roo_Imb. getLocalEntity(object.getId());
				if(localEntity != null) {
					persistentObject = Item. findItem (object.getId());
					persistentObject.setImbId(localEntity.getImbId());
					persistentObject.merge();
				}
				ItemController_Roo_Imb. mergeWithLocal(persistentObject, unmarshaller);
			} catch(Exception e) {
    			System.out.println("Unable to merge instance: " + object + ": " + e.getMessage());
    		}
    		
    		ItemController_Roo_Imb. removeLocalAndRemoteEntities();
    		new Thread(new NotificationThread(ImbNotificationType.UpdateEntity, 
    											"http://localhost:9090/todolistbus-0.1.0.BUILD-SNAPSHOT" + "/imb/update/{object}/Spring/{id}",
    											ItemController_Roo_Imb. transformToImbItem (persistentObject),
    											Boolean.class, 
    											"item", 
    											persistentObject.getImbId(),
    											persistentObject,
    											marshaller)).start();
		} catch(Exception e) {
			System.out.println("Unable to update entity: " + e.getMessage());
		}
	}
	
	before(Long id, Integer page, Integer size, Model model) : 
		execution (* (mx.itesm.todolist.web.ItemController).delete(..)) && args(id, page, size, model) {
		try {
		
			new Thread(new NotificationThread(ImbNotificationType.DeleteEntity, 
												"http://localhost:9090/todolistbus-0.1.0.BUILD-SNAPSHOT" + "/imb/delete/{object}/Spring/{id}", 
												"item", 
												Item. findItem (id).getImbId())).start();
		} catch(Exception e) {
			System.out.println("Unable to delete entity: " + e.getMessage());
		}
	}
	
	// Communication from the Imb bus to the Application
	
    @RequestMapping(value = "/create/item", method = RequestMethod.PUT)
    public void ItemController.imbCreate(@RequestBody imb.domain.todolist.itesm.mx.Item object) {
        Item modelObject;
        
        modelObject = ItemController_Roo_Imb. transformFromImbItem (object);
        modelObject.persist();
    }
    
    @RequestMapping(value = "/update/item/{id}", method = RequestMethod.POST)
    public boolean ItemController.imbUpdate(@PathVariable("id") Long id, @RequestBody imb.domain.todolist.itesm.mx.Item object) {
        Item remoteObject;
        LocalEntity localEntity;
        RemoteEntity remoteEntity;
        
        // This instance is not being edited, can save changes without problems
        remoteObject = ItemController_Roo_Imb. transformFromImbItem (object);
        if(!ItemController_Roo_Imb. isEditing(id)) {
        	try {
        		ItemController_Roo_Imb. mergeWithLocal(remoteObject, unmarshaller);
        	} catch(Exception e) {
    			System.out.println("Unable to merge remote instance: " + remoteObject + ": " + e.getMessage());
    		}
        }
        
        // This instance is being edited, save remote state to compare when
        // the instance is saved locally or the user leaves the edition page
        else {
        	try {
        		remoteEntity = new RemoteEntity();
	        	remoteEntity.setEntityName("Item");
	        	remoteEntity.setEntityId(Item. findByImbId(id).getId());
    			remoteEntity.setImbId(remoteObject.getImbId());
	    		remoteEntity.setValidity(new Date());
    			remoteEntity.setEntity(new String(remoteObject.marshal(marshaller).toByteArray(), "UTF-8"));
	    		remoteEntity.persist();
        	} catch(Exception e) {
    			System.out.println("Unable to register remote copy of Item: " + e.getMessage());
    		}
        }
        
        return true;
    }
    
    @RequestMapping(value = "/delete/item/{id}", method = RequestMethod.DELETE)
    public void ItemController.imbDelete(@PathVariable("id") Long id) {
        // This instance is not being edited, can save changes without problems
        if(!ItemController_Roo_Imb. isEditing(id)) {
        	Item. findByImbId(id).remove();	
        } else {
        	//TODO: Check this behavior
        	// We delete the instance anyway
        	Item. findByImbId(id).remove();
        }
    }
    
    /**
    * Helper method to transform from an IMB Bus object
    * to a Spring Roo instance.
    */
    public static Item transformFromImbItem(imb.domain.todolist.itesm.mx.Item object) {
    	return (Item)ItemController_Roo_Imb. transformObjects(object, Item. class);
    }
	
	/**
	* Helper method to transform an Spring Roo object to an instance 
	* that can be used for communications with the IMB Bus.
	*/
	public static imb.domain.todolist.itesm.mx.Item transformToImbItem(Item object) {
        return (imb.domain.todolist.itesm.mx.Item)ItemController_Roo_Imb. transformObjects(object, imb.domain.todolist.itesm.mx.Item. class);
    }
    
    /**
    * Transform between object representations
    */
    private static Object transformObjects(final Object fromObject, final Class<?> toClass) {
    	Object enumValue;
    	Object returnValue;
    	Method enumValueOf;
    	Method returnValueSetMethod;
        Object objectMethodReturnValue;
    	
        try {
            returnValue = toClass.newInstance();
            for (Method method : fromObject.getClass().getDeclaredMethods()) {
                if (method.getName().startsWith("get")) {
                    returnValueSetMethod = null;
                    objectMethodReturnValue = method.invoke(fromObject, (Object[]) new Class<?>[0]);
                    for (Method returnValueMethod : toClass.getDeclaredMethods()) {
                        if (returnValueMethod.getName().equals(method.getName().replace("get", "set"))) {
                            returnValueSetMethod = returnValueMethod;
                            break;
                        }
                    }

                    if ((returnValueSetMethod != null) && (objectMethodReturnValue != null)) {
                        // Enum
                        if (method.getReturnType().isEnum()) {
                        	enumValue = null;
                        	enumValueOf = returnValueSetMethod.getParameterTypes()[0].getDeclaredMethod("values");
                        	for(Object value : (Object[])enumValueOf.invoke(null)) {
                        		if(value.toString().toUpperCase().equals(objectMethodReturnValue.toString().toUpperCase())) {
                        			enumValue = value;
                        			break;
                        		}
                        	}
                        	
                            returnValueSetMethod.invoke(returnValue, enumValue);
                        }

                        // Date
                        else if (Date.class.isAssignableFrom(method.getReturnType())) {
                            GregorianCalendar calendar = new GregorianCalendar();
                            calendar.setTime((Date) objectMethodReturnValue);
                            returnValueSetMethod.invoke(returnValue, DatatypeFactory.newInstance()
                                    .newXMLGregorianCalendar(calendar));
                        }
                        
                        // Calendar
                        else if(XMLGregorianCalendar.class.isAssignableFrom(method.getReturnType())) {
                        	returnValueSetMethod.invoke(returnValue, ((XMLGregorianCalendar)objectMethodReturnValue).toGregorianCalendar().getTime());
                        }

                        // Simple types
                        else {
                            returnValueSetMethod.invoke(returnValue, objectMethodReturnValue);
                        }
                    }
                }
            }
        } catch (Exception e) {
        	System.out.println("Unable to transform from: " + fromObject + ", to: " + toClass + ": " + e.getMessage());
            returnValue = null;
        }
        
        return returnValue;
    }
    
    /**
    * Merge the changes made to an instance
    */
    private static void mergeWithLocal(final Item currentObject, final Unmarshaller unmarshaller) throws Exception {
    	boolean valueSet;
    	Object localValue;
    	Object remoteValue;
    	Object currentValue;
    	Method setMethod;
    	Item localObject;
    	Item remoteObject;
    	Item persistentObject;
    	List<LocalEntity> localEntities;
    	List<RemoteEntity> remoteEntities;
    	
    	// The instance should already exist, otherwise this wouldn't be a merge!
    	persistentObject = Item. findByImbId(currentObject.getImbId());
    	if(persistentObject != null) {
	    	localObject = null;
    		localEntities = LocalEntity.findLocalEntitysByImbId(currentObject.getImbId()).getResultList();
    		if((localEntities != null) && (!localEntities.isEmpty())) {
				for(LocalEntity local : localEntities) {
    				if(local.getImbId().equals(currentObject.getImbId())) {
	    				localObject = ItemController_Roo_Imb. transformFromImbItem (
	    								(imb.domain.todolist.itesm.mx.Item) unmarshaller.unmarshal(new StreamSource(new ByteArrayInputStream(local.getEntity().getBytes("UTF-8")))));
    					break;
	    			}
    			}
    		}
    	

	    	remoteObject = null;
    		remoteEntities = RemoteEntity.findRemoteEntitysByImbId(currentObject.getImbId()).getResultList();
    		if((remoteEntities != null) && (!remoteEntities.isEmpty())) {
				for(RemoteEntity remote : remoteEntities) {
    				if(remote.getImbId().equals(currentObject.getImbId())) {
    					remoteObject = ItemController_Roo_Imb. transformFromImbItem (
	    								(imb.domain.todolist.itesm.mx.Item) unmarshaller.unmarshal(new StreamSource(new ByteArrayInputStream(remote.getEntity().getBytes("UTF-8")))));
	    				break;
	    			}
    			}
    		}
    	
    		for(Method method : Item. class.getDeclaredMethods()) {
    			if(method.getName().startsWith("get")) {
    				// Corresponding setter method
    				setMethod = Item. class.getDeclaredMethod("set" + method.getName().replaceFirst("get", ""), method.getReturnType());
    				
    				// Get the different values for the current property
    				currentValue = method.invoke(currentObject);
    				localValue = null;
    				remoteValue = null;
    				
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
            	    	setMethod.invoke(persistentObject, currentValue);
            	    } else if((currentValue == null) && (localValue != null) && (remoteValue == null)) {
            	    	valueSet = true;
            	    	setMethod.invoke(persistentObject, localValue);
            	    } else if((currentValue == null) && (localValue == null) && (remoteValue != null)) {
            	    	valueSet = true;
            	    	setMethod.invoke(persistentObject, remoteValue);
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
	            	    	setMethod.invoke(persistentObject, currentValue);	
            	    	}
            	    
            		    // If both the current and local values are the same, and there's 
        	    	    // a remote value, save it, this property was updated by another tool
    	        	    // (override local value)
	            	    else if(currentValue.equals(localValue)) {
            	    		setMethod.invoke(persistentObject, remoteValue);
            		    }
            	    
            		    // There were not changes in the property value
        	    	    else {
    	        	    	setMethod.invoke(persistentObject, localValue);
	            	    }
            	    }
	    		}
    		}
    		
    		// Save changes
    		persistentObject.merge();
    	}    	
    }
    
    /**
    * Marshall the specified Item into an output stream.  
    */
    public ByteArrayOutputStream Item. marshal(final Marshaller marshaller) {
    	Object imbType;
    	ByteArrayOutputStream outputStream;
    	
    	try {
    		imbType = ItemController_Roo_Imb. transformToImbItem (this);
    		outputStream = new ByteArrayOutputStream();
	    	marshaller.marshal(imbType, new StreamResult(outputStream));
	    } catch(Exception e) {
    		System.out.println("Unable to marshal Item: " + e.getMessage());
    		outputStream = null;
    	}
    	
    	return outputStream;
    }
    
    /**
    * Remove all local states
    */
    private static void removeLocalAndRemoteEntities() {
    	for(LocalEntity localEntity : LocalEntity.findAllLocalEntitys()) {
    		localEntity.remove();
    	}
    	
    	for(RemoteEntity remoteEntity : RemoteEntity.findAllRemoteEntitys()) {
    		remoteEntity.remove();
    	}
    }
    
    /**
    * Check whether a particular instance is currently being edited
    */
    private static boolean isEditing(final Long imbId) {
    	boolean returnValue;
    	List<LocalEntity> localEntities;
    	
    	// Check if there's any local entity with the specified imbId
    	returnValue = false;
    	localEntities = LocalEntity.findLocalEntitysByImbId(imbId).getResultList();
        if((localEntities == null) || (localEntities.isEmpty())) {
        	returnValue = false;
        } else {
        	for(LocalEntity local : localEntities) {
        		if(local.getImbId().equals(imbId)) {
        			returnValue = true;
        			break;
        		}
        	}
        }
        
        return returnValue;
    }
    
    /**
    * Get the local entity of type: Item with the specified Id
    */
    private static LocalEntity getLocalEntity(final Long entityId) {
    	LocalEntity returnValue; 
    	List<LocalEntity> localEntities;
    	
    	returnValue = null;
    	localEntities = LocalEntity.findLocalEntitysByEntityId(entityId).getResultList();
    	if(localEntities != null) {
    		for(LocalEntity local : localEntities) {
    			if(local.getEntityName().equals("Item")) {
    				returnValue = local;
    				break;
    			}
    		}
    	}
    	
    	return returnValue;
    } 
    
    class NotificationThread implements Runnable {
    	private String url;
    	private Item object;
		private Long entityId;
		private String entityName;
		private boolean failedDelivery;
		private String returnTypeClass;
		private Marshaller marshaller;
		private ImbNotification notification;
		private ImbNotificationType notificationType;
		private imb.domain.todolist.itesm.mx.Item unmarshalledObject;
		
		public NotificationThread(final ImbNotificationType notificationType, final String url, final String entityName, final Long entityId) {
			this.url = url;
			this.entityId = entityId;
			this.entityName = entityName;
			this.notificationType = notificationType;
		}
		
		public NotificationThread(final ImbNotificationType notificationType, final String url, final imb.domain.todolist.itesm.mx.Item unmarshalledObject, 
									final Class<?> returnTypeClass, final String entityName, final Long entityId, Item object, Marshaller marshaller) {
			this.url = url;
			this.object = object;
			this.entityId = entityId;
			this.entityName = entityName;
			this.marshaller = marshaller;
			this.notificationType = notificationType;
			this.returnTypeClass = returnTypeClass.getName();
			this.unmarshalledObject = unmarshalledObject;
		}
		
		public NotificationThread(final ImbNotificationType notificationType, final String url, 
									final imb.domain.todolist.itesm.mx.Item unmarshalledObject, final String entityName, Item object, Marshaller marshaller) {
			this.url = url;
			this.object = object;
			this.entityName = entityName;
			this.marshaller = marshaller;
			this.notificationType = notificationType;
			this.unmarshalledObject = unmarshalledObject;
		}
		
    	@Override
		public void run() {
			try {
				switch(this.notificationType) {
					case CreateEntity:
						restTemplate.put(url, unmarshalledObject, entityName);
						break;
				
					case UpdateEntity:
						restTemplate.postForObject(url, unmarshalledObject, Class.forName(returnTypeClass), entityName, entityId);
						break;
					
					case DeleteEntity:
						restTemplate.delete(url, entityName, entityId);
						break;
				}
			} catch(Exception e) {
				// If failed to deliver, register for notification scheduling
				failedDelivery = false;
				if(IOException.class.isAssignableFrom(e.getClass())) {
					failedDelivery = true;
				} else if((e.getCause() != null) && (IOException.class.isAssignableFrom(e.getCause().getClass()))) {
					failedDelivery = true;
				}
				
				if(failedDelivery) {
					notification = new ImbNotification();
					notification.setUrl(url);
					notification.setEntityId(entityId);
					notification.setEntityName(entityName);
					notification.setType(notificationType);
					notification.setReturnTypeClass(returnTypeClass);
					
					if((object != null) && (marshaller != null)) {
						try {
							notification.setEntity(new String(object.marshal(marshaller).toByteArray(), "UTF-8"));
						} catch(Exception marshalException) {
							System.out.println("Unable to marshall entity for notification: " + marshalException.getMessage());
						}
					}
					
					notification.persist();
				}
			}
		}
    }
}