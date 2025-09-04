package common.models;

import java.util.*;

public class Course {
    private final String code;
    private final String name;
    private int capacity;
    private final Set<String> prerequisites = new HashSet<>();
    private final List<Enrollment> roster = new ArrayList<>();
    private final String schedule; // simplified

    public Course(String code, String name, int capacity, String schedule){
        this.code = code; this.name = name; this.capacity = capacity; this.schedule = schedule;
    }
    public String getCode(){ return code; }
    public String getName(){ return name; }
    public int getCapacity(){ return capacity; }
    public void setCapacity(int c){ this.capacity = c; }
    public List<Enrollment> getRoster(){ return roster; }
    public String getSchedule(){ return schedule; }
    public void addPrerequisite(String c){ prerequisites.add(c); }
    public Set<String> getPrerequisites(){ return prerequisites; }
    public boolean isFull(){ return roster.size() >= capacity; }
    public void addEnrollment(Enrollment e){ roster.add(e); }
    public void removeEnrollment(Enrollment e){ roster.remove(e); }
    @Override public String toString(){ return String.format("Course[%s:%s]", code, name); }
}
