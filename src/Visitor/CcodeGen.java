package Visitor;

import Node.*;
import Operation.*;
import Scope.Record;
import Statement.*;
import Scope.*;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Map;

public class CcodeGen implements Visitor {

    private int idx;
    private int callProcCount = 0;
    private StringBuilder codeBuffer;
    private TypeEnviroment typeEnvirornment;

    public StringBuilder getCodeBuffer() {
        return codeBuffer;
    }

    public CcodeGen() {
        codeBuffer = new StringBuilder();
        this.typeEnvirornment = new TypeEnviroment();
    }

    public void insertCodeCallProc(CallProcOp c) {

        codeBuffer.insert(idx, c.getId() + "_s ");

        idx = idx + c.getId().length() + 3;
        codeBuffer.insert(idx, "new_" + callProcCount + " = " + c.getId() + "(");
        idx += 8 + c.getId().length() + String.valueOf(callProcCount).length();

    }
    boolean addCode = false;
    public void addCode(CallProcOp c) {

        int i = 1;

        for (ExprOp e : c.getExprList().getExprlist()) {
            if (e.getStatement() != null && e.getStatement() instanceof CallProcOp c1) {
                ArrayList<String> types = typeEnvirornment.lookup(c1.getId()).getReturnType();
                if (types.size() > 1) {
                    insertCodeCallProc(c1);
                    callProcCount++;
                    addCode(c1);

                    insertCodeCallProc(c);
                    addCode = true;
                    int preCallCount = callProcCount - 1;
                    int z = 1;
                    for (int j = 0; j < types.size(); j++) {
                        codeBuffer.insert(idx, "new_" + preCallCount + ".var_" + j);
                        idx = idx + 9 + String.valueOf(preCallCount).length() + String.valueOf(j).length();
                        if (z < types.size()) {
                            codeBuffer.insert(idx, ",");
                            z++;
                            idx++;
                        }
                    }
                } else {
                    c1.accept(this);
                }
            } else if (e.getVar() != null && e.getVar() instanceof Id id) {
                if (!addCode) {
                    callProcCount++;
                    insertCodeCallProc(c);
                    addCode = true;
                }
                codeBuffer.insert(idx, id.toString());
                idx += id.toString().length();
                if (i < c.getExprList().getExprlist().size()) {
                    codeBuffer.insert(idx, ",");
                    i++;
                    idx++;
                }
            } else if (e.getOperation() != null) {
                e.getOperation().accept(this);
            } else {
                if (!addCode) {
                    addCode = true;
                    callProcCount++;
                    insertCodeCallProc(c);
                }
                codeBuffer.insert(idx, e.getVar());
                idx += String.valueOf(e.getVar()).length();
                if (i < c.getExprList().getExprlist().size()) {
                    codeBuffer.insert(idx, ",");
                    i++;
                    idx++;
                }
            }
        }
        addCode = false;
        codeBuffer.insert(idx, ");\n");
        idx += 3;
    }

    private String getTypeInC(String type) {
        if (type.contains("integer")) {
            return "int";
        } else if (type.contains("bool")) {
            return "bool";
        } else if (type.contains("real")) {
            return "float";
        } else if (type.contains("string")) {
            return "char*";
        }
        return null;
    }

    private void defStruct(ArrayList<String> types, String name) {
        //definisco le strutture per le funzioni con più valori di ritorno
        String var = "var";
        int i = 0;

        codeBuffer.append("typedef struct { \n");
        for (String type : types) {
            codeBuffer.append(getTypeInC(type)).append(" ").append(var).append("_").append(i).append(";\n");
            i++;
        }
        codeBuffer.append("}").append(name).append("_s;\n\n");
    }

    public void getStrcmp(String s, Id id2) {
        codeBuffer.insert(idx, "strcmp(\"");
        idx += 8;
        codeBuffer.insert(idx, s);
        idx += s.length();
        codeBuffer.insert(idx, "\",");
        idx += 2;
        codeBuffer.insert(idx, id2);
        idx += id2.toString().length();
        codeBuffer.insert(idx, ")");
        idx++;

    }

