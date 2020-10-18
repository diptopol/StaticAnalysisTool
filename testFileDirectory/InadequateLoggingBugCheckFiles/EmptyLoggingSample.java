

public class EmptyLoggingSample {

    public void duplicateLoggingSample() {
        try {

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println();
        } catch (ArrayStoreException e) {
            System.out.println("error");
        } finally {

        }
    }
}
