package student;

import java.util.*;
import common.models.*;
import student.validation.*;
import common.messagebus.MessageBroker;

/*
 Core business logic for enrollment operations.
 Demonstrates:
  - Factory Method: EnrollmentValidatorFactory
  - Strategy: each EnrollmentValidator implements the validation strategy
  - Observer: uses MessageBroker to publish events (e.g., seat available)
  - Transactional behavior simulated: either all succeed or rollback
*/
public class EnrollmentManager {
    private final MessageBroker broker = new MessageBroker();

    public EnrollmentManager(){
    }

    public synchronized boolean enroll(Student s, Course c){
        System.out.println("Attempting to enroll " + s + " into " + c);
        List<EnrollmentValidator> validators = EnrollmentValidatorFactory.createValidators();
        for(EnrollmentValidator v : validators){
            if(!v.validate(s, c)){
                System.out.println("Validation failed: " + v.reason());
                return false;
            }
        }
        // Simulate transaction: update multiple objects
        Enrollment e = new Enrollment(s, c);
        try {
            c.addEnrollment(e);
            s.addEnrollment(e);
            System.out.println("Enrollment successful: " + e);
            // Publish event to message broker
            broker.publish("enrollment", "Student " + s.getId() + " enrolled in " + c.getCode());
            return true;
        } catch(Exception ex){
            // rollback simplistic
            c.removeEnrollment(new Enrollment(s,c));
            s.removeEnrollment(new Enrollment(s,c));
            System.out.println("Enrollment failed, rolled back");
            return false;
        }
    }

    // drop
    public synchronized boolean drop(Student s, Course c){
        System.out.println("Dropping " + s + " from " + c);
        Enrollment found = null;
        for(Enrollment e : s.getEnrollments()){
            if(e.getCourse().getCode().equals(c.getCode())){
                found = e; break;
            }
        }
        if(found == null){
            System.out.println("Student not enrolled in course");
            return false;
        }
        s.removeEnrollment(found);
        c.removeEnrollment(found);
        broker.publish("drop", "Student " + s.getId() + " dropped " + c.getCode());
        // notify waitlist in real world
        broker.publish("waitlist", "Seat opened in " + c.getCode());
        return true;
    }
}
