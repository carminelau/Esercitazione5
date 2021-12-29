package Statement;

import Operation.ExprListOp;
import Operation.ExprOp;
import Visitor.Visitor;
import Visitor.XmlGenerator;

public class WriteStatOp extends Statement{
    private ExprOp expr;
    private String mode;

    public WriteStatOp(ExprOp expr, String mode) {
        this.expr = expr;
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "WriteStatOp{" +
                "expr=" + expr +
                "mode=" + mode +
                '}';
    }

    public ExprOp getExprList() {
        return expr;
    }

    public String getMode() { return mode;}

    public Object accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
