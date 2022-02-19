package Statement;

import Node.VarDeclListOp;
import Operation.ExprOp;
import Scope.Record;
import Visitor.Visitor;
import Visitor.XmlGenerator;

import java.util.LinkedHashMap;

public class IfStatOp extends Statement{

    private ExprOp e;
    private StatListOp statList;
    private VarDeclListOp vars;
    private ElseOp elseStat;
    private LinkedHashMap<String, Record> table= new LinkedHashMap<>();

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

    public void setTable(LinkedHashMap<String, Record> table) {
        this.table = table;
    }
    public LinkedHashMap<String, Record> getTable() {
        return table;
    }
}