    public void getStrcmp(Id id1, Id id2) {
        codeBuffer.insert(idx, "strcmp(");
        idx += 7;
        codeBuffer.insert(idx, id1);
        idx += id1.toString().length();
        codeBuffer.insert(idx, ",");
        idx++;
        codeBuffer.insert(idx, id2);
        idx += id2.toString().length();
        codeBuffer.insert(idx, ")");
        idx++;
    }

    public void getStrcmp(Id id1, String s) {

        codeBuffer.insert(idx, "strcmp(");
        idx += 7;
        codeBuffer.insert(idx, id1);
        idx += id1.toString().length();
        codeBuffer.insert(idx, ",\"");
        idx += 2;
        codeBuffer.insert(idx, s);
        idx += s.length();
        codeBuffer.insert(idx, "\")");
        idx += 2;
    }

    @Override
    public Object visit(ProgramOp programOp) {
        this.codeBuffer.append("#include <stdio.h>\n"
                + "#include <string.h> \n"
                + "#include <malloc.h> \n"
                + "#include <stdbool.h> \n\n");

        typeEnvirornment.enterScope(programOp.getGlobalTable());
        //Dichiarazioni di funzioni
        for (Map.Entry<String, Record> entry : programOp.getGlobalTable().entrySet()) {
            if (entry.getValue().getKind().equals("method") && !entry.getKey().equals("main")) {
                if (entry.getValue().getReturnType().size() > 1) {
                    defStruct(entry.getValue().getReturnType(), entry.getKey());
                    codeBuffer.append(entry.getKey()).append("_s ");
                } else {
                    codeBuffer.append(getTypeInC(entry.getValue().getReturnType().get(0))).append(" ");
                }
                if (entry.getValue().getParamType().get(0).equals("void")) {
                    codeBuffer.append(entry.getKey()).append("(").append(getTypeInC(entry.getValue().getParamType().get(0))).append(");")
                            .append("\n\n");
                } else if (entry.getValue().getParamType().size() == 1) {
                    codeBuffer.append(entry.getKey()).append("(");
                    codeBuffer.append(getTypeInC(entry.getValue().getParamType().get(0)));
                    codeBuffer.append(");").append("\n\n");
                } else {
                    int i = 1;
                    codeBuffer.append(entry.getKey()).append("(");
                    for (String type : entry.getValue().getParamType()) {
                        codeBuffer.append(getTypeInC(type));
                        if (i != entry.getValue().getParamType().size()) {
                            codeBuffer.append(",");
                        }
                        i++;
                    }
                    codeBuffer.append(");").append("\n\n");
                }

            }
        }
        programOp.getVarDeclOpList().accept(this);
        programOp.getProcOpList().accept(this);
        System.out.println(codeBuffer);
        typeEnvirornment.exitScope();
        return null;
    }

    @Override
    public Object visit(ProcListOp procListOp) {
        for (ProcOp procOp : procListOp.getList()) {
            procOp.accept(this);
        }
        return null;
    }

    @Override
    public Object visit(ParamDeclListOp paramDeclListOp) {
        int i = 1;
        for (ParDeclOp parDeclOp : paramDeclListOp.getList()) {
            if (parDeclOp.getT().getTipo().equals("void")) {
                codeBuffer.append(getTypeInC(parDeclOp.getT().getTipo()));
            }
            else if (paramDeclListOp.getList().size() == 1) {
                // ho un solo elemento nella lista ---> fun(int a,b);
                String id = parDeclOp.getId().toString();
                codeBuffer.append(getTypeInC(parDeclOp.getT().getTipo())).append(" ");
                codeBuffer.append(id);
                //codeBuffer.append("){\n");
            } else {
                //Ho più elementi nella lista ----> fun(int a; int b,c);
                String id = parDeclOp.getId().toString();
                codeBuffer.append(getTypeInC(parDeclOp.getT().getTipo())).append(" ");

                if (i < paramDeclListOp.getList().size()) {
                    codeBuffer.append(id).append(",");
                    i++;
                } else {
                    codeBuffer.append(id);
                }
            }
        }
        codeBuffer.append("){\n");
        return null;
    }

    @Override
    public Object visit(IdListOp idListOp) {
        return null;
    }

