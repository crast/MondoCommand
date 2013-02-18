package mondocommand;

public class MondoFailure extends Exception {
    private static final long serialVersionUID = 8065592348213485173L;

    public MondoFailure(String message) {
        super(message);
    }
}
