package com.staticAnalysisTool.bugPattern;

public class BugPattern {

    private BugType bugType;
    private BugLocation bugLocation;

    public BugPattern(BugType bugType, BugLocation bugLocation) {
        this.bugType = bugType;
        this.bugLocation = bugLocation;
    }

    public BugType getBugType() {
        return bugType;
    }

    public BugLocation getBugLocation() {
        return bugLocation;
    }

    @Override
    public String toString() {
        return "BugPattern{" +
                "bugType=" + bugType +
                ", bugLocation=" + bugLocation +
                '}';
    }
}
