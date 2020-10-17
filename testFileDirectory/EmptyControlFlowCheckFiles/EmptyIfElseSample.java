public class EmptyIfElseSample {

    public void testEmptyIfElseSample() {
        String valueForEvaluation = "VALUE_EVALUATION";

        if (valueForEvaluation.startsWith("VALUE")) {
            System.out.println("Started with VALUE");

        } else if (valueForEvaluation.startsWith("KEY")) {

        } else {
            System.out.println("Does not start with VALUE");
        }
    }
}