package student.validation;

import common.models.Student;
import common.models.Course;

public interface EnrollmentValidator {
    boolean validate(Student s, Course c);
    String reason();
}
