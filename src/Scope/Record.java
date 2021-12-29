package Scope;

import java.util.ArrayList;
import java.util.Objects;

public class Record {
    private String sym;
    private String kind;
    private String type;
    private ArrayList<String> returnType;
    private ArrayList<String> paramType;

    public Record(String sym, String kind, String type){
        this.sym = sym;
        this.type = type;
        this.kind = kind;
        returnType  = null;
    }

    public Record(String sym, String kind, String type,ArrayList<String> returnType,ArrayList<String> paramType){
        this.sym = sym;
        this.type = type;
        this.kind = kind;
        this.returnType  = returnType;
        this.paramType = paramType;
    }

    public Record(){

    }

    public String getSym() {
        return sym;
    }

    public String getType() {
        return type;
    }

    public ArrayList<String> getParamType() {
        return paramType;
    }

    public ArrayList<String> getReturnType() {
        return returnType;
    }

    public String getKind() {
        return kind;
    }

    public void setSym(String sym) {
        this.sym = sym;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setReturnType(ArrayList<String> returnType) {
        this.returnType = returnType;
    }

    public void setParamType(ArrayList<String> paramType) {
        this.paramType = paramType;
    }

    public void addReturnType(String t){
        this.returnType.add(t);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Record record = (Record) o;
        return sym.equals(record.sym);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sym);
    }

    @Override
    public String toString() {
        return (kind.equals("var") ?
                "Record{" +
                        "sym='" + sym + '\'' +
                        ", kind='" + kind + '\'' +
                        ", type=" + type +
                        "}\n" : "Record{" +
                "sym='" + sym + '\'' +
                ", kind='" + kind + '\'' + ", type=" + returnType+"\n");

    }
}
