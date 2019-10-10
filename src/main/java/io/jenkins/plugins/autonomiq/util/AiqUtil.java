package io.jenkins.plugins.autonomiq.util;

import com.google.gson.Gson;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class AiqUtil {

    public static final Gson gson = new Gson();

    public static String getExceptionTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String sStackTrace = sw.toString();
        return sStackTrace;
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static class ItemListFromString {
        private String error;
        private List<String> itemList;

        public ItemListFromString(String error, List<String> itemList) {
            this.error = error;
            this.itemList = itemList;
        }

        public String getError() {
            return error;
        }

        public List<String> getItemList() {
            return itemList;
        }

    }

    public static ItemListFromString getItemListFromString(String string) {

        String lineTermRE = "\n";
        String lineSplitRE = "\\s+";

        String[] lines = string.split(lineTermRE);
        List<String> items = new ArrayList<>(lines.length);

        for (String line : lines) {
            String trim = line.trim();
            // if not empty line
            if (trim.length() > 0) {

                // check if line has more than one item
                String[] lineSplit = trim.split(lineSplitRE);
                if (lineSplit.length > 1) {
                    return new ItemListFromString(String.format("Line in text area has more than one item per line: '%s'",
                            trim), null);
                }

                items.add(trim);
            }

        }

        return new ItemListFromString(null, items);
    }
}
