package Node;

import Visitor.Visitor;
import Visitor.XmlGenerator;

import java.util.ArrayList;

public class ProcListOp {
    private ArrayList<ProcOp> list;

    public ProcListOp(ArrayList<ProcOp> list) {
        this.list = list;
    }

    public ProcListOp add(ProcOp p){
        this.list.add(0,p);
        return this;
    }

    @Override
    public String toString() {
        return list.toString();

    }

    public ArrayList<ProcOp> getList() {
        return list;
    }

    public Object accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
