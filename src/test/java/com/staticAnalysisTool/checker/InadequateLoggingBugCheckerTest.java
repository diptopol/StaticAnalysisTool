package com.staticAnalysisTool.checker;

import com.staticAnalysisTool.bugPattern.BugPattern;
import com.staticAnalysisTool.bugPattern.BugType;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class InadequateLoggingBugCheckerTest {

    @Test
    public void testDuplicateLogging() {
        List<BugPattern> bugPatternList = new InadequateLoggingBugChecker()
                .check(new File("testFileDirectory/InadequateLoggingBugCheckFiles/DuplicateLoggingSample.java"));

        Assert.assertEquals(1, bugPatternList.size());
        Assert.assertEquals(BugType.INADEQUATE_LOGGING_INFO, bugPatternList.get(0).getBugType());
    }

    @Test
    public void testEmptyLogging() {
        List<BugPattern> bugPatternList = new InadequateLoggingBugChecker()
                .check(new File("testFileDirectory/InadequateLoggingBugCheckFiles/EmptyLoggingSample.java"));

        Assert.assertEquals(1, bugPatternList.size());
        Assert.assertEquals(BugType.INADEQUATE_LOGGING_INFO, bugPatternList.get(0).getBugType());
    }
}
