package student.validation;

import java.util.*;

public class EnrollmentValidatorFactory {
    // Factory Method: create list of validators for an enrollment operation
    public static List<EnrollmentValidator> createValidators(){
        List<EnrollmentValidator> validators = new ArrayList<>();
        validators.add(new PrerequisiteValidator());
        validators.add(new CapacityValidator());
        validators.add(new TimeConflictValidator());
        return validators;
    }
}
