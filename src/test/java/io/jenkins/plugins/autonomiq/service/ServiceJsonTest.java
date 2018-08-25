package io.jenkins.plugins.autonomiq.service;

import com.google.gson.reflect.TypeToken;
import io.jenkins.plugins.autonomiq.service.types.TestCasesResponse;
import io.jenkins.plugins.autonomiq.util.AiqUtil;
import org.junit.Test;

import java.io.*;
import java.util.List;

public class ServiceJsonTest {


    @Test
    public void testCaseJson() throws Exception {

        String currentDirectory;
        currentDirectory = System.getProperty("user.dir");

        File f = new File(/* currentDirectory + File.separatorChar + */"test_files/testcase.json");
        BufferedReader b = new BufferedReader(new FileReader(f));
        String data = b.readLine();

        List<TestCasesResponse> testCaseList = AiqUtil.gson.fromJson(data,
                new TypeToken<List<TestCasesResponse>>() {
                }.getType());

    }
}
