package Operation;

import Statement.Statement;
import Visitor.Visitor;
import Node.Id;

public class ExprOp {

    private Object var;
    private Operations operation;
    private Statement statement;

    public String getOut() {
        return out;
    }

    private String out;

    public ExprOp(Operations operation) {
        this.operation = operation;
    }
    public ExprOp(Statement statement) {
        this.statement = statement;
    }
    public ExprOp(int anIntConst) {
        this.var  = anIntConst;
    }

    public ExprOp(boolean aBooleanConst) {
        this.var = aBooleanConst;
    }

    public ExprOp(Id id) {
        this.var = id;
    }

    public ExprOp(String out,Id id) {
        this.out = out;
        this.var = id;
    }

    public ExprOp(String aString) {
        this.var = aString;
    }

    public ExprOp(float aFloatConst) {
        this.var = aFloatConst;
    }

    public Operations getOperation() {
        return operation;
    }

    public Object getVar() {
        return var;
    }

    public Statement getStatement() {
        return statement;
    }

    public Object accept(Visitor v){
        return v.visit(this);
    }

    public String getType(){
        if(var != null) {
            String regex = "(\\.)";
            String type = var.getClass().toString();
            //System.out.println(var + " -> " + type);
            for (String s : type.split(regex)) {
                type = s;
            }if (type.equals("Float")){
                type = "real_const";
            } else if (!type.equals("Id") && !type.equals("Null")) {
                type += "_const";
            }
            return type;
        }
        else {
            return "var is null";
        }
    }

    @Override
    public String toString() {

        return
                (var != null ? getType()+ " "+ var : "")+
                (operation != null ?  operation : "");
    }
}
