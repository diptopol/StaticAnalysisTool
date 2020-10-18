package staticAnalysis;

import bugPattern.BugPattern;
import bugPattern.BugType;
import checker.EmptyControlFlowChecker;
import checker.InadequateLoggingBugChecker;
import checker.MissingEqualMethodBugChecker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        File projectDirectory = new File("projectDirectory");
        List<BugPattern> bugPatterns = new ArrayList<>();

        bugPatterns.addAll(new MissingEqualMethodBugChecker().check(projectDirectory));
        bugPatterns.addAll(new EmptyControlFlowChecker().check(projectDirectory));
        bugPatterns.addAll(new InadequateLoggingBugChecker().check(projectDirectory));

        long missingEqualMethodCount = bugPatterns.stream()
                .filter(bugPattern -> BugType.MISSING_EQUAL_METHOD.equals(bugPattern.getBugType()))
                .count();

        long inadequateLoggingCount = bugPatterns.stream()
                .filter(bugPattern -> BugType.INADEQUATE_LOGGING_INFO.equals(bugPattern.getBugType()))
                .count();

        long emptyControlFlowCount = bugPatterns.stream()
                .filter(bugPattern -> BugType.EMPTY_CONTROL_FLOW.equals(bugPattern.getBugType()))
                .count();

        System.out.println("missingEqual:" + missingEqualMethodCount + ";inadequateLogging:" + inadequateLoggingCount
                + ";emptyControlFlow:" + emptyControlFlowCount);

        for (BugPattern bugPattern : bugPatterns) {
            System.out.println(bugPattern);
        }
    }
}
