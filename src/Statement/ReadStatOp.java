package Statement;

import Operation.ExprOp;
import Visitor.Visitor;

public class ReadStatOp extends Statement{

    private IdListOp idList;

    public ExprOp getExpr() {
        return expr;
    }

    private ExprOp expr;

    public ReadStatOp(IdListOp list) {
        this.idList = list;
    }

    public ReadStatOp(IdListOp list, ExprOp expr) {
        this.idList = list;
        this.expr =expr;
    }

    @Override
    public String toString() {
        return "ReadStatOp{" +
                "idList=" + idList +
                '}';
    }

    public IdListOp getIdList() {
        return idList;
    }

    public Object accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
