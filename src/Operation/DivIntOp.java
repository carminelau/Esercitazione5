package Operation;

public class DivIntOp extends Operations {


    private static final String operation = "div";

    public DivIntOp(ExprOp e1, ExprOp e2) {
        super(e1,e2);
    }

    @Override
    public String toString() {
        return "DivIntOp{" +
                "e1=" + super.getE1() +
                ", e2=" + super.getE2() +
                '}';
    }
}
