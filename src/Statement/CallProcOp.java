package Statement;

import Operation.*;
import Statement.Statement;
import Visitor.Visitor;

public class CallProcOp extends Statement {

    private ExprListOp exprList;
    private String id;

    public CallProcOp(String id) {
        this.id = id;
    }

    public CallProcOp(String id, ExprListOp exprList) {
        this.exprList = exprList;
        this.id = id;
    }


    public ExprListOp getExprList() {
        return exprList;
    }

    @Override
    public String toString() {
        return "CallProcOp{" +
                "exprList=" + exprList +
                ", id='" + id + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public Object accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
