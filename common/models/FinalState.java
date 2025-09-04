package common.models;

public class FinalState implements GradeState {
    public void submit(Grade g){
        System.out.println("Grade final; cannot submit.");
    }
    public void approve(Grade g){
        System.out.println("Already final.");
    }
    public String getName(){ return "Final"; }
}
