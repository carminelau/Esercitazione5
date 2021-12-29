package Operation;

public class PowOp extends Operations{

    private static final String operation = "^";

    public PowOp(ExprOp e1, ExprOp e2) {
        super(e1,e2);
    }

    @Override
    public String toString() {
        return "PowOp{" +
                "e1=" + super.getE1() +
                ", e2=" + super.getE2() +
                '}';
    }
}
