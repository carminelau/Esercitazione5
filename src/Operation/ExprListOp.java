package Operation;

import Visitor.Visitor;
import Visitor.XmlGenerator;

import java.util.ArrayList;

public class ExprListOp {
    private  ArrayList<ExprOp> exprlist;

    public ExprListOp(ArrayList<ExprOp> exprlist) {
        this.exprlist = exprlist;
    }

    public ExprListOp add(ExprOp e){
        exprlist.add(0,e);
        return this;
    }

    public ArrayList<ExprOp> getExprlist() {
        return exprlist;
    }

    @Override
    public String toString() {
        return exprlist.toString();
    }

    public Object accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
