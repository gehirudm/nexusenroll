package admin;

import common.models.*;
/*
 Facade pattern: simplify complex operations across services (demo).
*/
public class ServicesFacade {
    public void forceAddStudentToCourse(Student s, Course c){
        Enrollment e = new Enrollment(s,c);
        // Admin can override capacity rules
        c.addEnrollment(e);
        s.addEnrollment(e);
        System.out.println("[Facade] Force-added " + s + " to " + c);
    }
}
