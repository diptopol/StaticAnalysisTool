package com.staticAnalysisTool.checker;

import com.staticAnalysisTool.bugPattern.BugLocation;
import com.staticAnalysisTool.bugPattern.BugPattern;
import com.staticAnalysisTool.bugPattern.BugType;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.staticAnalysisTool.util.DirectoryExplorer;
import com.staticAnalysisTool.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MissingEqualMethodBugChecker implements BugChecker {

    @Override
    public List<BugPattern> check(File projectDirectory) {
        List<BugPattern> bugPatterns = new ArrayList<>();

        new DirectoryExplorer((path, file) -> path.endsWith(".java") || file.getName().endsWith(".java"), (path, file) -> {
            try {
                Map<String, Integer> hashMapAndEqualMethodPosition = new HashMap<>();

                new VoidVisitorAdapter<Object>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void visit(MethodDeclaration methodDeclaration, Object arg) {
                        super.visit(methodDeclaration, arg);

                        Map<String, Integer> hashMapAndEqualMethodPosition = (Map<String, Integer>) arg;

                        if (methodDeclaration.getNameAsString().equals("equals") && methodDeclaration.getTypeAsString().equals("boolean")) {
                            NodeList<Parameter> nodes = methodDeclaration.getParameters();

                            if ((nodes.size() == 1) && (nodes.get(0).getTypeAsString().equals("Object"))) {
                                hashMapAndEqualMethodPosition.put(methodDeclaration.getNameAsString(), Util.getLineNumber(methodDeclaration));
                            }

                        } else if (methodDeclaration.getNameAsString().equals("hashCode") && methodDeclaration.getTypeAsString().equals("int")
                                && methodDeclaration.getParameters().size() == 0) {
                            hashMapAndEqualMethodPosition.put(methodDeclaration.getNameAsString(), Util.getLineNumber(methodDeclaration));
                        }
                    }
                }.visit(JavaParser.parse(file), hashMapAndEqualMethodPosition);

                if (hashMapAndEqualMethodPosition.containsKey("hashCode")
                        && !hashMapAndEqualMethodPosition.containsKey("equals")) {

                    bugPatterns.add(new BugPattern(BugType.MISSING_EQUAL_METHOD, new BugLocation(file.getName(),
                            hashMapAndEqualMethodPosition.get("hashCode"))));
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).explore(projectDirectory);

        return bugPatterns;
    }
}
