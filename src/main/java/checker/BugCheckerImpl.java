package checker;

import bugPattern.BugLocation;
import bugPattern.BugPattern;
import bugPattern.BugType;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import staticAnalysis.DirectoryExplorer;
import util.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BugCheckerImpl implements BugChecker {

    public List<BugPattern> check(File projectDirectory, BugType bugType) {
        List<BugPattern> bugPatterns = new ArrayList<>();

        if (BugType.MISSING_EQUAL_METHOD.equals(bugType)) {
            new DirectoryExplorer((path, file) -> path.endsWith(".java"), (path, file) -> {
                try {
                    Map<String, Integer> hashMapAndEqualMethodPosition = new HashMap<>();

                    new VoidVisitorAdapter<Object>() {
                        @Override
                        public void visit(MethodDeclaration md, Object arg) {
                            super.visit(md, arg);

                            if (md.getNameAsString().equals("equals") && md.getTypeAsString().equals("boolean")) {
                                NodeList<Parameter> nodes = md.getParameters();

                                if ((nodes.size() == 1) && (nodes.get(0).getTypeAsString().equals("Object"))) {
                                    hashMapAndEqualMethodPosition.put(md.getNameAsString(), Util.getLineNumber(md));
                                }

                            } else if (md.getNameAsString().equals("hashCode") && md.getTypeAsString().equals("int")
                                    && md.getParameters().size() == 0) {
                                hashMapAndEqualMethodPosition.put(md.getNameAsString(), Util.getLineNumber(md));
                            }
                        }
                    }.visit(JavaParser.parse(file), null);

                    if (hashMapAndEqualMethodPosition.containsKey("hashCode")
                            && !hashMapAndEqualMethodPosition.containsKey("equals")) {
                        bugPatterns.add(new BugPattern(bugType, new BugLocation(file.getName(),
                                hashMapAndEqualMethodPosition.get("hashCode"))));
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).explore(projectDirectory);

        } else if (BugType.EMPTY_CONTROL_FLOW.equals(bugType)) {
            new DirectoryExplorer((path, file) -> path.endsWith(".java"), (path, file) -> {
                try {
                    new VoidVisitorAdapter<Object>() {
                        @Override
                        public void visit(IfStmt statement, Object arg) {
                            super.visit(statement, arg);
                            checkForEmptyStatement(statement);
                        }

                        private void checkForEmptyStatement(Statement statement) {
                            if (statement.isBlockStmt()) {
                                BlockStmt blockStmt = (BlockStmt) statement;

                                if (blockStmt.isEmpty()) {
                                    bugPatterns.add(new BugPattern(bugType, new BugLocation(file.getName(),
                                            Util.getLineNumber(statement))));
                                }

                                //TODO: check nested layer empty check
                            } else if (statement.isIfStmt()) {
                                IfStmt ifStmt = (IfStmt) statement;

                                checkForEmptyStatement(ifStmt.getThenStmt());

                                if (ifStmt.getElseStmt().isPresent()) {
                                    checkForEmptyStatement(ifStmt.getElseStmt().get());
                                }
                            }
                        }

                    }.visit(JavaParser.parse(file), null);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).explore(projectDirectory);


        } else {
            throw new IllegalStateException();
        }

        return bugPatterns;
    }
}