    @Override
    public Object visit(AssignStatOp assignStatOp) {
        int returnCount = 0;
        boolean multiCall = false;
        ExprOp exprOp = assignStatOp.getExpr();
        if (exprOp.getStatement() != null && exprOp.getStatement() instanceof CallProcOp callProcOp) {
            if (typeEnvirornment.lookup(callProcOp.getId()).getReturnType().size() > 1) {
                codeBuffer.append(callProcOp.getId()).append("_s new_").append(callProcCount).append(" = ")
                        .append(callProcOp.getId());
            }
            codeBuffer.append("(");
            if (callProcOp.getExprList() != null) {
                int j = 1;
                for (ExprOp e : callProcOp.getExprList().getExprlist()) {
                    if (e.getVar() != null) {
                        if (j < callProcOp.getExprList().getExprlist().size()) {
                            if (e.getType().contains("String")) {
                                codeBuffer.append("\"");
                                codeBuffer.append(e.getVar()).append("\"").append(",");
                            } else
                                codeBuffer.append(e.getVar()).append(",");

                            j++;
                        } else {
                            if (e.getType().contains("String")) {
                                codeBuffer.append("\"");
                                codeBuffer.append(e.getVar()).append("\");\n");
                            } else {
                                codeBuffer.append(e.getVar()).append(");\n");
                            }
                        }
                    } else if (e.getOperation() != null) {
                        idx = codeBuffer.length();
                        e.getOperation().accept(this);
                        if (j < callProcOp.getExprList().getExprlist().size()) {
                            codeBuffer.append(",");
                            j++;
                        } else {
                            codeBuffer.append(");\n");
                        }
                    } else if (e.getStatement() != null && e.getStatement() instanceof CallProcOp c) {

                        ArrayList<String> types = typeEnvirornment.lookup(c.getId()).getReturnType();
                        if(types.size()>1){
                            idx = codeBuffer.lastIndexOf("\n");
                            codeBuffer.insert(idx,"\n");
                            idx++;
                            addCode(c);
                            multiCall = true;
                            int i = 0;
                            for(String ignored : types){
                                codeBuffer.append("new_").append(callProcCount).append(".var_").append(i);
                                if(i<types.size()-1){
                                    codeBuffer.append(",");
                                }
                                i++;

                            }
                        }else {
                            c.accept(this);
                            codeBuffer.deleteCharAt(codeBuffer.length() - 1);
                            codeBuffer.deleteCharAt(codeBuffer.length() - 1);
                        }
                        if (j < callProcOp.getExprList().getExprlist().size()) {
                            codeBuffer.append(",");
                            j++;
                        } else {
                            codeBuffer.append(");\n");
                        }
                    }
                }
            } else {
                codeBuffer.append(");\n");
            }
            for (String ignored : typeEnvirornment.lookup(callProcOp.getId()).getReturnType()) {
                if (typeEnvirornment.lookup(callProcOp.getId()).getReturnType().size() > 1) {
                    int newCallcount;
                    if(multiCall) {
                        newCallcount = callProcCount - 1;
                    }else{
                        newCallcount = callProcCount;
                    }
                    codeBuffer.append(assignStatOp.getId()).append(" = ")
                            .append("new_").append(newCallcount).append(".var_").append(returnCount).append(";\n");
                }
                returnCount++;
            }
            if (typeEnvirornment.lookup(callProcOp.getId()).getReturnType().size() > 1)
                callProcCount++;
        } else if (exprOp.getVar() instanceof Id id) {
            codeBuffer.append(assignStatOp.getId()).append(" = ").append(id).append(";\n");
        } else if (exprOp.getOperation() != null) {
            codeBuffer.append(assignStatOp.getId()).append(" = ");
            idx = codeBuffer.length();
            exprOp.getOperation().accept(this);
            codeBuffer.append(";\n");
        } else {
            if (exprOp.getVar() instanceof String) {
                codeBuffer.append(assignStatOp.getId()).append(" = \"").
                        append(exprOp.getVar()).append("\";\n");
            } else
                codeBuffer.append(assignStatOp.getId()).append(" = ").
                        append(exprOp.getVar()).append(";\n");
        }

        return null;
    }

