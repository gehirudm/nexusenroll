package common.models;

public class SubmittedState implements GradeState {
    public void submit(Grade g){
        System.out.println("Already submitted.");
    }
    public void approve(Grade g){
        System.out.println("Approving grade -> final.");
        g.setState(new FinalState());
    }
    public String getName(){ return "Submitted"; }
}
