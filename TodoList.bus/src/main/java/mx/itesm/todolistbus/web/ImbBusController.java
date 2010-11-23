package mx.itesm.todolistbus.web;

import mx.itesm.todolistbus.domain. Tool;
import mx.itesm.todolistbus.domain. ImbNotification;
import mx.itesm.todolistbus.domain. ImbNotificationType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

@RequestMapping("/imb")
@Controller
public class ImbBusController {

	@Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private Marshaller marshaller;
    
        	
    @RequestMapping(value = "/create/item/{toolName}", method = RequestMethod.PUT)
    public void createItem(@PathVariable("toolName") String toolName, @RequestBody imb.domain.todolist.itesm.mx.Item object, HttpServletRequest request) {
    	// Send notifications to the appropriate tools
        for(Tool tool : Tool.findAllTools()) {
        	try {
        		if(this.notify(toolName, tool, request)) {
        			new Thread(new NotificationThread(ImbNotificationType.CreateEntity, 
														"http://" + tool.getIpAddress() + ":" +  tool.getPort() + "/" + tool.getContextPath() + "/items/create/{object}",
														object,
														"item", marshaller)).start();
				}
			} catch(Exception e) {
				System.out.println("Unable to send create notification to tool: " + e.getMessage());
			}
        }
    }
    
    @RequestMapping(value = "/update/item/{toolName}/{id}", method = RequestMethod.POST)
    public boolean updateItem(@PathVariable("id") Long id, @PathVariable("toolName") String toolName, 
    							@RequestBody imb.domain.todolist.itesm.mx.Item object, HttpServletRequest request) {
    	// Send notifications to the appropriate tools
        for(Tool tool : Tool.findAllTools()) {
        	try {
        		if(this.notify(toolName, tool, request)) {
        			new Thread(new NotificationThread(ImbNotificationType.UpdateEntity, 
    													"http://" + tool.getIpAddress() + ":" +  tool.getPort() + "/" + tool.getContextPath() + "/items/update/{object}/{id}",
    													object,
    													Boolean.class, 
    													"item", 
    													object.getImbId(), marshaller)).start();
				}
			} catch(Exception e) {
				System.out.println("Unable to send update notification to tool: " + e.getMessage());
			}
        }
        
        return true;
    }
    
    @RequestMapping(value = "/delete/item/{toolName}/{id}", method = RequestMethod.DELETE)
    public void deleteItem(@PathVariable("id") Long id, @PathVariable("toolName") String toolName, HttpServletRequest request) {
    	// Send notifications to the appropriate tools
        for(Tool tool : Tool.findAllTools()) {
        	try {
        		if(this.notify(toolName, tool, request)) {
        			new Thread(new NotificationThread(ImbNotificationType.DeleteEntity, 
														"http://" + tool.getIpAddress() + ":" +  tool.getPort() + "/" + tool.getContextPath() + "/items/delete/{object}/{id}", 
														"item", 
														id)).start();
				}
			} catch(Exception e) {
				System.out.println("Unable to send delete notification to tool: " + e.getMessage());
			}
        }
    }

	    
		
    /**
    * Decide whether or a tool should be notified of a change
    */
    private boolean notify(final String toolName, final Tool tool, final HttpServletRequest request) {
    	return !((tool.getIpAddress().equals(request.getRemoteAddr())) && (tool.getName().toString().equals(toolName)));
    }
    
    class NotificationThread implements Runnable {
    	private String url;
		private Long entityId;
		private String entityName;
		private boolean failedDelivery;
		private String returnTypeClass;
		private Marshaller marshaller;
		private ImbNotification notification;
		private ImbNotificationType notificationType;
		private Object unmarshalledObject;
		
		public NotificationThread(final ImbNotificationType notificationType, final String url, final String entityName, final Long entityId) {
			this.url = url;
			this.entityId = entityId;
			this.entityName = entityName;
			this.notificationType = notificationType;
		}
		
		public NotificationThread(final ImbNotificationType notificationType, final String url, final Object unmarshalledObject, 
									final Class<?> returnTypeClass, final String entityName, final Long entityId, Marshaller marshaller) {
			this.url = url;
			this.entityId = entityId;
			this.entityName = entityName;
			this.marshaller = marshaller;
			this.notificationType = notificationType;
			this.returnTypeClass = returnTypeClass.getName();
			this.unmarshalledObject = unmarshalledObject;
		}
		
		public NotificationThread(final ImbNotificationType notificationType, final String url, 
									final Object unmarshalledObject, final String entityName, Marshaller marshaller) {
			this.url = url;
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
					
					if(marshaller != null) {
						try {
							notification.setEntity(new String(this.marshal(unmarshalledObject).toByteArray(), "UTF-8"));
						} catch(Exception marshalException) {
							System.out.println("Unable to marshall entity for notification: " + marshalException.getMessage());
						}
					}
					
					notification.persist();
				}
			}
		}
		
	    /**
    	* Marshall the specified $type into an output stream.  
	    */
    	public ByteArrayOutputStream marshal(Object imbType) {
    		ByteArrayOutputStream outputStream;
    	
	    	try {
    			outputStream = new ByteArrayOutputStream();
	    		marshaller.marshal(imbType, new StreamResult(outputStream));
		    } catch(Exception e) {
    			System.out.println("Unable to marshal: " + e.getMessage());
    			outputStream = null;
	    	}
    	
    		return outputStream;
    	}
    }
}