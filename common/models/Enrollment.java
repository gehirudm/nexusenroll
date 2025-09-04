package common.models;

public class Enrollment {
    private final Student student;
    private final Course course;

    public Enrollment(Student student, Course course){
        this.student = student; this.course = course;
    }
    public Student getStudent(){ return student; }
    public Course getCourse(){ return course; }
    @Override public String toString(){ return String.format("Enrollment[%s->%s]", student.getId(), course.getCode()); }
}
