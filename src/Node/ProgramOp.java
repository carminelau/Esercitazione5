package Node;

import Visitor.Visitor;

public class ProgramOp {

    private VarDeclListOp varDeclOpList;
    private ProcListOp ProcOpList;
    private MainOp main;

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

    @Override
    public String toString() {
        return "ProgramOp{" +
                "varDeclOpList=" + varDeclOpList +
                ", ProcOpList=" + ProcOpList +
                ", main=" + main +
                '}';
    }
}
