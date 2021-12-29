package Statement;

import Node.VarDeclListOp;
import Operation.ExprOp;
import Visitor.Visitor;
import Visitor.XmlGenerator;

public class WhileStatOp extends Statement {
    private StatListOp statListOp;
    private VarDeclListOp vars;
    private ExprOp e;

    public WhileStatOp(ExprOp e,VarDeclListOp vars,StatListOp statListOp) {
        this.statListOp = statListOp;
        this.vars=vars;
        this.e = e;
    }

    @Override
    public String toString() {
        return "WhileStatOp{" +
                "vardecllist" + vars +
                "statListOp=" + statListOp +
                ", e=" + e +
                '}';
    }

    public StatListOp getStatListOp() {
        return statListOp;
    }
    
    public VarDeclListOp getVarDeclList() {
        return vars;
    }
    
    public ExprOp getE() {
        return e;
    }

    public Object accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
