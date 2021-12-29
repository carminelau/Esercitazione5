package Node;

import Operation.ExprOp;
import Visitor.Visitor;

import java.util.HashMap;

public class IdListInitOp {
    private HashMap<String, ExprOp> list;

    public IdListInitOp(HashMap<String, ExprOp> list) {
        this.list = list;

    }



    public IdListInitOp put(String id,ExprOp e){
        this.list.put(id,e);
        return this;
    }

    public HashMap<String, ExprOp> getList() {
        return list;
    }

    @Override
    public String toString() {
        return list.toString() ;
    }

    public Object accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
