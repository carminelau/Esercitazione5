package Statement;
import Node.Id;
import Operation.*;
import Visitor.Visitor;

public class AssignStatOp extends Statement {
    private Id id;
    private ExprOp expr;
    private static final String operation = ":=";

    public AssignStatOp(Id id, ExprOp expr) {
        this.id = id;
        this.expr = expr;
    }

    public Object accept(Visitor visitor) {
        return visitor.visit(this);
    }


    public Id getId() {
        return id;
    }

    public ExprOp getExpr() {
        return expr;
    }

    public static String getOperation() {
        return operation;
    }

    @Override
    public String toString() {
        return "Assign{" +
                "id=" + id +
                ", expr=" + expr +
                '}';
    }
}
