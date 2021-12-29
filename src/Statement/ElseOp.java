package Statement;

import Node.VarDeclListOp;
import Visitor.Visitor;
import Visitor.XmlGenerator;

public class ElseOp {
    private StatListOp statList;
    private VarDeclListOp vars;

    public ElseOp(VarDeclListOp vars,StatListOp list) {
        this.statList = list;
        this.vars=vars;
    }

    @Override
    public String toString() {
        return "ElseOp{" +
                "Vardecllist=" + vars +
                "statList=" + statList +
                '}';
    }

    public VarDeclListOp getVars() {
        return vars;
    }

    public StatListOp getStatList() {
        return statList;
    }

    public Object accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
