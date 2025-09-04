package student.validation;

import common.models.Student;
import common.models.Course;

public class CapacityValidator implements EnrollmentValidator {
    private String lastReason = "";
    public boolean validate(Student s, Course c){
        if(c.isFull()){
            lastReason = "Course is full";
            return false;
        }
        return true;
    }
    public String reason(){ return lastReason; }
}
