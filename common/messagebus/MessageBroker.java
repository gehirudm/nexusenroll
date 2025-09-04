package common.messagebus;

import java.util.*;
import common.notification.NotificationListener;
import common.notification.NotificationService;

/*
 A simple message broker that other services can use to publish/subscribe.
 This demonstrates the Observer pattern (listeners register to NotificationService).
*/
public class MessageBroker {
    private final NotificationService ns = NotificationService.getInstance();

    public void publish(String topic, String message){
        System.out.println("[MessageBroker] Publishing topic=" + topic + " message=" + message);
        ns.notifyAll(topic, message);
    }

    public void subscribe(NotificationListener l){
        ns.register(l);
    }
}
