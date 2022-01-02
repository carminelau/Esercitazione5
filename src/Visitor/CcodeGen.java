package Visitor;

import Node.*;
import Operation.*;
import Scope.Record;
import Statement.*;
import Scope.*;

import java.util.ArrayList;
import java.util.Map;

public class CcodeGen implements Visitor {

    private int idx;
    private int callProcCount = 0;
    private StringBuilder codeBuffer;
    private TypeEnviroment typeEnvirornment;
    boolean addCode = false;

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

    public void getStrcat(String s1, String s2) {

        codeBuffer.insert(idx, "strcat(");
        idx += 7;
        codeBuffer.insert(idx, s1);
        idx += s1.length();
        codeBuffer.insert(idx, ",\"");
        idx += 2;
        codeBuffer.insert(idx, s2);
        idx += s2.length();
        codeBuffer.insert(idx, "\")");
        idx += 2;
    }

    public void getPow(ExprOp e1, ExprOp e2) {

        if (e1.getVar() instanceof Id id1 && e2.getVar() instanceof Id id2) {
            String typeId1 = typeEnvirornment.lookup(id1.toString()).getType();
            String typeId2 = typeEnvirornment.lookup(id2.toString()).getType();

            if (typeId1.equalsIgnoreCase("integer") || typeId1.equalsIgnoreCase("real") ||typeId2.equalsIgnoreCase("integer") || typeId2.equalsIgnoreCase("real") ) {
                codeBuffer.insert(idx, "pow(");
                idx += 4;
                codeBuffer.insert(idx, e1.getVar());
                idx += e1.getVar().toString().length();
                codeBuffer.insert(idx, ",");
                idx += 1;
                codeBuffer.insert(idx, e2.getVar());
                idx += e2.getVar().toString().length();
                codeBuffer.insert(idx, ")");
                idx += 1;
            }
        }
    }

    private String getConv(String id) {
        String type = typeEnvirornment.lookup(id).getType();
        return switch (type) {
            case "integer", "boolean" -> "%d";
            case "real" -> "%f";
            case "string" -> "%s";
            default -> "";
        };
    }

    private String getConvOp(String type) {
        return switch (type) {
            case "integer", "boolean" -> "%d";
            case "real" -> "%f";
            case "string" -> "%s";
            default -> "";
        };
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
                if (entry.getValue().getReturnType() != null) {
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
        }
        programOp.getVarDeclOpList().accept(this);
        programOp.getProcOpList().accept(this);
        programOp.getMain().accept(this);
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
        int i = 1;
        ArrayList<String> ids = idListOp.getIdList();
        for(String id : ids){
            if(i < ids.size()) {
                codeBuffer.append(id).append(",");
                i++;
            }else{
                codeBuffer.append(id).append("){\n");
            }
        }
        return null;
    }

