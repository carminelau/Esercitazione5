package Operation;

import Visitor.Visitor;

public class Operations {
    private ExprOp e1,e2;

    public Operations(ExprOp e1, ExprOp e2){
        this.e1 = e1;
        this.e2 = e2;
    }
    public Object accept(Visitor visitor){
        return visitor.visit(this);
    }

    public Operations(ExprOp e1){
        this.e1 = e1;
    }

    public ExprOp getE1() {
        return e1;
    }

    public ExprOp getE2() {
        return e2;
    }
}
