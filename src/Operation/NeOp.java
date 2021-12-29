package Operation;

public class NeOp extends Operations {


    private static final String operation = "<>"; // !=

    public NeOp(ExprOp e1, ExprOp e2) {
        super(e1,e2);
    }

    @Override
    public String toString() {
        return "OrOp{" +
                "e1=" + super.getE1() +
                ", e2=" + super.getE2() +
                '}';
    }
}
