package Statement;

import Node.VarDeclListOp;
import Operation.ExprOp;
import Visitor.Visitor;
import Visitor.XmlGenerator;

public class IfStatOp extends Statement{

    private ExprOp e;
    private StatListOp statList;
    private VarDeclListOp vars;
    private ElseOp elseStat;

    public IfStatOp(ExprOp e, VarDeclListOp vars,StatListOp statList,ElseOp elseStat) {
        this.e = e;
        this.statList = statList;
        this.vars = vars;
        this.elseStat = elseStat;
    }

    public ExprOp getE() {
        return e;
    }

    public StatListOp getStatList() {
        return statList;
    }

    public VarDeclListOp getVars() {
        return vars;
    }

    public ElseOp getElseStat() {
        return elseStat;
    }

    @Override
    public String toString() {
        return "IfStatOp{" +
                 e +
                "," + statList +
                "," + vars +
                "," + elseStat +
                '}';
    }

    public Object accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
