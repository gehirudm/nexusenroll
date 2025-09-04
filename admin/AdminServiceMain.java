package admin;

import admin.report.*;
import common.models.*;
import java.util.*;

/*
 Admin service simulation: create courses, change capacity, generate reports.
 Demonstrates Adapter pattern (ReportAdapter) and a small Facade (ServicesFacade).
*/
public class AdminServiceMain {
    public static void main(String[] args){
        Course c1 = new Course("BUS101","Intro Business",50,"Tue10-12");
        Course c2 = new Course("CS201","Algorithms",2,"Mon9-11");
        Student s1 = new Student("S001","Alice");
        Student s2 = new Student("S002","Bob");
        // enroll two students into CS201 to show capacity usage
        Enrollment e1 = new Enrollment(s1, c2); c2.addEnrollment(e1); s1.addEnrollment(e1);
        Enrollment e2 = new Enrollment(s2, c2); c2.addEnrollment(e2); s2.addEnrollment(e2);

        List<Course> courses = Arrays.asList(c1, c2);
        ReportGenerator rg = new ReportAdapter();
        String csv = rg.generateEnrollmentReport(courses);
        System.out.println("Enrollment report (CSV):");
        System.out.println(csv);

        // Facade usage: show simple facade to interact multiple services (simplified)
        ServicesFacade facade = new ServicesFacade();
        facade.forceAddStudentToCourse(s1, c1);
        System.out.println("After force-add, course " + c1.getCode() + " enrolled count: " + c1.getRoster().size());
    }
}
