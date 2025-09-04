package common.notification;

import java.util.*;

public class NotificationService {
    // Singleton pattern: single shared NotificationService
    private static NotificationService instance;
    private final List<NotificationListener> listeners = new ArrayList<>();

    private NotificationService(){}

    public static synchronized NotificationService getInstance(){
        if(instance == null) instance = new NotificationService();
        return instance;
    }

    public void register(NotificationListener l){
        listeners.add(l);
    }
    public void unregister(NotificationListener l){
        listeners.remove(l);
    }

    // Observer pattern: publish notifications to registered listeners
    public void notifyAll(String topic, String message){
        for(NotificationListener l : new ArrayList<>(listeners)){
            l.onNotify(topic, message);
        }
    }
}
