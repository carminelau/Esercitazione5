package Statement;

import Operation.ExprOp;
import Visitor.Visitor;
import Visitor.XmlGenerator;

public class StatOp extends Statement{
    private Statement statement;
    private ExprOp expr;

    public StatOp(Statement statement) {
        this.statement = statement;
    }

    public Statement getStatement() {
        return statement;
    }

    @Override
    public String toString() {
        if (statement != null) {
            return statement.toString();
        }
        return "Statement Null";
    }

    public Object accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
