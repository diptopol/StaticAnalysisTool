package checker;

import bugPattern.BugLocation;
import bugPattern.BugPattern;
import bugPattern.BugType;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import staticAnalysis.DirectoryExplorer;
import util.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InadequateLoggingBugChecker implements BugChecker {

    @Override
    public List<BugPattern> check(File projectDirectory) {
        List<BugPattern> bugPatterns = new ArrayList<>();

        new DirectoryExplorer((path, file) -> path.endsWith(".java") || file.getName().endsWith(".java"), (path, file) -> {
            try {
                new VoidVisitorAdapter<Object>() {

                    @SuppressWarnings("unchecked")
                    public void visit(TryStmt tryStmt, Object arg) {
                        super.visit(tryStmt, arg);

                        List<BugPattern> bugPatterns = (List<BugPattern>) arg;
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
                                                bugPatterns.add(new BugPattern(BugType.INADEQUATE_LOGGING_INFO,
                                                        new BugLocation(file.getName(), Util.getLineNumber(methodCallExpr))));
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

                                if (!errorMessageListPerBlockStatement.isEmpty()
                                        && errorMessages.containsAll(errorMessageListPerBlockStatement)) {
                                    duplicateLogExists = true;
                                    bugPatterns.add(new BugPattern(BugType.INADEQUATE_LOGGING_INFO, new BugLocation(file.getName(), Util.getLineNumber(catchClause))));
                                }
                            }

                            if (!duplicateLogExists) {
                                errorMessageInLoggingList.add(String.join(";", errorMessageListPerBlockStatement));
                            }
                        }
                    }
                }.visit(JavaParser.parse(file), bugPatterns);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }).explore(projectDirectory);

        return bugPatterns;
    }
}