    @Override
    public Object visit(CallProcOp callProcOp) {
        codeBuffer.append(callProcOp.getId()).append("(");

        if (callProcOp.getExprList() != null) {
            int i = 1;
            for (ExprOp e : callProcOp.getExprList().getExprlist()) {
                if (e.getOperation() != null) {
                    codeBuffer.append(callProcOp.getId()).append("(");
                    idx = codeBuffer.length();
                    e.getOperation().accept(this);
                    if (i < callProcOp.getExprList().getExprlist().size()) {
                        codeBuffer.append(",");
                        i++;
                    }
                } else if (e.getVar() != null && e.getVar() instanceof Id id) {
                    codeBuffer.append(id);
                    if (i < callProcOp.getExprList().getExprlist().size()) {
                        codeBuffer.append(",");
                        i++;
                    }
                } else if (e.getStatement() != null && e.getStatement() instanceof CallProcOp c) {
                    ArrayList<String> types = typeEnvirornment.lookup(c.getId()).getReturnType();
                    if (types.size() > 1) {
                        addCode(c);
                        int z = 1;
                        for (int j = 0; j < types.size(); j++) {
                            codeBuffer.append("new_").append(callProcCount).append(".var_").append(j);
                            if (z < types.size()) {
                                codeBuffer.append(",");
                                z++;
                            }
                        }
                    } else {
                        c.accept(this);
                        codeBuffer.deleteCharAt(codeBuffer.length() - 1);
                        codeBuffer.deleteCharAt(codeBuffer.length() - 1);
                        codeBuffer.deleteCharAt(codeBuffer.length() - 1);
                        codeBuffer.append(")");
                    }
                    if (i < callProcOp.getExprList().getExprlist().size()) {
                        codeBuffer.append(",");
                        i++;
                    }
                } else {
                    if (e.getType().contains("String")) {
                        codeBuffer.append("\"");
                    }
                    codeBuffer.append(e.getVar());
                    if (e.getType().contains("String")) {
                        codeBuffer.append("\"");
                    }
                    if (i < callProcOp.getExprList().getExprlist().size()) {
                        codeBuffer.append(",");
                        i++;
                    }
                }
            }
        }
        codeBuffer.append(");\n");
        return null;
    }

    @Override
    public Object visit(MainOp main) {
        return null;
    }

    @Override
    public Object visit(StatListOp statListOp) {
        for (StatOp statOp : statListOp.getStatements()) {

            statOp.accept(this);
        }
        return null;
    }

    @Override
    public Object visit(ElseOp elseOp) {
        codeBuffer.append("else{\n");

        elseOp.getStatList().accept(this);
        codeBuffer.append("}\n");
        return null;
    }