    @Override
    public Object visit(AssignStatOp assignStatOp) {
        int returnCount = 0;
        boolean multiCall = false;
        ExprOp exprOp = assignStatOp.getExpr();
        if (exprOp.getStatement() != null && exprOp.getStatement() instanceof CallProcOp callProcOp) {
            if(typeEnvirornment.lookup(callProcOp.getId()).getReturnType()!= null) {
                if (typeEnvirornment.lookup(callProcOp.getId()).getReturnType().size() > 1) {
                    codeBuffer.append(callProcOp.getId()).append(" = ");
                    exprOp.accept(this);
                }
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
            if(typeEnvirornment.lookup(callProcOp.getId()).getReturnType()!= null) {
                for (String ignored : typeEnvirornment.lookup(callProcOp.getId()).getReturnType()) {
                    if (typeEnvirornment.lookup(callProcOp.getId()).getReturnType().size() > 1) {
                        int newCallcount;
                        if (multiCall) {
                            newCallcount = callProcCount - 1;
                        } else {
                            newCallcount = callProcCount;
                        }
                        codeBuffer.append(assignStatOp.getId()).append(" = ")
                                .append("new_").append(newCallcount).append(".var_").append(returnCount).append(";\n");
                    }
                    returnCount++;
                }
                if (typeEnvirornment.lookup(callProcOp.getId()).getReturnType().size() > 1)
                    callProcCount++;
            }
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
        typeEnvirornment.enterScope(main.getGlobalTable());
        codeBuffer.append("int");
        codeBuffer.append(" main( ");

        codeBuffer.append(" ){\n");
        main.getVarDeclOpList().accept(this);
        main.getStats().accept(this);
        codeBuffer.append("return 0;\n}");
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
        } else if (operation instanceof DivIntOp) {
            if (e1.getVar() instanceof Integer && e2.getVar() instanceof Integer) {
                e1.accept(this);
                codeBuffer.insert(idx, "/");
                idx++;
                e2.accept(this);
            }
        } else if (operation instanceof PowOp) {
            getPow(e1,e2);
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
        } else if (operation instanceof StrConcatOp) {

            if (e1.getVar() instanceof String s1 && e2.getVar() instanceof String s2) {
                /*
                    if Return value < 0 then it indicates str1 is less than str2.
                    if Return value > 0 then it indicates str2 is less than str1.
                    if Return value = 0 then it indicates str1 is equal to str2.

                    int strcmp(const char *str1, const char *str2)
                */
                getStrcat(s1,s2);
            }
        }
        return null;
    }

    @Override
    public Object visit(ExprOp exprOp) {
        if (exprOp.getVar() != null) {
            codeBuffer.append(exprOp.getVar().toString());
            if (exprOp.getVar() instanceof Id id) {
                idx += id.toString().length();
            } else {
                idx++;
            }
        } else if (exprOp.getOperation() != null) {
            idx = codeBuffer.length();
            exprOp.getOperation().accept(this);
        } else if (exprOp.getStatement() != null && exprOp.getStatement() instanceof CallProcOp c) {
            ArrayList<String> types = typeEnvirornment.lookup(c.getId()).getReturnType();
            if (types.get(0).equals("string")) {
                //Se ho una chiamata a funzione che restituisce una stringa faccio il confronto in java
                String s = (String) c.getExprList().getExprlist().get(0).getVar();
                codeBuffer.append(s.length());
            } else {
                idx = codeBuffer.lastIndexOf("\n");
                codeBuffer.insert(idx, "\n");
                idx++;
                c.accept(this);
                //tolgo un ';' di troppo aggiunto sia da callproc che da operation
                codeBuffer.deleteCharAt(codeBuffer.length() - 1);
                codeBuffer.deleteCharAt(codeBuffer.length() - 1);
                idx = codeBuffer.length();
            }

        }
        return null;
    }

    @Override
    public Object visit(IdListInitOp idListInitOp) {
        int i = 1;

        for (Map.Entry<String, ExprOp> entry : idListInitOp.getList().entrySet()) {
            String var = entry.getKey();
            System.out.println(var + "Record: " + typeEnvirornment.lookup(var));
            if (typeEnvirornment.lookup(var) != null) {
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
                        } else {
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

        codeBuffer.append("}\n");


        typeEnvirornment.exitScope();
        return null;
    }

    @Override
    public Object visit(ExprListOp exprListOp) {
        for (ExprOp e : exprListOp.getExprlist()) {
            if (e.getVar() instanceof String s) {
                codeBuffer.append("printf(");
                codeBuffer.append("\"").append(s).append("\"");
                codeBuffer.append(");\n");
            } else if (exprListOp.getExprlist().size() > 1 && e.getVar() instanceof Id id) {
                codeBuffer.append("printf(\"");
                codeBuffer.append(getConv(id.toString())).append("\"").append(",");
                codeBuffer.append(id);
                codeBuffer.append(");\n");
            } else if (exprListOp.getExprlist().size() == 1 && e.getVar() instanceof Id id) {
                codeBuffer.append("printf(\"");
                codeBuffer.append(getConv(id.toString())).append("\",");
                codeBuffer.append(id);
                codeBuffer.append(");\n");
            } else if (exprListOp.getExprlist().size() == 1 && e.getOperation() != null) {
                codeBuffer.append("printf(\"");
                codeBuffer.append(getConvOp(e.getOperation().getOpType())).append("\",");
                idx = codeBuffer.length();
                e.getOperation().accept(this);
                codeBuffer.append(")\n");
            } else if (e.getStatement() instanceof CallProcOp c) {

                ArrayList<String> ret = typeEnvirornment.lookup(c.getId()).getReturnType();

                if (ret.size() > 1) {
                    codeBuffer.append(c.getId()).append("_s new_").append(callProcCount).append(" = ");
                    c.accept(this);
                    codeBuffer.append("printf(");
                    codeBuffer.append("\"");
                    for (String type : ret) {
                        codeBuffer.append(getConvOp(type));
                        codeBuffer.append("\\n");
                    }
                    codeBuffer.append("\"");
                    codeBuffer.append(",");
                    for (int i = 0; i < ret.size(); i++) {
                        codeBuffer.append("new_").append(callProcCount).append(".var_").append(i);
                        if (i < ret.size() - 1) {
                            codeBuffer.append(",");
                        }
                    }
                    codeBuffer.append(");\n");
                    callProcCount++;
                } else {
                    if (ret.get(0).equals("void")) {
                        codeBuffer.append("printf(\"\");\n");
                    } else {
                        codeBuffer.append(getTypeInC(ret.get(0))).append(" new_").append(callProcCount).append(" = ");
                        c.accept(this);
                        codeBuffer.append("printf(\"").append(getConvOp(ret.get(0))).append("\",").append(" new_")
                                .append(callProcCount).append(");\n");
                        callProcCount++;
                    }
                }

            }
        }
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
        codeBuffer.append(getTypeInC(typeOp.getTipo()));
        return null;
    }

    @Override
    public Object visit(ReturnStatOp returnStatOp) {
        if (returnStatOp != null){
            codeBuffer.append("return ");
            returnStatOp.getExpr().accept(this);
            codeBuffer.append("; \n");
        }
        return null;
    }

    @Override
    public Object visit(WriteStatOp writeStatOp) {
        ExprOp e = writeStatOp.getExpr();
        String mode = writeStatOp.getMode();

        if (e.getVar() instanceof String s) {
            if (mode.equalsIgnoreCase("WRITE_LN")){
                codeBuffer.append("println(");
                codeBuffer.append("\"").append(s).append("\"");
                codeBuffer.append(");\n");
            } else if (mode.equalsIgnoreCase("WRITE_")) {
                codeBuffer.append("printf(");
                codeBuffer.append("\"").append(s).append("\"");
                codeBuffer.append(");\n");
            } else if (mode.equalsIgnoreCase("WRITE_T")) {
                codeBuffer.append("printf(");
                codeBuffer.append("\"").append(s).append("\"");
                codeBuffer.append("+ \"\\t\");\n");
            } else if (mode.equalsIgnoreCase("WRITE_B")) {
                codeBuffer.append("printf(");
                codeBuffer.append("\"").append(s).append("\"");
                codeBuffer.append("+ \"\\b\");\n");
            }
        } else if (e.getVar() instanceof Id id1){
            if (mode.equalsIgnoreCase("WRITE_LN")){
                codeBuffer.append("println(");
                codeBuffer.append(id1);
                codeBuffer.append(");\n");
            } else if (mode.equalsIgnoreCase("WRITE_")) {
                codeBuffer.append("printf(");
                codeBuffer.append(id1);
                codeBuffer.append(");\n");
            } else if (mode.equalsIgnoreCase("WRITE_T")) {
                codeBuffer.append("printf(");
                codeBuffer.append(id1);
                codeBuffer.append("+ \"\\t\");\n");
            } else if (mode.equalsIgnoreCase("WRITE_B")) {
                codeBuffer.append("printf(");
                codeBuffer.append(id1);
                codeBuffer.append("+ \"\\b\");\n");
            }

        } else if (e.getVar() instanceof Integer i){
            if (mode.equalsIgnoreCase("WRITE_LN")){
                codeBuffer.append("println(");
                codeBuffer.append(i);
                codeBuffer.append(");\n");
            } else if (mode.equalsIgnoreCase("WRITE_")) {
                codeBuffer.append("printf(");
                codeBuffer.append(i);
                codeBuffer.append(");\n");
            } else if (mode.equalsIgnoreCase("WRITE_T")) {
                codeBuffer.append("printf(");
                codeBuffer.append(i);
                codeBuffer.append("+ \"\\t\");\n");
            } else if (mode.equalsIgnoreCase("WRITE_B")) {
                codeBuffer.append("printf(");
                codeBuffer.append(i);
                codeBuffer.append("+ \"\\b\");\n");
            }
        } else if (e.getVar() instanceof Float f){
            if (mode.equalsIgnoreCase("WRITE_LN")){
                codeBuffer.append("println(");
                codeBuffer.append(f);
                codeBuffer.append(");\n");
            } else if (mode.equalsIgnoreCase("WRITE_")) {
                codeBuffer.append("printf(");
                codeBuffer.append(f);
                codeBuffer.append(");\n");
            } else if (mode.equalsIgnoreCase("WRITE_T")) {
                codeBuffer.append("printf(");
                codeBuffer.append(f);
                codeBuffer.append("+ \"\\t\");\n");
            } else if (mode.equalsIgnoreCase("WRITE_B")) {
                codeBuffer.append("printf(");
                codeBuffer.append(f);
                codeBuffer.append("+ \"\\b\");\n");
            }
        } else if (e.getVar() instanceof Operations op){

            if (mode.equalsIgnoreCase("WRITE_LN")){
                codeBuffer.append("println(\"");
                codeBuffer.append(getConvOp(e.getOperation().getOpType())).append("\",");
                idx = codeBuffer.length();
                e.getOperation().accept(this);
                codeBuffer.append(")\n");
            } else if (mode.equalsIgnoreCase("WRITE_")) {
                codeBuffer.append("printf(\"");
                codeBuffer.append(getConvOp(e.getOperation().getOpType())).append("\",");
                idx = codeBuffer.length();
                e.getOperation().accept(this);
                codeBuffer.append(")\n");
            } else if (mode.equalsIgnoreCase("WRITE_T")) {
                codeBuffer.append("printf(\"");
                codeBuffer.append(getConvOp(e.getOperation().getOpType())).append("\",");
                idx = codeBuffer.length();
                e.getOperation().accept(this);
                codeBuffer.append("\\t)\n");
            } else if (mode.equalsIgnoreCase("WRITE_B")) {
                codeBuffer.append("printf(\"");
                codeBuffer.append(getConvOp(e.getOperation().getOpType())).append("\",");
                idx = codeBuffer.length();
                e.getOperation().accept(this);
                codeBuffer.append("\\b)\n");
            }

        } else if (e.getStatement() instanceof CallProcOp c) {

            ArrayList<String> ret = typeEnvirornment.lookup(c.getId()).getReturnType();

            if (ret.size() > 1) {
                codeBuffer.append(c.getId()).append("_s new_").append(callProcCount).append(" = ");
                c.accept(this);
                codeBuffer.append("printf(");
                codeBuffer.append("\"");
                for (String type : ret) {
                    codeBuffer.append(getConvOp(type));
                    codeBuffer.append("\\n");
                }
                codeBuffer.append("\"");
                codeBuffer.append(",");
                for (int i = 0; i < ret.size(); i++) {
                    codeBuffer.append("new_").append(callProcCount).append(".var_").append(i);
                    if (i < ret.size() - 1) {
                        codeBuffer.append(",");
                    }
                }
                codeBuffer.append(");\n");
                callProcCount++;
            } else {
                if (ret.get(0).equals("void")) {
                    codeBuffer.append("printf(\"\");\n");
                } else {
                    codeBuffer.append(getTypeInC(ret.get(0))).append(" new_").append(callProcCount).append(" = ");
                    c.accept(this);
                    codeBuffer.append("printf(\"").append(getConvOp(ret.get(0))).append("\",").append(" new_")
                            .append(callProcCount).append(");\n");
                    callProcCount++;
                }
            }

        }

        idx = codeBuffer.length();
        return null;
    }

    @Override
    public Object visit(WhileStatOp whileStatOp) {

        if (whileStatOp.getStatListOp() != null && whileStatOp.getStatListOp() == null) {
            codeBuffer.append("while(");
            whileStatOp.getE().accept(this);
            codeBuffer.append("){\n");
            whileStatOp.getVarDeclList().accept(this);
            whileStatOp.getStatListOp().accept(this);
            codeBuffer.append("}\n");
        } else if (whileStatOp.getStatListOp() != null && whileStatOp.getStatListOp() != null) {
            codeBuffer.append("while(");
            whileStatOp.getE().accept(this);
            codeBuffer.append("){\n");
            whileStatOp.getVarDeclList().accept(this);
            whileStatOp.getStatListOp().accept(this);
            codeBuffer.append("}\n");
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
        } else if (statOp.getStatement() instanceof ReturnStatOp returnStatOp){
            returnStatOp.accept(this);
        }
        return null;
    }

    @Override
    public Object visit(ReadStatOp readlnStatOp) {
        if (readlnStatOp.getIdList().getIdList().size() == 1) {

            if (getConv(readlnStatOp.getIdList().getIdList().get(0)).equals("%s")) {
                codeBuffer.append(readlnStatOp.getIdList().getIdList().get(0)).append(" = malloc(sizeof(char));\n");
            }
            codeBuffer.append("scanf(\"");
            codeBuffer.append(getConv(readlnStatOp.getIdList().getIdList().get(0)));
            if (getConv(readlnStatOp.getIdList().getIdList().get(0)).equals("%s")) {
                codeBuffer.append("\",").append(readlnStatOp.getIdList().getIdList().get(0)).append(");\n");
            } else
                codeBuffer.append("\",&").append(readlnStatOp.getIdList().getIdList().get(0)).append(");\n");
        } else {

            for (String id : readlnStatOp.getIdList().getIdList()) {

                if (getConv(id).equals("%s")) {
                    codeBuffer.append(id).append(" = malloc(sizeof(char));\n");
                }
                codeBuffer.append("scanf(\"");
                codeBuffer.append(getConv(id));
                if (getConv(id).equals("%s")) {
                    codeBuffer.append("\",").append(id).append(");\n");
                } else {
                    codeBuffer.append("\",&").append(id).append(");\n");
                }
                codeBuffer.append("printf(\"\\n\");\n");
            }
        }

        return null;
    }

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
