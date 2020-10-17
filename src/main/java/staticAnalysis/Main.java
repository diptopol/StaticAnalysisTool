package staticAnalysis;

import bugPattern.BugPattern;
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

        for (BugPattern bugPattern : bugPatterns) {
            System.out.println(bugPattern);
        }
    }
}
