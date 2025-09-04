package admin.report;

import common.models.Course;
import java.util.*;

/*
 Target interface for generating reports
*/
public interface ReportGenerator {
    String generateEnrollmentReport(List<Course> courses);
}
