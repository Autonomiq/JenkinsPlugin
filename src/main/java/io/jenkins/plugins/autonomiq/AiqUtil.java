package io.jenkins.plugins.autonomiq;

import java.io.PrintWriter;
import java.io.StringWriter;

public class AiqUtil {


    public static String getExceptionTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String sStackTrace = sw.toString();
        return sStackTrace;
    }

}
