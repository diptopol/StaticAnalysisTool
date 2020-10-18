

public class DuplicateLoggingSample {

    public void duplicateLoggingSample() {
        try {

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("error");
        } catch (ArrayStoreException e) {
            System.out.println("error");
        } finally {

        }
    }
}
