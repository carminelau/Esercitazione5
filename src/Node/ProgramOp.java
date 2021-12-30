package Node;

import Visitor.Visitor;
import Scope.Record;
import java.util.LinkedHashMap;

public class ProgramOp {

    private VarDeclListOp varDeclOpList;
    private ProcListOp ProcOpList;
    private MainOp main;

    private LinkedHashMap<String, Record> globalTable = new LinkedHashMap<>();


    public ProgramOp(VarDeclListOp varDeclOpList, ProcListOp procOpList, MainOp main) {
        this.varDeclOpList = varDeclOpList;
        this.ProcOpList = procOpList;
        this.main=main;
    }

    public Object accept(Visitor v){
        return v.visit(this);
    }

    public VarDeclListOp getVarDeclOpList() {
        return varDeclOpList;
    }

    public ProcListOp getProcOpList() {
        return ProcOpList;
    }

    public MainOp getMain() {
        return main;
    }

    public LinkedHashMap<String, Record> getGlobalTable() {
        return globalTable;
    }

    public void setGlobalTable(LinkedHashMap<String, Record> globalTable) {
        this.globalTable = globalTable;
    }

    @Override
    public String toString() {
        return "ProgramOp{" +
                "varDeclOpList=" + varDeclOpList +
                ", ProcOpList=" + ProcOpList +
                ", main=" + main +
                '}';
    }
}
