package admin.report;

import common.models.Course;
import java.util.*;

public class ConcreteCsvReportGenerator {
    // Imagine this is a 3rd party library class we cannot change.
    public String createCSV(List<Course> courses){
        StringBuilder sb = new StringBuilder();
        sb.append("Course,Enrolled,Capacity\n");
        for(Course c : courses){
            sb.append(String.format("%s,%d,%d\n", c.getCode(), c.getRoster().size(), c.getCapacity()));
        }
        return sb.toString();
    }
}
