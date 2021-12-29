package Node;

import Statement.StatListOp;
import Visitor.Visitor;

public class MainOp {

    private VarDeclListOp varDeclOpList;
    private StatListOp stats;


    public MainOp(VarDeclListOp varDeclOpList, StatListOp stats) {
        this.varDeclOpList = varDeclOpList;
        this.stats = stats;
    }

    @Override
    public String toString() {
        return "MainOp{" +
                "varDeclOpList=" + varDeclOpList +
                ", stats=" + stats +
                '}';
    }

    public VarDeclListOp getVarDeclOpList() {
        return varDeclOpList;
    }

    public StatListOp getStats() {
        return stats;
    }

    public Object accept(Visitor visitor) {
        return visitor.visit(this);
    }

}
