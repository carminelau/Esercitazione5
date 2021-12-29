package Operation;

public class FunctionParamOp extends Operations{

    public FunctionParamOp(ExprOp e1) {
        super(e1);
    }

    @Override
    public String toString() {
        return "FunctionParamOp{" +
                "e1=" + super.getE1() +
                ", e2=" + super.getE2() +
                '}';
    }
}
