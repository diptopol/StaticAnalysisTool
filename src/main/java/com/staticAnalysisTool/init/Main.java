package com.staticAnalysisTool.init;

import com.staticAnalysisTool.bugPattern.BugPattern;
import com.staticAnalysisTool.bugPattern.BugType;
import com.staticAnalysisTool.checker.EmptyControlFlowChecker;
import com.staticAnalysisTool.checker.InadequateLoggingBugChecker;
import com.staticAnalysisTool.checker.MissingEqualMethodBugChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

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

        logger.info("Missing Equal Method Count: {}", missingEqualMethodCount);
        logger.info("Inadequate Logging Count: {}", inadequateLoggingCount);
        logger.info("Empty Control Flow Count: {}", emptyControlFlowCount);

        for (BugPattern bugPattern : bugPatterns) {
            logger.info("BugPattern: {}", bugPattern);
        }
    }
}
