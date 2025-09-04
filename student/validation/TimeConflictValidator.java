package student.validation;

import common.models.Student;
import common.models.Course;

public class TimeConflictValidator implements EnrollmentValidator {
    private String lastReason = "";
    public boolean validate(Student s, Course c){
        // simplified: conflict if any enrolled course has same schedule string
        for(var e : s.getEnrollments()){
            if(e.getCourse().getSchedule().equals(c.getSchedule())){
                lastReason = "Time conflict with " + e.getCourse().getCode();
                return false;
            }
        }
        return true;
    }
    public String reason(){ return lastReason; }
}
