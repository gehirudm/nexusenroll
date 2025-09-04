package admin.report;

import java.util.*;
import common.models.Course;

/*
 Adapter pattern: adapt ConcreteCsvReportGenerator to our ReportGenerator interface
*/
public class ReportAdapter implements ReportGenerator {
    private final ConcreteCsvReportGenerator adaptee = new ConcreteCsvReportGenerator();
    public String generateEnrollmentReport(List<Course> courses){
        return adaptee.createCSV(courses);
    }
}
