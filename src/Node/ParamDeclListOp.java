package Node;

import Visitor.Visitor;
import Visitor.XmlGenerator;

import java.util.ArrayList;

public class ParamDeclListOp {
    private ArrayList<ParDeclOp> list;

    public ParamDeclListOp(ArrayList<ParDeclOp> list) {
        this.list = list;
    }

    public ParamDeclListOp add(ParDeclOp p){
        list.add(p);
        return this;
    }

    public ArrayList<ParDeclOp> getList() {
        return list;
    }

    @Override
    public String toString() {
        return  list.toString();

    }

    public Object accept(Visitor visitor) {
        return visitor.visit(this);
    }

}
