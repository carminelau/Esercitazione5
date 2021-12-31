package Node;

import Visitor.Visitor;

public class TypeOp {
    private String tipo;

    public TypeOp(String tipo){
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
            return tipo;
    }

    public Object accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
