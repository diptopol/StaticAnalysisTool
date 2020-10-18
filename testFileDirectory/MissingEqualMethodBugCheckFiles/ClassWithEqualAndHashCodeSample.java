
import java.util.Objects;

public class ClassWithEqualAndHashCodeSample {

    private int id;
    private String value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BugPattern that = (BugPattern) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
