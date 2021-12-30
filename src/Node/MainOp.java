package Node;

import Statement.StatListOp;
import Visitor.Visitor;
import Scope.Record;
import java.util.LinkedHashMap;

public class MainOp {

    private VarDeclListOp varDeclOpList;
    private StatListOp stats;

    private LinkedHashMap<String, Record> globalTable = new LinkedHashMap<>();

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

    public LinkedHashMap<String, Record> getGlobalTable() {
        return globalTable;
    }

    public void setGlobalTable(LinkedHashMap<String, Record> globalTable) {
        this.globalTable = globalTable;
    }


}
