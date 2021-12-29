package Node;

import Statement.StatListOp;
import Visitor.Visitor;
import Scope.Record;

import java.util.LinkedHashMap;

public class ProcOp {
    private Id id;
    private ParamDeclListOp list;
    private TypeOp t;
    private VarDeclListOp vars;
    private StatListOp stats;

    private LinkedHashMap<String, Record> table = new LinkedHashMap<String, Record>();

    public ProcOp(Id id, ParamDeclListOp paramList, TypeOp t, VarDeclListOp vars, StatListOp stats) {
        this.id = id;
        this.list = paramList;
        this.t = t;
        this.vars = vars;
        this.stats = stats;
    }

    public ProcOp(Id id, ParamDeclListOp paramList, VarDeclListOp vars, StatListOp stats) {
        this.id = id;
        this.list = paramList;
        this.vars = vars;
        this.stats = stats;
    }


    public Object accept(Visitor visitor) { return visitor.visit(this); }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public ParamDeclListOp getList() {
        return list;
    }

    public void setList(ParamDeclListOp list) {
        this.list = list;
    }

    public TypeOp getT() {
        return t;
    }

    public void setT(TypeOp t) {
        this.t = t;
    }

    public VarDeclListOp getVars() {
        return vars;
    }

    public void setVars(VarDeclListOp vars) {
        this.vars = vars;
    }

    public StatListOp getStats() {
        return stats;
    }

    public void setStats(StatListOp stats) {
        this.stats = stats;
    }

    public void setTable(LinkedHashMap<String,Record> table) {
        this.table = table;
    }
    public LinkedHashMap<String, Record> getTable() {
        return table;
    }

    @Override
    public String toString() {
        String pippo= "ProcOp{" +
                "id=" + id +
                ", list=" + list;
                if (t != null){
                    pippo += ", t=" + t;
                }
                pippo += ", vars=" + vars +
                ", stats=" + stats +
                '}';
                return  pippo;
    }
}
