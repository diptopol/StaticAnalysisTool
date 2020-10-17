package checker;

import bugPattern.BugLocation;
import bugPattern.BugPattern;
import bugPattern.BugType;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import staticAnalysis.DirectoryExplorer;
import util.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EmptyControlFlowChecker implements BugChecker {

    @Override
    public List<BugPattern> check(File projectDirectory) {
        List<BugPattern> bugPatterns = new ArrayList<>();

        new DirectoryExplorer((path, file) -> path.endsWith(".java"), (path, file) -> {
            try {
                new VoidVisitorAdapter<Object>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void visit(IfStmt ifStmt, Object arg) {
                        super.visit(ifStmt, arg);

                        List<BugPattern> bugPatterns = (List<BugPattern>) arg;
                        checkForEmptyStatement(ifStmt, bugPatterns);
                    }

                    private void checkForEmptyStatement(Statement statement, List<BugPattern> bugPatterns) {
                        if (statement.isBlockStmt()) {
                            BlockStmt blockStmt = (BlockStmt) statement;

                            if (blockStmt.isEmpty()) {
                                bugPatterns.add(new BugPattern(BugType.EMPTY_CONTROL_FLOW, new BugLocation(file.getName(),
                                        Util.getLineNumber(blockStmt))));
                            } else {
                                boolean nonEmptyStatementExists = false;

                                for (Statement statementInBlock : blockStmt.getStatements()) {
                                    if (!statementInBlock.isEmptyStmt()) {
                                        nonEmptyStatementExists = true;
                                    }
                                }

                                if (!nonEmptyStatementExists) {
                                    bugPatterns.add(new BugPattern(BugType.EMPTY_CONTROL_FLOW, new BugLocation(file.getName(),
                                            Util.getLineNumber(blockStmt))));
                                }
                            }

                        } else if (statement.isIfStmt()) {
                            IfStmt ifStmt = (IfStmt) statement;

                            checkForEmptyStatement(ifStmt.getThenStmt(), bugPatterns);

                            if (ifStmt.getElseStmt().isPresent()) {
                                checkForEmptyStatement(ifStmt.getElseStmt().get(), bugPatterns);
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
