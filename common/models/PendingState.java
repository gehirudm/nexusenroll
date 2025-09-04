package common.models;

public class PendingState implements GradeState {
    public void submit(Grade g){
        System.out.println("Submitting grade -> moving to SubmittedState");
        g.setState(new SubmittedState());
    }
    public void approve(Grade g){
        System.out.println("Cannot approve: grade still pending.");
    }
    public String getName(){ return "Pending"; }
}
