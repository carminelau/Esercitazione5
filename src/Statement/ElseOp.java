package Statement;

import Node.VarDeclListOp;
import Scope.Record;
import Visitor.Visitor;
import Visitor.XmlGenerator;

import java.util.LinkedHashMap;

public class ElseOp {

    private static LinkedHashMap<String, Record> table= new LinkedHashMap<>();
    private StatListOp statList;
    private VarDeclListOp vars;

    public ElseOp(VarDeclListOp vars,StatListOp list) {
        this.statList = list;
        this.vars=vars;
    }

    public void setTable(LinkedHashMap<String, Record> table) {
        this.table = table;
    }
    public static LinkedHashMap<String, Record> getTable() {
        return table;
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