    @Override
    public Object visit(Operations operation) {
        ExprOp e1 = operation.getE1();
        ExprOp e2 = operation.getE2();

        if (operation instanceof AddOp) {
            e1.accept(this);
            codeBuffer.insert(idx, "+");
            idx++;
            e2.accept(this);
        } else if (operation instanceof DiffOp) {
            e1.accept(this);
            codeBuffer.insert(idx, "-");
            idx++;
            e2.accept(this);
        } else if (operation instanceof MulOp) {
            e1.accept(this);
            codeBuffer.insert(idx, "*");
            idx++;
            e2.accept(this);
        } else if (operation instanceof DivOp) {
            e1.accept(this);
            codeBuffer.insert(idx, "/");
            idx++;
            e2.accept(this);
        } else if (operation instanceof AndOp) {
            e1.accept(this);
            codeBuffer.insert(idx, "&&");
            idx += 2;
            e2.accept(this);
        } else if (operation instanceof OrOp) {
            e1.accept(this);
            codeBuffer.insert(idx, "||");
            idx += 2;
            e2.accept(this);
        } else if (operation instanceof GtOp) {

            if (e1.getVar() instanceof String s1 && e2.getVar() instanceof String s2) {
                /*
                    if Return value < 0 then it indicates str1 is less than str2.
                    if Return value > 0 then it indicates str2 is less than str1.
                    if Return value = 0 then it indicates str1 is equal to str2.

                    int strcmp(const char *str1, const char *str2)
                */

                if (s1.length() > s2.length()) {
                    codeBuffer.insert(idx, "true");
                    idx++;
                } else {
                    codeBuffer.insert(idx, "false");
                    idx++;
                }
            } else if (e1.getVar() instanceof Id id1 && e2.getVar() instanceof String s2) {
                getStrcmp(id1, s2);
                codeBuffer.insert(idx, ">0");
                idx += 2;
            } else if (e2.getVar() instanceof Id id2 && e1.getVar() instanceof String s1) {
                getStrcmp(s1, id2);
                codeBuffer.insert(idx, ">0");
                idx += 2;
            } else {
                String typeId1;
                if (e1.getVar() instanceof Id id1 && e2.getVar() instanceof Id id2) {
                    typeId1 = typeEnvirornment.lookup(id1.toString()).getType();
                    if (typeId1.equals("string")) {
                        getStrcmp(id1, id2);
                        codeBuffer.insert(idx, ">0");
                        idx += 2;
                    } else {
                        e1.accept(this);
                        codeBuffer.insert(idx, ">");
                        idx++;
                        e2.accept(this);
                    }
                } else {
                    e1.accept(this);
                    codeBuffer.insert(idx, ">");
                    idx++;
                    e2.accept(this);
                }
            }
        } else if (operation instanceof GeOp) {
            if (e1.getVar() instanceof String s1 && e2.getVar() instanceof String s2) {
                if (s1.length() >= s2.length()) {
                    codeBuffer.insert(idx, "true");
                    idx += 4;
                } else {
                    codeBuffer.insert(idx, "false");
                    idx += 5;
                }
            } else if (e1.getVar() instanceof Id id1 && e2.getVar() instanceof String s2) {
                getStrcmp(id1, s2);
                codeBuffer.insert(idx, ">= 0");
                idx += 2;
            } else if (e2.getVar() instanceof Id id2 && e1.getVar() instanceof String s1) {
                getStrcmp(s1, id2);
                codeBuffer.insert(idx, ">= 0");
                idx += 2;
            } else {
                String typeId1;
                if (e1.getVar() instanceof Id id1 && e2.getVar() instanceof Id id2) {
                    typeId1 = typeEnvirornment.lookup(id1.toString()).getType();
                    if (typeId1.equals("string")) {
                        getStrcmp(id1, id2);
                        codeBuffer.insert(idx, ">= 0");
                        idx += 2;
                    } else {
                        e1.accept(this);
                        codeBuffer.insert(idx, ">=");
                        idx += 2;
                        e2.accept(this);
                    }
                } else {
                    e1.accept(this);
                    codeBuffer.insert(idx, ">=");
                    idx += 2;
                    e2.accept(this);
                }
            }
        } else if (operation instanceof LtOp) {
            if (e1.getVar() instanceof String s1 && e2.getVar() instanceof String s2) {
                if (s1.length() < s2.length()) {
                    codeBuffer.insert(idx, "true");
                    idx += 4;
                } else {
                    codeBuffer.insert(idx, "false");
                    idx += 5;
                }
            } else {
                String typeId1;
                if (e1.getVar() instanceof Id id1 && e2.getVar() instanceof Id id2) {
                    typeId1 = typeEnvirornment.lookup(id1.toString()).getType();
                    if (typeId1.equals("string")) {
                        getStrcmp(id1, id2);
                        codeBuffer.insert(idx, "<0");
                        idx += 2;
                    } else {
                        e1.accept(this);
                        codeBuffer.insert(idx, "<");
                        idx++;
                        e2.accept(this);
                    }
                } else {
                    e1.accept(this);
                    codeBuffer.insert(idx, "<");
                    idx++;
                    e2.accept(this);
                }
            }
        } else if (operation instanceof LeOp) {
            if (e1.getVar() instanceof String s1 && e2.getVar() instanceof String s2) {
                if (s1.length() <= s2.length()) {
                    codeBuffer.insert(idx, "true");
                    idx += 4;
                } else {
                    codeBuffer.insert(idx, "false");
                    idx += 5;
                }
            } else if (e1.getVar() instanceof Id id1 && e2.getVar() instanceof String s2) {
                getStrcmp(id1, s2);
                codeBuffer.insert(idx, "<=0");
                idx += 3;
            } else if (e2.getVar() instanceof Id id2 && e1.getVar() instanceof String s1) {
                getStrcmp(s1, id2);
                codeBuffer.insert(idx, "<=0");
                idx += 3;
            } else {
                String typeId1;
                if (e1.getVar() instanceof Id id1 && e2.getVar() instanceof Id id2) {
                    typeId1 = typeEnvirornment.lookup(id1.toString()).getType();
                    if (typeId1.equals("string")) {
                        getStrcmp(id1, id2);
                        codeBuffer.insert(idx, "<=0");
                        idx += 3;
                    } else {
                        e1.accept(this);
                        codeBuffer.insert(idx, "<=");
                        idx += 2;
                        e2.accept(this);
                    }
                } else {
                    e1.accept(this);
                    codeBuffer.insert(idx, "<=");
                    idx += 2;
                    e2.accept(this);
                }
            }
        } else if (operation instanceof EqOp) {
            if (e1.getVar() instanceof String s1 && e2.getVar() instanceof String s2) {
                if (s1.length() >= s2.length()) {
                    codeBuffer.insert(idx, "true");
                    idx += 4;
                } else {
                    codeBuffer.insert(idx, "false");
                    idx += 5;
                }
            } else if (e1.getVar() instanceof Id id1 && e2.getVar() instanceof String s2) {
                getStrcmp(id1, s2);
                codeBuffer.insert(idx, "== 0");
                idx += 3;
            } else if (e2.getVar() instanceof Id id2 && e1.getVar() instanceof String s1) {
                getStrcmp(s1, id2);
                codeBuffer.insert(idx, "== 0");
                idx += 3;
            } else {
                String typeId1;
                if (e1.getVar() instanceof Id id1 && e2.getVar() instanceof Id id2) {
                    typeId1 = typeEnvirornment.lookup(id1.toString()).getType();
                    if (typeId1.equals("string")) {
                        getStrcmp(id1, id2);
                        codeBuffer.insert(idx, "==0");
                        idx += 3;
                    } else {
                        e1.accept(this);
                        codeBuffer.insert(idx, "==");
                        idx += 2;
                        e2.accept(this);
                    }
                } else {
                    e1.accept(this);
                    codeBuffer.insert(idx, "==");
                    idx += 2;
                    e2.accept(this);
                }
            }
        } else if (operation instanceof NeOp) {
            if (e1.getVar() instanceof String s1 && e2.getVar() instanceof String s2) {
                if (s1.length() >= s2.length()) {
                    codeBuffer.insert(idx, "true");
                    idx += 4;
                } else {
                    codeBuffer.insert(idx, "false");
                    idx += 5;
                }
            } else if (e1.getVar() instanceof Id id1 && e2.getVar() instanceof String s2) {
                getStrcmp(id1, s2);
                codeBuffer.insert(idx, "!=0");
                idx += 3;
            } else if (e2.getVar() instanceof Id id2 && e1.getVar() instanceof String s1) {
                getStrcmp(s1, id2);
                codeBuffer.insert(idx, "!=0");
                idx += 3;
            } else {
                String typeId1;
                if (e1.getVar() instanceof Id id1 && e2.getVar() instanceof Id id2) {
                    typeId1 = typeEnvirornment.lookup(id1.toString()).getType();
                    if (typeId1.equals("string")) {
                        getStrcmp(id1, id2);
                        codeBuffer.insert(idx, "!=0");
                        idx += 3;
                    } else {
                        e1.accept(this);
                        codeBuffer.insert(idx, "!=");
                        idx += 2;
                        e2.accept(this);
                    }
                } else {
                    e1.accept(this);
                    codeBuffer.insert(idx, "!=");
                    idx += 2;
                    e2.accept(this);
                }
            }
        } else if (operation instanceof NotOp) {
            codeBuffer.insert(idx, "!");
            idx++;
            e1.accept(this);
        }
        return null;
    }

