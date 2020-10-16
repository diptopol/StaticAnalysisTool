package bugPattern;

public class BugLocation {

    private String fileName;
    private int lineNumber;

    public BugLocation(String fileName, int lineNumber) {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }

    @Override
    public String toString() {
        return "BugLocation{" +
                "fileName='" + fileName + '\'' +
                ", lineNumber=" + lineNumber +
                '}';
    }
}
