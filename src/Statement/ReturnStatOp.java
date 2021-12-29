package Statement;

import Operation.ExprOp;
import Operation.Operations;
import Visitor.Visitor;

public class ReturnStatOp extends Statement{

    private ExprOp expr;

    public ReturnStatOp(ExprOp e) {
        this.expr=e;
    }

    public ExprOp getExpr() {
        return expr;
    }

    public Object accept(Visitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ReturnStatOp{" +
                "expr=" + expr +
                '}';
    }
}
