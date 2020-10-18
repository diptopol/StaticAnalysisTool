package com.staticAnalysisTool.checker;

import com.staticAnalysisTool.bugPattern.BugPattern;

import java.io.File;
import java.util.List;

public interface BugChecker {

    List<BugPattern> check(File projectDirectory);

}
