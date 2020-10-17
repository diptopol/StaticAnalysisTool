package util;

import com.github.javaparser.ast.Node;

public class Util {

    public static int getLineNumber(Node node) {
        return node.getRange().isPresent() ? node.getRange().get().begin.line : 0;
    }
}
