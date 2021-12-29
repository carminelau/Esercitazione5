package Operation;

public class DiffOp extends Operations {

    private static final String operation = "-";

    public DiffOp(ExprOp e1, ExprOp e2) {
        super(e1,e2);
    }

    public DiffOp(ExprOp e1){ super(e1);}

    @Override
    public String toString() {
        return "DiffOp{" +
                "e1=" + super.getE1() +
                ", e2=" + super.getE2() +
                '}';
    }
}
