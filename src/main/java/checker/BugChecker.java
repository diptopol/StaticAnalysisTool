package checker;

import bugPattern.BugPattern;
import bugPattern.BugType;

import java.io.File;
import java.util.List;

public interface BugChecker {

    List<BugPattern> check(File projectDirectory, BugType bugType);
}
