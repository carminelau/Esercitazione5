package Node;

import Visitor.Visitor;

import java.util.ArrayList;

public class VarDeclListOp {

    private  ArrayList<VarDeclOp> list;

    public VarDeclListOp(ArrayList<VarDeclOp> list) {
        this.list = list;
    }

    public  VarDeclListOp add(VarDeclOp varDecl) {
        this.list.add(0,varDecl);
        return this;
    }

    public ArrayList<VarDeclOp> getList() {
        return list;
    }

    @Override
    public String toString() {
        return "VarDeclListOp{" +
                "list=" + list +
                '}';
    }

    public Object accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
