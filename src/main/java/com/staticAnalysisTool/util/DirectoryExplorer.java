package com.staticAnalysisTool.util;

import java.io.File;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class DirectoryExplorer {

    private BiFunction<String, File, Boolean> filter;
    private BiConsumer<String, File> fileHandler;


    public DirectoryExplorer(BiFunction<String, File, Boolean> filter, BiConsumer<String, File> fileHandler) {
        this.filter = filter;
        this.fileHandler = fileHandler;
    }

    public void explore(File root) {
        explore("", root);
    }

    private void explore(String path, File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                explore(path + "/" + child.getName(), child);
            }
        } else {
            if (filter.apply(path, file)) {
                fileHandler.accept(path, file);
            }
        }
    }
}
