package Operation;

public class EqOp extends Operations {

    private static final String operation = "=";

    public EqOp(ExprOp e1, ExprOp e2) {
        super(e1,e2);
    }

    @Override
    public String toString() {
        return "EqOp{" +
                "e1=" + super.getE1()+
                ", e2=" + super.getE2() +
                '}';
    }
}
