package Operation;


public class AddOp extends Operations{

    public AddOp(ExprOp e1, ExprOp e2) {
        super(e1,e2);
    }

    @Override
    public String toString() {
        return "AddOp{" +
                "e1=" + super.getE1() +
                ", e2=" + super.getE2() +
                '}';
    }
}