    @Override
    public Object visit(ExprOp exprOp) {
        return null;
    }

    @Override
    public Object visit(IdListInitOp idListInitOp) {
        int i = 1;

        for (Map.Entry<String, ExprOp> entry : idListInitOp.getList().entrySet()) {
            String var = entry.getKey();

            String type = typeEnvirornment.lookup(var).getType();
            ExprOp value = entry.getValue();
            if (value.getVar() != null) {
                if (type.equals("string") && value.getVar() instanceof String) {
                    codeBuffer.append(var);
                    codeBuffer.append(" = \"").append(value.getVar()).append("\";\n");
                } else if (value.getVar() == null) {//caso in cui ho int v1,v = 5+5;
                    if (idListInitOp.getList().size() == 1) {
                        codeBuffer.append(var).append(";\n");
                    } else if (i < idListInitOp.getList().size()) {
                        codeBuffer.append(var).append(",");
                        i++;
                    } else {
                        codeBuffer.append(var).append(";\n");

                    }
                } else {//caso int v = 5;
                    if (idListInitOp.getList().size() == 1) {
                        codeBuffer.append(var);
                        codeBuffer.append(" = ").append(value.getVar()).append(";\n");
                    } else if (i < idListInitOp.getList().size()) {
                        codeBuffer.append(var);
                        codeBuffer.append(" = ").append(value.getVar()).append(", ");
                        i++;
                    }else{
                        codeBuffer.append(var);
                        codeBuffer.append(" = ").append(value.getVar()).append(";\n");
                    }
                }
            } else if (value.getOperation() != null) {
                codeBuffer.append(var).append(" = ");
                idx = codeBuffer.length();
                value.getOperation().accept(this);
                codeBuffer.append(";\n");
            } else if (value.getStatement() != null && value.getStatement() instanceof CallProcOp c) {
                codeBuffer.append(var).append(" = ");
                c.accept(this);
            }
        }
        return null;
    }

