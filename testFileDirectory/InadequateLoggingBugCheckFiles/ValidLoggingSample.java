

public class ValidLoggingSample {

    public void validLoggingSample() {
        try {
            System.out.println("inside try");

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("ArrayIndexOutOfBoundsException");
        } catch (ArrayStoreException e) {
            System.out.println("ArrayStoreException");
        }
    }
}
