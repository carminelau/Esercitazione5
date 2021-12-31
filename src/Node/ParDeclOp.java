package Node;

import Statement.IdListOp;
import Visitor.Visitor;
import Visitor.XmlGenerator;
import org.w3c.dom.Node;

public class ParDeclOp {
    public String getOut() {
        return out;
    }

    private String out;
    private TypeOp t;
    private Id id;

    public ParDeclOp(TypeOp t, Id id) {
        this.id = id;
        this.t = t;
    }

    public ParDeclOp(String out,TypeOp t, Id id) {
        this.id = id;
        this.t = t;
        this.out = out;
    }

    public ParDeclOp(TypeOp t) {
        this.t = t;
    }

    public TypeOp getT() {
        return t;
    }

    public Id getId() {
        return id;
    }

    @Override
    public String toString() {
        return t + "" + id;
    }

    public Object accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
