package indexer.dataunit;

public class Location {
    int lineNumber;
    String path;

    public Location(int lineNumber, String path) {
        this.lineNumber = lineNumber;
        this.path = path;
    }

    @Override
    public String toString() {
        return "Location{" +
                "lineNumber=" + lineNumber +
                ", path='" + path + '\'' +
                '}';
    }
}
