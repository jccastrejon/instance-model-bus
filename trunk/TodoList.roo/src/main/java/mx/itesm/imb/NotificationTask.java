package mx.itesm.imb;

import mx.itesm.todolist.domain.ImbNotification;
import mx.itesm.todolist.domain.ImbNotificationType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Unmarshaller;
import org.springframework.web.client.RestTemplate;

public class NotificationTask {

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private Unmarshaller unmarshaller;

    public void sendNotifications() {
    	List<ImbNotification> notifications;
    	
    	notifications = ImbNotification.findAllImbNotifications();
    	if((notifications != null) && (!notifications.isEmpty())) {
    		for(ImbNotification notification : notifications) {
    			new Thread(new NotificationThread(notification, restTemplate, unmarshaller)).start();
    		}
    	}
    }
    
    class NotificationThread implements Runnable {
    	boolean failedDelivery;
    	RestTemplate restTemplate;
    	Unmarshaller unmarshaller;
    	ImbNotification notification;
    	
    	public NotificationThread(ImbNotification notification, RestTemplate restTemplate, Unmarshaller unmarshaller) {
    		this.notification = notification;
    		this.restTemplate = restTemplate;
    		this.unmarshaller = unmarshaller;
    	}
    	
    	@Override
		public void run() {
			Object unmarshalledObject;
			
			unmarshalledObject = null;
			if(notification.getEntity() != null) {
				try {
					unmarshalledObject = unmarshaller.unmarshal(new StreamSource(new ByteArrayInputStream(notification.getEntity().getBytes("UTF-8"))));
				} catch(Exception e) {
					System.out.println("Unable to unmarshall object for notification: " + e.getMessage());
				}
			}
			
			try {
				switch(notification.getType()) {
					case CreateEntity:
						restTemplate.put(notification.getUrl(), unmarshalledObject, notification.getEntityName());
						break;
					
					case UpdateEntity:
						restTemplate.postForObject(notification.getUrl(), unmarshalledObject, 
							Class.forName(notification.getReturnTypeClass()), notification.getEntityName(), notification.getEntityId());
						break;
					
					case DeleteEntity:
						restTemplate.delete(notification.getUrl(), notification.getEntityName(), notification.getEntityId());
						break;
				}
			} catch(Exception e) {
				failedDelivery = false;
				if(IOException.class.isAssignableFrom(e.getClass())) {
					failedDelivery = true;
				} else if((e.getCause() != null) && (IOException.class.isAssignableFrom(e.getCause().getClass()))) {
					failedDelivery = true;
				}
				
				// Remove the notification if it was delivered
				if(!failedDelivery) {
					notification.remove();
				}
			}
		}
    }
}