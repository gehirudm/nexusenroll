package faculty;

import common.models.*;
import java.util.*;

/*
 Faculty service simulation: view roster and submit grades.
 Demonstrates State pattern for grade lifecycle.
*/
public class FacultyServiceMain {
    public static void main(String[] args){
        // Setup data
        Student s1 = new Student("S001","Alice");
        Student s2 = new Student("S002","Bob");
        Course cs201 = new Course("CS201","Algorithms",10,"Mon9-11");

        // Enroll students (directly)
        Enrollment e1 = new Enrollment(s1, cs201);
        Enrollment e2 = new Enrollment(s2, cs201);
        cs201.addEnrollment(e1); s1.addEnrollment(e1);
        cs201.addEnrollment(e2); s2.addEnrollment(e2);

        // View roster
        System.out.println("Roster for " + cs201.getCode());
        cs201.getRoster().forEach(r -> System.out.println(" - " + r.getStudent()));

        // Create grades and submit (State)
        Grade g1 = new Grade(s1, cs201);
        g1.setLetter("A");
        System.out.println("Initial grade: " + g1);
        g1.submit(); // moves to Submitted
        System.out.println("After submit: " + g1);
        g1.approve(); // moves to Final
        System.out.println("After approve: " + g1);

        // Demonstrate partial failure handling: submit batch grades, one invalid -> allow corrections
        Grade g2 = new Grade(s2, cs201);
        g2.setLetter("X"); // invalid letter; business rule could reject
        System.out.println("Submitting batch grades...");
        List<Grade> batch = Arrays.asList(g1, g2);
        for(Grade g : batch){
            try {
                if(!isValidLetter(g.getLetter())){
                    throw new RuntimeException("Invalid grade letter for " + g.getStudent().getId());
                }
                g.submit();
            } catch(Exception ex){
                System.out.println("Error processing grade: " + ex.getMessage() + " -- instructor can correct and resubmit.");
            }
        }
    }

    static boolean isValidLetter(String l){
        return l != null && (l.matches("[ABCDF]") || l.equals("P"));
    }
}
