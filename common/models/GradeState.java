package common.models;

public interface GradeState {
    void submit(Grade g);
    void approve(Grade g);
    String getName();
}
