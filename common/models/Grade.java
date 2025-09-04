package common.models;

public class Grade {
    private final Student student;
    private final Course course;
    private String letter;
    private GradeState state;

    public Grade(Student s, Course c){
        this.student = s; this.course = c; this.state = new PendingState();
    }
    public Student getStudent(){ return student; }
    public Course getCourse(){ return course; }
    public String getLetter(){ return letter; }
    public void setLetter(String l){ this.letter = l; }
    public GradeState getState(){ return state; }
    public void setState(GradeState s){ this.state = s; }

    // state transition helpers
    public void submit(){
        state.submit(this);
    }
    public void approve(){
        state.approve(this);
    }
    @Override public String toString(){
        return String.format("Grade[%s:%s=%s (%s)]", student.getId(), course.getCode(), letter, state.getName());
    }
}
