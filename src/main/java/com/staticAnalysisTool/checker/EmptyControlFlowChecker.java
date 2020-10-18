package com.staticAnalysisTool.checker;

import com.staticAnalysisTool.bugPattern.BugLocation;
import com.staticAnalysisTool.bugPattern.BugPattern;
import com.staticAnalysisTool.bugPattern.BugType;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.staticAnalysisTool.util.DirectoryExplorer;
import com.staticAnalysisTool.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class EmptyControlFlowChecker implements BugChecker {

    @Override
    public List<BugPattern> check(File projectDirectory) {
        List<BugPattern> bugPatterns = new ArrayList<>();

        new DirectoryExplorer((path, file) -> path.endsWith(".java") || file.getName().endsWith(".java"), (path, file) -> {
            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(IfStmt ifStmt, Object arg) {
                        super.visit(ifStmt, arg);

                        List<BugPattern> bugPatterns = (List<BugPattern>) arg;
                        checkForEmptyStatement(ifStmt, bugPatterns);
                    }

                    @Override
                    public void visit(ForStmt forStmt, Object arg) {
                        super.visit(forStmt, arg);

                        List<BugPattern> bugPatterns = (List<BugPattern>) arg;
                        checkForEmptyStatement(forStmt, bugPatterns);
                    }

                    @Override
                    public void visit(ForeachStmt foreachStmt, Object arg) {
                        super.visit(foreachStmt, arg);

                        List<BugPattern> bugPatterns = (List<BugPattern>) arg;
                        checkForEmptyStatement(foreachStmt, bugPatterns);
                    }

                    @Override
                    public void visit(DoStmt doStmt, Object arg) {
                        super.visit(doStmt, arg);

                        List<BugPattern> bugPatterns = (List<BugPattern>) arg;
                        checkForEmptyStatement(doStmt, bugPatterns);
                    }

                    @Override
                    public void visit(WhileStmt whileStmt, Object arg) {
                        super.visit(whileStmt, arg);

                        List<BugPattern> bugPatterns = (List<BugPattern>) arg;
                        checkForEmptyStatement(whileStmt, bugPatterns);
                    }

                    @Override
                    public void visit(SwitchStmt switchStmt, Object arg) {
                        super.visit(switchStmt, arg);

                        List<BugPattern> bugPatterns = (List<BugPattern>) arg;
                        checkForEmptyStatement(switchStmt, bugPatterns);
                    }

                    @Override
                    public void visit(TryStmt tryStmt, Object arg) {
                        super.visit(tryStmt, arg);

                        List<BugPattern> bugPatterns = (List<BugPattern>) arg;
                        checkForEmptyStatement(tryStmt, bugPatterns);
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
                            IfStmt ifStmt = statement.asIfStmt();

                            checkForEmptyStatement(ifStmt.getThenStmt(), bugPatterns);

                            if (ifStmt.getElseStmt().isPresent() && ifStmt.getElseStmt().get().isBlockStmt()) {

                                checkForEmptyStatement(ifStmt.getElseStmt().get(), bugPatterns);
                            }
                        } else if (statement.isForStmt()) {
                            ForStmt forStmt = statement.asForStmt();
                            checkForEmptyStatement(forStmt.getBody(), bugPatterns);

                        } else if (statement.isForeachStmt()) {
                            ForeachStmt foreachStmt = statement.asForeachStmt();
                            checkForEmptyStatement(foreachStmt.getBody(), bugPatterns);

                        } else if (statement.isDoStmt()) {
                            DoStmt doStmt = statement.asDoStmt();
                            checkForEmptyStatement(doStmt.getBody(), bugPatterns);

                        } else if (statement.isWhileStmt()) {
                            WhileStmt whileStmt = statement.asWhileStmt();
                            checkForEmptyStatement(whileStmt.getBody(), bugPatterns);

                        } else if (statement.isSwitchStmt()) {
                            SwitchStmt switchStmt = statement.asSwitchStmt();
                            NodeList<SwitchEntryStmt> switchEntryStmts = switchStmt.getEntries();

                            boolean isStatementListEmpty = false;
                            for (SwitchEntryStmt switchEntryStmt : switchEntryStmts) {
                                if (switchEntryStmt.getStatements().isEmpty()) {
                                    isStatementListEmpty = true;
                                } else {
                                    boolean nonEmptyStatementExists = false;
                                    for (Statement switchEntryBlockStatement : switchEntryStmt.getStatements()) {
                                        if (!switchEntryBlockStatement.isEmptyStmt() && !switchEntryBlockStatement.isBreakStmt()) {
                                            nonEmptyStatementExists = true;
                                        }
                                    }

                                    if (nonEmptyStatementExists) {
                                        isStatementListEmpty = false;
                                        break;

                                    } else {
                                        isStatementListEmpty = true;
                                    }
                                }
                            }

                            if (isStatementListEmpty) {
                                bugPatterns.add(new BugPattern(BugType.EMPTY_CONTROL_FLOW, new BugLocation(file.getName(),
                                        Util.getLineNumber(switchStmt))));
                            }
                        } else if (statement.isTryStmt()) {
                            TryStmt tryStmt = statement.asTryStmt();

                            if (tryStmt.getTryBlock().isEmpty()) {
                                bugPatterns.add(new BugPattern(BugType.EMPTY_CONTROL_FLOW, new BugLocation(file.getName(),
                                        Util.getLineNumber(tryStmt))));
                            }

                            for (CatchClause catchClause : tryStmt.getCatchClauses()) {
                                if (catchClause.getBody().isEmpty()) {
                                    bugPatterns.add(new BugPattern(BugType.EMPTY_CONTROL_FLOW, new BugLocation(file.getName(),
                                            Util.getLineNumber(catchClause))));
                                }
                            }

                            if (tryStmt.getFinallyBlock().isPresent() && tryStmt.getFinallyBlock().get().isEmpty()) {
                                bugPatterns.add(new BugPattern(BugType.EMPTY_CONTROL_FLOW, new BugLocation(file.getName(),
                                        Util.getLineNumber(tryStmt.getFinallyBlock().get()))));
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
