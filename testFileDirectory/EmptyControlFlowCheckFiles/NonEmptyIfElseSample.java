
public class NonEmptyIfElseSample {

    public void nonEmptyIfElseSample() {
        String valueForEvaluation = "VALUE_EVALUATION";

        if (valueForEvaluation.startsWith("VALUE")) {
            System.out.println("Started with VALUE");
        } else {
            System.out.println("Does not start with VALUE");
        }
    }
}