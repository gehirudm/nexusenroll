package common.models;

import java.util.*;

public class Student {
    private final String id;
    private final String name;
    private final Set<String> completedCourses = new HashSet<>();
    private final List<Enrollment> enrollments = new ArrayList<>();

    public Student(String id, String name) {
        this.id = id; this.name = name;
    }
    public String getId(){return id;}
    public String getName(){return name;}
    public Set<String> getCompletedCourses(){ return completedCourses; }
    public List<Enrollment> getEnrollments(){ return enrollments; }

    public void addCompletedCourse(String courseCode){ completedCourses.add(courseCode); }
    public void addEnrollment(Enrollment e){ enrollments.add(e); }
    public void removeEnrollment(Enrollment e){ enrollments.remove(e); }
    @Override public String toString(){ return String.format("Student[%s:%s]", id, name); }
}
