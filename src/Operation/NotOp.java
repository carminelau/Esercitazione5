package Operation;

public class NotOp extends Operations {

    private static final String operation = "not";

    public NotOp(ExprOp e) {
        super(e);
    }

    @Override
    public String toString() {
        return "NotOp{" +
                "e=" + super.getE1() +
                '}';
    }
}
