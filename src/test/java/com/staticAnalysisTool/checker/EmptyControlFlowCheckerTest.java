package com.staticAnalysisTool.checker;

import com.staticAnalysisTool.bugPattern.BugPattern;
import com.staticAnalysisTool.bugPattern.BugType;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class EmptyControlFlowCheckerTest {

    @Test
    public void testValidIfElseStatement() {
        List<BugPattern> bugPatternList = new EmptyControlFlowChecker()
                .check(new File("testFileDirectory/EmptyControlFlowCheckFiles/NonEmptyIfElseSample.java"));

        Assert.assertEquals(0, bugPatternList.size());
    }

    @Test
    public void testInvalidIfElseStatement() {

        List<BugPattern> bugPatternList = new EmptyControlFlowChecker()
                .check(new File("testFileDirectory/EmptyControlFlowCheckFiles/EmptyIfElseSample.java"));

        Assert.assertEquals(1, bugPatternList.size());
        Assert.assertEquals(BugType.EMPTY_CONTROL_FLOW, bugPatternList.get(0).getBugType());
        Assert.assertEquals(9, bugPatternList.get(0).getBugLocation().getLineNumber());
    }

    @Test
    public void testEmptyForLoop() {
        List<BugPattern> bugPatternList = new EmptyControlFlowChecker()
                .check(new File("testFileDirectory/EmptyControlFlowCheckFiles/EmptyForLoopSample.java"));

        Assert.assertEquals(1, bugPatternList.size());
        Assert.assertEquals(BugType.EMPTY_CONTROL_FLOW, bugPatternList.get(0).getBugType());
        Assert.assertEquals(4, bugPatternList.get(0).getBugLocation().getLineNumber());
    }

    @Test
    public void testEmptyForEach() {
        List<BugPattern> bugPatternList = new EmptyControlFlowChecker()
                .check(new File("testFileDirectory/EmptyControlFlowCheckFiles/EmptyForEachSample.java"));

        Assert.assertEquals(1, bugPatternList.size());
        Assert.assertEquals(BugType.EMPTY_CONTROL_FLOW, bugPatternList.get(0).getBugType());
        Assert.assertEquals(10, bugPatternList.get(0).getBugLocation().getLineNumber());
    }

    @Test
    public void testEmptySwitchStatement() {
        List<BugPattern> bugPatternList = new EmptyControlFlowChecker()
                .check(new File("testFileDirectory/EmptyControlFlowCheckFiles/EmptySwitchSample.java"));

        Assert.assertEquals(1, bugPatternList.size());
        Assert.assertEquals(BugType.EMPTY_CONTROL_FLOW, bugPatternList.get(0).getBugType());
        Assert.assertEquals(6, bugPatternList.get(0).getBugLocation().getLineNumber());
    }

}
