package student.validation;

import common.models.Student;
import common.models.Course;

public class PrerequisiteValidator implements EnrollmentValidator {
    private String lastReason = "";
    public boolean validate(Student s, Course c){
        for(String p : c.getPrerequisites()){
            if(!s.getCompletedCourses().contains(p)){
                lastReason = "Missing prerequisite: " + p;
                return false;
            }
        }
        return true;
    }
    public String reason(){ return lastReason; }
}