    @Override
    public Object visit(ParDeclOp parDeclOp) {
        if (parDeclOp != null) {
            parDeclOp.accept(this);
        }
        return null;
    }

    @Override
    public Object visit(ProcOp procOp) {
        typeEnvirornment.enterScope(procOp.getTable());
        if (procOp.getId().toString().equals("main")) {
            codeBuffer.append("int");
        } else if (procOp.getT() != null) {
            codeBuffer.append(getTypeInC(procOp.getT().getTipo()));
        } else {
            codeBuffer.append("void");
        }
        //PARAMETRI PASSATI ALLA FUNZIONE
        if (!procOp.getId().toString().equals("main")) {
            codeBuffer.append(" ").append(procOp.getId()).append("(");
            procOp.getList().accept(this);

        } else {
            codeBuffer.append(" ").append(procOp.getId()).append("(");
            codeBuffer.append("){\n");
            if (procOp.getList().getList().get(0).getId() != null) {
                for (ParDeclOp parDeclOp : procOp.getList().getList()) {
                    String id = parDeclOp.getId().toString();
                    codeBuffer.append(getTypeInC(parDeclOp.getT().getTipo())).append(" ");

                    codeBuffer.append(id).append(";\n");

                    codeBuffer.append(";\n");
                }
            }


        }
        //VARIABILI DICHIARATE NEL CORPO DELLA FUNZIONE

        if (procOp.getVars() != null)
            procOp.getVars().accept(this);
        //STATEMENT
        if (procOp.getStats() != null)
            procOp.getStats().accept(this);

        //RETURN
        ArrayList<ParDeclOp> output = new ArrayList<>();
        for (ParDeclOp p: procOp.getList().getList()) {

            if (p.getOut() != null) {
                output.add(p);
            }
        }
        if (procOp.getId().toString().equals("main")) {
            codeBuffer.append("return 0;\n}");
        } else if (procOp.getList().getList() != null) {

            int i = 0;
            if (procOp.getList().getList().size() > 1) {

                codeBuffer.append(procOp.getId()).append("_s new;\n");
                for (ParDeclOp e : output) {
                    if (e.getId() != null) {
                        String id = e.getId().toString();
                        if (i < output.size()) {
                            codeBuffer.append("new.var_").append(i).append(" = ").append(id).append(";\n");
                            i++;
                        } else {
                            codeBuffer.append("\n");
                        }
                    } else {
                        //se è una costante
                        if (e.getT().getTipo().equals("string")) {
                            codeBuffer.append("new.var_").append(i).append(" = \"").append(e.getId().toString()).append("\";\n");
                        } else {
                            codeBuffer.append("new.var_").append(i).append(" = ").append(e.getId().toString()).append(";\n");
                        }

                        i++;

                    }
                }
                codeBuffer.append("return new;\n}\n");
            } else {
                ParDeclOp e = output.get(0);
                if (e.getId() != null) {
                    String id = e.getId().toString();
                    codeBuffer.append("return ").append(id).append(";\n}\n");
                }
            }
        } else {
            //se return è void
            codeBuffer.append("\n}\n");
        }

        typeEnvirornment.exitScope();
        return null;
    }

