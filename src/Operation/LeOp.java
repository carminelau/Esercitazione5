package Operation;

public class LeOp extends Operations {

    private static final String operation = "<=";

    public LeOp(ExprOp e1, ExprOp e2) {
        super(e1,e2);
    }

    @Override
    public String toString() {
        return "LeOp{" +
                "e1=" + super.getE1() +
                ", e2=" + super.getE2() +

                '}';
    }

}
