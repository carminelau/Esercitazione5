package Statement;

import Visitor.Visitor;

import java.util.ArrayList;

public class IdListOp {
    private ArrayList<String> idList;

    public IdListOp(ArrayList<String> idList) {
        this.idList = idList;
    }

    public ArrayList<String> getIdList() {
        return idList;
    }

    public IdListOp add(String id) {
        idList.add(id);
        return this;
    }

    @Override
    public String toString() {
        return idList.toString();
    }


    public Object accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
