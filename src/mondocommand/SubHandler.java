package mondocommand;


public interface SubHandler {
    public void handle(CallInfo call) throws MondoFailure;
}
