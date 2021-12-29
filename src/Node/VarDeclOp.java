package Node;

import Visitor.Visitor;

public class VarDeclOp {

    private String t;
    private TypeOp tipo;
    private IdListInitOp list;

    public VarDeclOp(TypeOp t, IdListInitOp list) {
        this.tipo = t;
        this.list = list;
    }

    public String getT() {
        return t;
    }

    public VarDeclOp(String t, IdListInitOp list) {
        this.t=t;
        this.list = list;
    }

    public TypeOp getTipo() {
        return tipo;
    }

    public IdListInitOp getList() {
        return list;
    }

    @Override
    public String toString()
    {
        if(tipo != null)
            return tipo + " " +list;
        else
            return t + " " + list;
    }

    public Object accept(Visitor visitor) {
        return visitor.visit(this);
    }
}