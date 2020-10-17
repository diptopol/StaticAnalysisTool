package staticAnalysis;

import bugPattern.BugPattern;
import bugPattern.BugType;
import checker.BugChecker;
import checker.BugCheckerImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static BugChecker bugChecker;

    public static void main(String[] args) {
        File projectDirectory = new File("projectDirectory");
        bugChecker = new BugCheckerImpl();
        List<BugPattern> bugPatterns = new ArrayList<>();

        bugPatterns.addAll(bugChecker.check(projectDirectory, BugType.MISSING_EQUAL_METHOD));

        bugPatterns.addAll(bugChecker.check(projectDirectory, BugType.EMPTY_CONTROL_FLOW));

        bugPatterns.addAll(bugChecker.check(projectDirectory, BugType.INADEQUATE_LOGGING_INFO));

        for (BugPattern bugPattern : bugPatterns) {
            System.out.println(bugPattern);
        }
    }
}