    @Override
    public Object visit(ExprListOp exprListOp) {
        return null;
    }

    @Override
    public Object visit(VarDeclOp varDecl) {
        codeBuffer.append(getTypeInC(varDecl.getTipo().getTipo())).append(" ");
        varDecl.getList().accept(this);
        return null;
    }

    @Override
    public Object visit(VarDeclListOp varDeclList) {
        for (VarDeclOp varDeclOp : varDeclList.getList()) {
            varDeclOp.accept(this);
        }
        return null;
    }

    @Override
    public Object visit(TypeOp typeOp) {
        return null;
    }

    @Override
    public Object visit(ReturnStatOp returnStatOp) {
        return null;
    }

    @Override
    public Object visit(WriteStatOp writeStatOp) {
        writeStatOp.getExprList().accept(this);
        return null;
    }

    @Override
    public Object visit(WhileStatOp whileStatOp) {
        if (whileStatOp.getStatListOp() != null && whileStatOp.getStatListOp() == null) {
            codeBuffer.append("while(");
            whileStatOp.getE().accept(this);
            codeBuffer.append("){\n");
            whileStatOp.getStatListOp().accept(this);
            codeBuffer.append("}\n");
        } else if (whileStatOp.getStatListOp() != null && whileStatOp.getStatListOp() != null) {
            codeBuffer.append("while(");
            whileStatOp.getE().accept(this);
            codeBuffer.append("){\n");
            whileStatOp.getStatListOp().accept(this);
            whileStatOp.getStatListOp().accept(this);
            codeBuffer.append("}\n");
            whileStatOp.getStatListOp().accept(this);
        }

        return null;
    }

    @Override
    public Object visit(StatOp statOp) {
        if (statOp.getStatement() instanceof AssignStatOp assignStat) {
            assignStat.accept(this);
        } else if (statOp.getStatement() instanceof IfStatOp ifStat) {
            ifStat.accept(this);
        } else if (statOp.getStatement() instanceof WhileStatOp whileStat) {
            whileStat.accept(this);
        } else if (statOp.getStatement() instanceof ReadStatOp readlnStat) {
            readlnStat.accept(this);
        } else if (statOp.getStatement() instanceof WriteStatOp writeStat) {//writeOp
            writeStat.accept(this);
        } else if (statOp.getStatement() instanceof CallProcOp callProcOp) {//callProc
            callProcOp.accept(this);
        }
        return null;
    }

    @Override
    public Object visit(ReadStatOp readlnStatOp) {return null;}

    @Override
    public Object visit(IfStatOp ifStatOp) {
        codeBuffer.append("if(");
        if (ifStatOp.getE().getOperation() != null) {
            idx = codeBuffer.length();
            ifStatOp.getE().getOperation().accept(this);
            codeBuffer.append("){\n");
        } else if (ifStatOp.getE().getVar() != null && ifStatOp.getE().getVar() instanceof Id id) {
            codeBuffer.append(id);
            codeBuffer.append("){\n");
        } else if (ifStatOp.getE().getStatement() != null && ifStatOp.getE().getStatement() instanceof CallProcOp c) {
            idx = codeBuffer.lastIndexOf("\n");
            codeBuffer.insert(idx, "\n");
            idx++;
            c.accept(this);
            codeBuffer.deleteCharAt(codeBuffer.length() - 1);
            codeBuffer.deleteCharAt(codeBuffer.length() - 1);
            codeBuffer.append("){\n");
        } else {
            codeBuffer.append(ifStatOp.getE().getVar());
            codeBuffer.append("){\n");
        }
        ifStatOp.getStatList().accept(this);
        codeBuffer.append("}\n");

        if (ifStatOp.getElseStat() != null) {
            ifStatOp.getElseStat().accept(this);
        }
        return null;
    }
}
