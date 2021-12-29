package Statement;

import Visitor.Visitor;
import Visitor.XmlGenerator;

import java.util.ArrayList;

public class StatListOp {
    private ArrayList statements;

    public StatListOp(ArrayList<StatOp> statements) {
        this.statements = statements;
    }

    public StatListOp() {
        statements = new ArrayList<StatOp>();
    }

    public StatListOp add(StatOp statement){
        statements.add(0,statement);
        return this;
    }

    public ArrayList<StatOp> getStatements() {
        return statements;
    }

    @Override
    public String toString() {
        return
                "statementList " + statements.toString();

    }

    public Object accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
