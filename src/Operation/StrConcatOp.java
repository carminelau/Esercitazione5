package Operation;

public class StrConcatOp extends Operations{

    private static final String operation = "&";

    public StrConcatOp(ExprOp e1, ExprOp e2) {
        super(e1,e2);
    }

    @Override
    public String toString() {
        return "StrConcatOp{" +
                "e1=" + super.getE1() +
                ", e2=" + super.getE2() +
                '}';
    }
}
