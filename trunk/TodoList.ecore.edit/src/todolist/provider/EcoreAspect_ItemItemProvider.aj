
package todolist.provider;

import java.lang.reflect.Method;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.web.client.RestTemplate;

import todolist.Item;

public aspect EcoreAspect_ItemItemProvider {
    private static RestTemplate restTemplate;
    private static Marshaller marshaller;
    private static Unmarshaller unmarshaller;

    static {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
        restTemplate = applicationContext.getBean("restTemplate", RestTemplate.class);
        marshaller = applicationContext.getBean("jaxbMarshaller", Marshaller.class);
        unmarshaller = applicationContext.getBean("jaxbMarshaller", Unmarshaller.class);
    }

	after(Notification notification) returning: execution (* (todolist.provider.ItemItemProvider).notifyChanged(..)) && args(notification) {
		Object imbType;
		Object oldValue;
		Object newValue;
		Object notifier;
		Method idMethod;
		String referenceName;
		String containingClass;
		EReference changeReference;
		String[] referenceNameParts;
		
		notifier = notification.getNotifier();
		oldValue = notification.getOldValue();
		newValue = notification.getNewValue();
		switch(notification.getEventType()) {
			case Notification.ADD:
				if(EReference.class.isAssignableFrom(notification.getFeature().getClass())) {
					try {
						changeReference = ((EReference) notification.getFeature());
						referenceName = changeReference.getName();
						// Set IMB Id
						idMethod = Class.forName(changeReference.getEReferenceType().getInstanceClassName())
											.getMethod("setImbId", new Class[]{Long.class});
						idMethod.invoke(newValue, System.currentTimeMillis() + new Random().nextInt());
						
						// Convert to the appropriate Imb type
						imbType = EcoreAspect_ItemItemProvider. getImbType(referenceName, newValue);
						
						// Send to the Imb
						new Thread(new NotificationThread("CreateEntity", 
															"http://localhost:9090/todolistbus-0.1.0.BUILD-SNAPSHOT" + "/imb/create/{object}/Eclipse",
															imbType, 
															referenceName.toLowerCase())).start();
					} catch(Exception e) {
						System.out.println("Unable to communicate with the bus, will try again later");
					}
				}
			break;

			case Notification.SET:
				try {
					containingClass = ((EStructuralFeature)notification.getFeature()).getEContainingClass().getName();
				
					// Convert to appropriate Imb type
					imbType = EcoreAspect_ItemItemProvider. getImbType(containingClass, notifier);
				
					// Clear states
					this.getLocalState().delete();
					this.getRemoteState().delete();
				
					// Send to the Imb
					idMethod = notifier.getClass().getMethod("getImbId");
					new Thread(new NotificationThread("UpdateEntity",
														"http://localhost:9090/todolistbus-0.1.0.BUILD-SNAPSHOT" + "/imb/update/{object}/Eclipse/{id}",
														imbType, 
														Boolean.class,
														containingClass.toLowerCase(), 
														Long.parseLong(idMethod.invoke(notifier).toString()))).start();
				} catch(Exception e) {
					System.out.println("Unable to communicate with the bus, will try again later");
				}
			break;
				
			case Notification.REMOVE:
				try {
					referenceName = oldValue.getClass().getName();
					referenceName = referenceName.replace("Impl", "");
					referenceNameParts = referenceName.split("\\.");
					referenceName = referenceNameParts[referenceNameParts.length-1].toLowerCase();
					idMethod = oldValue.getClass().getMethod("getImbId");
					new Thread(new NotificationThread("DeleteEntity",
														"http://localhost:9090/todolistbus-0.1.0.BUILD-SNAPSHOT" + "/imb/delete/{object}/Eclipse/{id}",
														referenceName, 
														Long.parseLong(idMethod.invoke(oldValue).toString()))).start();
				} catch(Exception e) {
					System.out.println("Unable to communicate with the bus, will try again later");
				}
			break;
		}
	}
	
	/**
	* Convert from an Ecore to an Imb type.
	*/
	private static Object getImbType(final String typeName, final Object typeInstance) throws Exception {
		Object returnValue;
		Method transformationMethod;
		
		transformationMethod = Class.forName("todolist.provider" + ".EcoreAspect_" + typeName + "ItemProvider")
									.getMethod("convertToImb" + typeName, new Class[]{Class.forName("todolist" + "." + typeName)});
		returnValue = transformationMethod.invoke(null, typeInstance);
		
		return returnValue;
	}
	
	
				
	/**
	* Helper method to convert an object to an instance 
	* that can be used for communications with the IMB Bus.
	*/
	public static imb.domain.todolist.itesm.mx.Item convertToImbItem(Item object) {
        Method returnValueSetMethod;
        Object objectMethodReturnValue;
        imb.domain.todolist.itesm.mx.Item returnValue;

        try {
            returnValue = new imb.domain.todolist.itesm.mx.Item();

            for (Method method : object.getClass().getDeclaredMethods()) {
                if (method.getName().startsWith("get")) {
                    returnValueSetMethod = null;
                    objectMethodReturnValue = method.invoke(object, (Object[]) new Class<?>[0]);
                    for (Method returnValueMethod : imb.domain.todolist.itesm.mx.Item. class.getDeclaredMethods()) {
                        if (returnValueMethod.getName().equals(method.getName().replace("get", "set"))) {
                            returnValueSetMethod = returnValueMethod;
                            break;
                        }
                    }

                    if ((returnValueSetMethod != null) && (objectMethodReturnValue != null)) {
                        // Enum
                        if (method.getReturnType().isEnum()) {
                            returnValueSetMethod.invoke(
                                    returnValue,
                                    returnValueSetMethod.getParameterTypes()[0].getDeclaredMethod("valueOf",
                                            String.class)
                                            .invoke(null, objectMethodReturnValue.toString().toUpperCase()));
                        }

                        // Date
                        else if (Date.class.isAssignableFrom(method.getReturnType())) {
                            GregorianCalendar calendar = new GregorianCalendar();
                            calendar.setTime((Date) objectMethodReturnValue);
                            returnValueSetMethod.invoke(returnValue, DatatypeFactory.newInstance()
                                    .newXMLGregorianCalendar(calendar));
                        }

                        // Simple types
                        else {
                            returnValueSetMethod.invoke(returnValue, objectMethodReturnValue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            }
            e.printStackTrace();
            returnValue = null;
        }

        return returnValue;
    }
    
    public static String marshal(Item object) throws Exception {
    	String returnValue;
    	imb.domain.todolist.itesm.mx.Item imbType;
    	ByteArrayOutputStream outputStream;
    	
    	imbType = convertToImbItem(object);
    	outputStream = new ByteArrayOutputStream();
	    marshaller.marshal(imbType, new StreamResult(outputStream));
	    
	    returnValue = new String(outputStream.toByteArray(), "UTF-8");
	    return returnValue;
    }
    
    public static Item unmarshal() {
    	return null;
    }
    
    		
	    
    class NotificationThread implements Runnable {
    	private String url;
		private Long entityId;
		private String entityName;
		private boolean failedDelivery;
		private String returnTypeClass;
		private String notificationType;
		private Object unmarshalledObject;
		
		public NotificationThread(final String notificationType, final String url, final String entityName, final Long entityId) {
			this.url = url;
			this.entityId = entityId;
			this.entityName = entityName;
			this.notificationType = notificationType;
		}
		
		public NotificationThread(final String notificationType, final String url, final Object unmarshalledObject, 
									final Class<?> returnTypeClass, final String entityName, final Long entityId) {
			this.url = url;
			this.entityId = entityId;
			this.entityName = entityName;
			this.returnTypeClass = returnTypeClass.getName();
			this.notificationType = notificationType;
			this.unmarshalledObject = unmarshalledObject;
		}
		
		public NotificationThread(final String notificationType, final String url, 
									final Object unmarshalledObject, final String entityName) {
			this.url = url;
			this.entityName = entityName;
			this.notificationType = notificationType;
			this.unmarshalledObject = unmarshalledObject;
		}
		
    	@Override
		public void run() {
			try {
				if(notificationType.equals("CreateEntity")) {
					restTemplate.put(url, unmarshalledObject, entityName);
				} else if(notificationType.equals("UpdateEntity")) {
					restTemplate.postForObject(url, unmarshalledObject, Class.forName(returnTypeClass), entityName, entityId);
				} else if(notificationType.equals("DeleteEntity")) {
					restTemplate.delete(url, entityName, entityId);
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
					//TODO: Change for property value
					restTemplate.put("http://localhost:8080/ecore/" + entityName + "s/addNotification/{url}/{entityId}/{entityName}/{notificationType}/{returnTypeClass}", 
										unmarshalledObject, this.getValidUrl(url), this.getValidValue(entityId), this.getValidValue(entityName), 
										this.getValidValue(notificationType), this.getValidValue(returnTypeClass));
				}
			}
		}
		
		private String getValidUrl(final String url) {
			String returnValue;
			
			returnValue = url;
			if(url != null) {
				returnValue = returnValue.replace(":", "_").replace("/", "-").replace("{", "<").replace("}", ">");
			} else {
				returnValue = "null";
			}
			
			return returnValue;
		}
		
		private String getValidValue(final String currentValue) {
			String returnValue;
			
			returnValue = currentValue;
			if((currentValue == null) || ((currentValue != null) && (currentValue.trim().equals("")))) {
				returnValue = "null";
			}
			
			return returnValue;
		}
		
		private Long getValidValue(final Long currentValue) {
            Long returnValue;
            
            returnValue = currentValue;
            if(currentValue == null) {
                returnValue = 0L;
            }
            
            return returnValue;
        }
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