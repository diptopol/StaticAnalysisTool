package com.staticAnalysisTool.checker;

import com.staticAnalysisTool.bugPattern.BugPattern;
import com.staticAnalysisTool.bugPattern.BugType;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class MissingEqualMethodBugCheckerTest {

    @Test
    public void testMissingEqualMethod() {
        List<BugPattern> bugPatternList = new MissingEqualMethodBugChecker()
                .check(new File("testFileDirectory/MissingEqualMethodBugCheckFiles/MissingEqualMethodSample.java"));

        Assert.assertEquals(1, bugPatternList.size());
        Assert.assertEquals(BugType.MISSING_EQUAL_METHOD, bugPatternList.get(0).getBugType());

    }
}
