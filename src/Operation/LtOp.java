package Operation;

public class LtOp extends Operations {
    private ExprOp e1;
    private ExprOp e2;
    private static final String operation = "<";

    public LtOp(ExprOp e1, ExprOp e2) {
        super(e1,e2);
    }

    @Override
    public String toString() {
        return "LtOp{" +
                "e1=" + super.getE1() +
                ", e2=" + super.getE2() +
                '}';
    }
}
