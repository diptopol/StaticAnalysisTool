package checker;

import bugPattern.BugLocation;
import bugPattern.BugPattern;
import bugPattern.BugType;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import staticAnalysis.DirectoryExplorer;
import util.Util;

import java.io.File;
import java.io.IOException;
import java.util.*;

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


        } else if (BugType.INADEQUATE_LOGGING_INFO.equals(bugType)) {
            new DirectoryExplorer((path, file) -> path.endsWith(".java"), (path, file) -> {
                try {
                    new VoidVisitorAdapter<Object>() {

                        public void visit(TryStmt tryStmt, Object arg) {
                            super.visit(tryStmt, arg);

                            List<String> errorMessageInLoggingList = new ArrayList<>();

                            for (CatchClause catchClause : tryStmt.getCatchClauses()) {
                                BlockStmt clauseBody = catchClause.getBody();
                                List<String> errorMessageListPerBlockStatement = new ArrayList<>();
                                List<Statement> clauseBodyStatementList = clauseBody.getStatements();

                                for (Statement clauseBodyStatement : clauseBodyStatementList) {
                                    if (clauseBodyStatement.isExpressionStmt()) {
                                        ExpressionStmt expressionStmt = clauseBodyStatement.asExpressionStmt();
                                        Expression expression = expressionStmt.getExpression();

                                        if (expression.isMethodCallExpr()) {
                                            MethodCallExpr methodCallExpr = expression.asMethodCallExpr();
                                            String methodName = methodCallExpr.getNameAsString();

                                            List<String> logLevelMethodNameList =
                                                    new ArrayList<>(Arrays.asList("warn", "info", "debug", "error", "trace", "println"));

                                            if (logLevelMethodNameList.contains(methodName)) {
                                                if (methodCallExpr.getArguments().isEmpty()) {
                                                    //TODO: need to decide what to do
                                                }

                                                if (methodCallExpr.getArguments().size() == 1 &&
                                                        methodCallExpr.getArgument(0).isStringLiteralExpr()) {

                                                    String argument = methodCallExpr.getArgument(0).asStringLiteralExpr().asString();

                                                    if (!errorMessageListPerBlockStatement.contains(argument)) {
                                                        errorMessageListPerBlockStatement.add(argument);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                boolean duplicateLogExists = false;

                                for (String errorMessageList : errorMessageInLoggingList) {
                                    List<String> errorMessages = new ArrayList<>(Arrays.asList(errorMessageList.split(";")));

                                    if (errorMessages.containsAll(errorMessageListPerBlockStatement)) {
                                        duplicateLogExists = true;
                                        bugPatterns.add(new BugPattern(bugType, new BugLocation(file.getName(), Util.getLineNumber(catchClause))));
                                    }
                                }

                                if (!duplicateLogExists) {
                                    errorMessageInLoggingList.add(String.join(";", errorMessageListPerBlockStatement));
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
