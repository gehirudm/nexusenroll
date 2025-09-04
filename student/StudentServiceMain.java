package student;

import common.models.*;
import common.messagebus.MessageBroker;
import common.notification.NotificationListener;
import common.notification.NotificationService;

/*
 Student service simulation main.
 Subscribes to notifications and performs sample enroll/drop flows.
*/
public class StudentServiceMain {
    public static void main(String[] args) {
        // Setup sample data
        Student alice = new Student("S001","Alice");
        alice.addCompletedCourse("CS101");

        Course cs201 = new Course("CS201","Algorithms",1,"Mon9-11");
        cs201.addPrerequisite("CS101");

        EnrollmentManager em = new EnrollmentManager();

        // Subscribe a simple listener to notifications (Observer)
        MessageBroker broker = new MessageBroker();
        broker.subscribe(new NotificationListener(){
            public void onNotify(String topic, String message){
                System.out.println("[StudentService Notification] topic=" + topic + " msg=" + message);
            }
        });

        // Try to enroll
        boolean ok = em.enroll(alice, cs201);
        System.out.println("Enroll result: " + ok);

        // Try double enrolling (should fail due to capacity)
        Student bob = new Student("S002","Bob");
        bob.addCompletedCourse("CS101");
        boolean ok2 = em.enroll(bob, cs201); // expected false (full)
        System.out.println("Enroll Bob result: " + ok2);

        // Alice drops the course -> should notify waitlist
        boolean dropOk = em.drop(alice, cs201);
        System.out.println("Drop Alice result: " + dropOk);

        // After drop, attempt to enroll Bob again (now has seat)
        boolean ok3 = em.enroll(bob, cs201);
        System.out.println("Enroll Bob after drop result: " + ok3);
    }
}
