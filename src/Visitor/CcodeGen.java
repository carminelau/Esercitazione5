package Visitor;

import Node.*;
import Operation.*;
import Scope.Record;
import Statement.*;
import Scope.*;

import java.util.ArrayList;
import java.util.Map;

public class CcodeGen implements Visitor {

    private int countStringLength = 0,callProcCount=0;
    private StringBuilder codeBuffer;
    private TypeEnviroment typeEnvirornment;

    public StringBuilder getCodeBuffer() {
        return codeBuffer;
    }

    public CcodeGen() {
        codeBuffer = new StringBuilder();
        this.typeEnvirornment = new TypeEnviroment();
    }

    private String getTypeInC(boolean out, String type) {
        if (!out) {
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
        } else {
            if (type.contains("integer")) {
                return "int*";
            } else if (type.contains("bool")) {
                return "bool";
            } else if (type.contains("real")) {
                return "float*";
            } else if (type.contains("string")) {
                return "char**";
            }
            return null;
        }
    }

    public void getStrcmp(String s, Id id2) {
        codeBuffer.append("strcmp(\"");
        codeBuffer.append(s);
        codeBuffer.append("\",");
        codeBuffer.append(id2);
        codeBuffer.append(")");
    }

    public void getStrcmp(Id id1, Id id2) {
        codeBuffer.append("strcmp(");
        codeBuffer.append(id1);
        codeBuffer.append(",");
        codeBuffer.append(id2);
        codeBuffer.append(")");

    }

    public void getStrcmp(Id id1, String s) {

        codeBuffer.append("strcmp(");
        codeBuffer.append(id1);
        codeBuffer.append(",\"");
        codeBuffer.append(s);
        codeBuffer.append("\")");
    }

    public void getStrcat(String s1, String s2) {

        codeBuffer.append("strcat(\"");

        codeBuffer.append(s1);

        codeBuffer.append("\",\"");

        codeBuffer.append(s2);

        codeBuffer.append("\");\n");

    }

    public void getStrcpy(String s1, String s2) {

        codeBuffer.append("strcpy(\"");

        codeBuffer.append(s1);

        codeBuffer.append("\",\"");

        codeBuffer.append(s2);

        codeBuffer.append("\");\n");

    }

    public void getPow(ExprOp e1, ExprOp e2) {

        if (e1.getVar() instanceof Id id1 && e2.getVar() instanceof Id id2) {
            String typeId1 = typeEnvirornment.lookup(id1.toString()).getType();
            String typeId2 = typeEnvirornment.lookup(id2.toString()).getType();

            if (typeId1.equalsIgnoreCase("integer") || typeId1.equalsIgnoreCase("real") || typeId2.equalsIgnoreCase("integer") || typeId2.equalsIgnoreCase("real")) {
                codeBuffer.append("pow(");
                codeBuffer.append(e1.getVar());
                codeBuffer.append(",");
                codeBuffer.append(e2.getVar());
                codeBuffer.append(")");
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

    private String getType(String id) {
        String type = typeEnvirornment.lookup(id).getType();
        return type;
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
                + "#include <math.h> \n"
                + "#include <stdbool.h> \n\n"
                + "char* concatC_I(char* stringa, int valore, int dim1, int flag);\n"
                + "char* concatC_F(char* stringa, float valore, int dim1, int flag);\n"
                + "char* concatC_C(char* stringa, char* stringa2, int dim1, int dim2);\n\n");

        codeBuffer.append("char* concatC_I(char* stringa, int valore, int dim1, int flag){\n" +
                "    char * newBuffer = (char *)malloc(dim1 + 30);\n" +
                "    char val[20];\n" +
                "    sprintf(val, \"%d\",valore);\n" +
                "    if(flag){\n" +
                "        strcpy(newBuffer,stringa);\n" +
                "        strcat(newBuffer,val);\n" +
                "    }else{\n" +
                "        strcpy(newBuffer,val);\n" +
                "        strcat(newBuffer,stringa);\n" +
                "    }\n" +
                "    return newBuffer;\n" +
                "}\n" +
                "\n" +
                "char* concatC_F(char* stringa, float valore, int dim1, int flag){\n" +
                "    char * newBuffer = (char *)malloc(dim1 + 30);\n" +
                "    char val[20];\n" +
                "    sprintf(val, \"%.2f\",valore);\n" +
                "    if(flag){\n" +
                "        strcpy(newBuffer,stringa);\n" +
                "        strcat(newBuffer,val);\n" +
                "    }else{\n" +
                "        strcpy(newBuffer,val);\n" +
                "        strcat(newBuffer,stringa);\n" +
                "    }\n" +
                "    return newBuffer;\n" +
                "}\n" +
                "\n" +
                "char* concatC_C(char* stringa, char* stringa2, int dim1, int dim2){\n" +
                "    char * newBuffer = (char *)malloc(dim1 + dim2);\n" +
                "    strcpy(newBuffer,stringa);\n" +
                "    strcat(newBuffer,stringa2);\n" +
                "    \n" +
                "    return newBuffer;\n" +
                "}\n");

        typeEnvirornment.enterScope(programOp.getGlobalTable());
        //Dichiarazioni di funzioni
        for (Map.Entry<String, Record> entry : programOp.getGlobalTable().entrySet()) {
            if (entry.getValue().getKind().equals("method") && !entry.getKey().equals("main")) {
                if (entry.getValue().getReturnType() != null) {

                    codeBuffer.append(getTypeInC(false, entry.getValue().getReturnType())).append(" ");

                    if (entry.getValue().getParamType().size() != 0) {
                        if (entry.getValue().getParamType().get(0).equals("void")) {
                            codeBuffer.append(entry.getKey()).append("(").append(getTypeInC(false, entry.getValue().getParamType().get(0))).append(");")
                                    .append("\n\n");
                        } else if (entry.getValue().getParamType().size() == 1) {
                            if (entry.getValue().getParamType().get(0).startsWith("out")) {
                                codeBuffer.append(entry.getKey()).append("(");
                                codeBuffer.append(getTypeInC(true, entry.getValue().getParamType().get(0)));
                                codeBuffer.append(");").append("\n\n");
                            } else {
                                codeBuffer.append(entry.getKey()).append("(");
                                codeBuffer.append(getTypeInC(false, entry.getValue().getParamType().get(0)));
                                codeBuffer.append(");").append("\n\n");
                            }
                        } else {
                            int i = 1;
                            codeBuffer.append(entry.getKey()).append("(");
                            for (String type : entry.getValue().getParamType()) {
                                if (type.startsWith("out")) {
                                    codeBuffer.append(getTypeInC(true, type));
                                } else {
                                    codeBuffer.append(getTypeInC(false, type));
                                }
                                if (i != entry.getValue().getParamType().size()) {
                                    codeBuffer.append(",");
                                }
                                i++;
                            }
                            codeBuffer.append(");").append("\n\n");
                        }
                    } else {
                        codeBuffer.append(entry.getKey()).append("(");
                        codeBuffer.append(");").append("\n\n");
                    }
                }
            }
        }
        programOp.getVarDeclOpList().accept(this);
        programOp.getProcOpList().accept(this);
        programOp.getMain().accept(this);
        //System.out.println(codeBuffer);
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
        if (paramDeclListOp.getList().size() != 0) {
            for (ParDeclOp parDeclOp : paramDeclListOp.getList()) {
                if (parDeclOp != null) {
                    if (parDeclOp.getT().getTipo().equals("void")) {
                        codeBuffer.append(getTypeInC(false, parDeclOp.getT().getTipo()));
                    } else if (paramDeclListOp.getList().size() == 1) {
                        // ho un solo elemento nella lista ---> fun(int a,b);
                        String id = parDeclOp.getId().toString();
                        if (parDeclOp.getOut() == null) {
                            codeBuffer.append(getTypeInC(false, parDeclOp.getT().getTipo())).append(" ");
                        } else {
                            if (parDeclOp.getOut().equalsIgnoreCase("out"))
                                codeBuffer.append(getTypeInC(true, parDeclOp.getT().getTipo())).append(" ");
                        }
                        codeBuffer.append(id);
                        //codeBuffer.append("){\n");
                    } else {
                        //Ho più elementi nella lista ----> fun(int a; int b,c);
                        String id = parDeclOp.getId().toString();

                        if (parDeclOp.getOut() == null) {
                            codeBuffer.append(getTypeInC(false, parDeclOp.getT().getTipo())).append(" ");
                        } else {
                            if (parDeclOp.getOut().equalsIgnoreCase("out"))
                                codeBuffer.append(getTypeInC(true, parDeclOp.getT().getTipo())).append(" ");
                        }

                        if (i < paramDeclListOp.getList().size()) {
                            codeBuffer.append(id).append(",");
                            i++;
                        } else {
                            codeBuffer.append(id);
                        }
                    }
                }
            }

            codeBuffer.append(") {\n");
        } else {
            codeBuffer.append(") {\n");
        }
        return null;
    }

    @Override
    public Object visit(IdListOp idListOp) {
        int i = 1;
        ArrayList<String> ids = idListOp.getIdList();
        for (String id : ids) {
            if (i < ids.size()) {
                codeBuffer.append(id).append(",");
                i++;
            } else {
                codeBuffer.append(id).append("){\n");
            }
        }
        return null;
    }

    @Override
    public Object visit(AssignStatOp assignStatOp) {
        Record ra = typeEnvirornment.lookup(assignStatOp.getId().toString());

        ExprOp exprOp = assignStatOp.getExpr();
        if (exprOp.getStatement() != null && exprOp.getStatement() instanceof CallProcOp callProcOp) {
            if((ra.getType().split(" ")).length > 1){
                if(ra.getType().split(" ")[1].equalsIgnoreCase("string")){
                    codeBuffer.append(assignStatOp.getId()).append(" = ").append(callProcOp.getId());
                    codeBuffer.append("(");
                }
                else{
                    codeBuffer.append("*").append(assignStatOp.getId()).append(" = ").append(callProcOp.getId());
                    codeBuffer.append("(");
                }
            } else {
                codeBuffer.append(assignStatOp.getId()).append(" = ").append(callProcOp.getId());
                codeBuffer.append("(");
            }
            if (callProcOp.getExprList() != null) {
                int j = 1;
                for (ExprOp e : callProcOp.getExprList().getExprlist()) {
                    if (e.getVar() != null) {
                        if (j < callProcOp.getExprList().getExprlist().size()) {
                            if (e.getOut() != null) {
                                if (e.getOut().equalsIgnoreCase("out")) {
                                    Record r = typeEnvirornment.lookup(e.getVar().toString());
                                    if (r.getType().equalsIgnoreCase("string")) {
                                        codeBuffer.append(e.getVar()).append(",");
                                    } else {
                                        codeBuffer.append("&").append(e.getVar()).append(",");
                                    }
                                }
                                j++;
                            } else {
                                if (e.getType().contains("String")) {
                                    codeBuffer.append("\"");
                                    codeBuffer.append(e.getVar()).append("\"").append(",");
                                } else
                                    codeBuffer.append(e.getVar()).append(",");

                                j++;
                            }
                        } else {
                            if (e.getOut() != null) {
                                if (e.getOut().equalsIgnoreCase("out")) {
                                    Record r = typeEnvirornment.lookup(e.getVar().toString());
                                    if (r.getType().equalsIgnoreCase("string")) {
                                        codeBuffer.append(e.getVar()).append(");\n");
                                    } else {
                                        codeBuffer.append("&").append(e.getVar()).append(");\n");
                                    }
                                }
                            } else {
                                if (e.getType().contains("String")) {
                                    codeBuffer.append("\"");
                                    codeBuffer.append(e.getVar()).append("\");\n");
                                } else {
                                    codeBuffer.append(e.getVar()).append(");\n");
                                }
                            }
                        }
                    } else if (e.getOperation() != null) {
                        e.getOperation().accept(this);
                        if (j < callProcOp.getExprList().getExprlist().size()) {
                            codeBuffer.append(",");
                            j++;
                        } else {
                            codeBuffer.append(");\n");
                        }
                    } else if (e.getStatement() != null && e.getStatement() instanceof CallProcOp c) {

                        String types = typeEnvirornment.lookup(c.getId()).getReturnType();

                        c.accept(this);
                        codeBuffer.deleteCharAt(codeBuffer.length() - 1);
                        codeBuffer.deleteCharAt(codeBuffer.length() - 1);

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
           if (typeEnvirornment.lookup(callProcOp.getId()).getReturnType() != null) {
                callProcCount++;
            }
        } else if (exprOp.getVar() instanceof Id id) {
            if((ra.getType().split(" ")).length > 1){
                if(ra.getType().split(" ")[1].equalsIgnoreCase("string")){
                    codeBuffer.append(assignStatOp.getId()).append(" = ");
                }
                else{
                    codeBuffer.append("*").append(assignStatOp.getId()).append(" = ");
                }
            } else {
                codeBuffer.append(assignStatOp.getId()).append(" = ");
            }
            if (exprOp.getOut() != null) {
                Record r = typeEnvirornment.lookup(id.toString());
                if (r.getType().equalsIgnoreCase("string")) {
                    codeBuffer.append(id).append(";\n");
                } else {
                    codeBuffer.append("*").append(id).append(";\n");
                }
            } else {
                codeBuffer.append(id).append(";\n");
            }
        }else if (exprOp.getOperation() != null) {
            if((ra.getType().split(" ")).length > 1){
                if(ra.getType().split(" ")[1].equalsIgnoreCase("string")){
                    codeBuffer.append(assignStatOp.getId()).append(" = ");
                }
                else{
                    codeBuffer.append("*").append(assignStatOp.getId()).append(" = ");
                }
            } else {
                codeBuffer.append(assignStatOp.getId()).append(" = ");
            }
            exprOp.getOperation().accept(this);
            codeBuffer.append(";\n");
        } else {
            if (exprOp.getVar() instanceof String) {
                if((ra.getType().split(" ")).length > 1){
                    if(ra.getType().split(" ")[1].equalsIgnoreCase("string")){
                        codeBuffer.append(assignStatOp.getId()).append(" = ");
                    }
                    else{
                        codeBuffer.append("*").append(assignStatOp.getId()).append(" = ");
                    }
                } else {
                    codeBuffer.append(assignStatOp.getId()).append(" = ");
                }
                codeBuffer.append("\"").append(exprOp.getVar()).append("\";\n");
            } else
                codeBuffer.append(exprOp.getVar()).append(";\n");
        }

        return null;
    }

    @Override
    public Object visit(CallProcOp callProcOp) {

        if (callProcOp.getExprList() != null) {
            int i = 1;
            codeBuffer.append(callProcOp.getId()).append("(");
            for (ExprOp e : callProcOp.getExprList().getExprlist()) {
                if (e.getOperation() != null) {

                    e.getOperation().accept(this);
                    if (i < callProcOp.getExprList().getExprlist().size()) {
                        codeBuffer.append(",");
                        i++;
                    }

                } else if (e.getVar() != null && e.getVar() instanceof Id id) {
                    if (e.getOut() == null) {
                        codeBuffer.append(id);
                        if (i < callProcOp.getExprList().getExprlist().size()) {
                            codeBuffer.append(",");
                            i++;
                        }
                    } else {
                        Record r = typeEnvirornment.lookup(id.toString());
                        if (r.getType().equalsIgnoreCase("string")) {
                            codeBuffer.append(id);
                        } else {
                            codeBuffer.append("&").append(id);
                        }
                        if (i < callProcOp.getExprList().getExprlist().size()) {
                            codeBuffer.append(",");
                            i++;
                        }
                    }
                }else if (e.getStatement() != null && e.getStatement() instanceof CallProcOp c) {
                    System.out.println(callProcOp.getId());

                    String types = typeEnvirornment.lookup(c.getId()).getReturnType();
                    c.accept(this);
                    codeBuffer.deleteCharAt(codeBuffer.length() - 1);
                    codeBuffer.deleteCharAt(codeBuffer.length() - 1);
                    codeBuffer.deleteCharAt(codeBuffer.length() - 1);
                    codeBuffer.append(")");

                    if (i < callProcOp.getExprList().getExprlist().size()) {
                        codeBuffer.append(",");
                        i++;
                    }
                } else {
                    System.out.println(callProcOp.getId());
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
            codeBuffer.append("+");

            e2.accept(this);
        } else if (operation instanceof DiffOp) {
            e1.accept(this);
            codeBuffer.append("-");

            e2.accept(this);
        } else if (operation instanceof MulOp) {
            e1.accept(this);
            codeBuffer.append("*");

            e2.accept(this);
        } else if (operation instanceof DivOp) {
            e1.accept(this);
            codeBuffer.append("/");

            e2.accept(this);
        } else if (operation instanceof DivIntOp) {
            if (e1.getVar() instanceof Integer && e2.getVar() instanceof Integer) {
                e1.accept(this);
                codeBuffer.append("/");

                e2.accept(this);
            }
        } else if (operation instanceof PowOp) {
            getPow(e1, e2);
        } else if (operation instanceof AndOp) {
            e1.accept(this);
            codeBuffer.append("&&");
            e2.accept(this);
        } else if (operation instanceof OrOp) {
            e1.accept(this);
            codeBuffer.append("||");

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
                    codeBuffer.append("true");

                } else {
                    codeBuffer.append("false");

                }
            } else if (e1.getVar() instanceof Id id1 && e2.getVar() instanceof String s2) {
                getStrcmp(id1, s2);
                codeBuffer.append(">0");

            } else if (e2.getVar() instanceof Id id2 && e1.getVar() instanceof String s1) {
                getStrcmp(s1, id2);
                codeBuffer.append(">0");

            } else {
                String typeId1;
                if (e1.getVar() instanceof Id id1 && e2.getVar() instanceof Id id2) {
                    typeId1 = typeEnvirornment.lookup(id1.toString()).getType();
                    if (typeId1.equals("string")) {
                        getStrcmp(id1, id2);
                        codeBuffer.append(">0");

                    } else {
                        e1.accept(this);
                        codeBuffer.append(">");

                        e2.accept(this);
                    }
                } else {
                    e1.accept(this);
                    codeBuffer.append(">");

                    e2.accept(this);
                }
            }
        } else if (operation instanceof GeOp) {
            if (e1.getVar() instanceof String s1 && e2.getVar() instanceof String s2) {
                if (s1.length() >= s2.length()) {
                    codeBuffer.append("true");

                } else {
                    codeBuffer.append("false");

                }
            } else if (e1.getVar() instanceof Id id1 && e2.getVar() instanceof String s2) {
                getStrcmp(id1, s2);
                codeBuffer.append(">= 0");

            } else if (e2.getVar() instanceof Id id2 && e1.getVar() instanceof String s1) {
                getStrcmp(s1, id2);
                codeBuffer.append(">= 0");

            } else {
                String typeId1;
                if (e1.getVar() instanceof Id id1 && e2.getVar() instanceof Id id2) {
                    typeId1 = typeEnvirornment.lookup(id1.toString()).getType();
                    if (typeId1.equals("string")) {
                        getStrcmp(id1, id2);
                        codeBuffer.append(">= 0");

                    } else {
                        e1.accept(this);
                        codeBuffer.append(">=");

                        e2.accept(this);
                    }
                } else {
                    e1.accept(this);
                    codeBuffer.append(">=");

                    e2.accept(this);
                }
            }
        } else if (operation instanceof LtOp) {
            if (e1.getVar() instanceof String s1 && e2.getVar() instanceof String s2) {
                if (s1.length() < s2.length()) {
                    codeBuffer.append("true");

                } else {
                    codeBuffer.append("false");

                }
            } else {
                String typeId1;
                if (e1.getVar() instanceof Id id1 && e2.getVar() instanceof Id id2) {
                    typeId1 = typeEnvirornment.lookup(id1.toString()).getType();
                    if (typeId1.equals("string")) {
                        getStrcmp(id1, id2);
                        codeBuffer.append("<0");

                    } else {
                        e1.accept(this);
                        codeBuffer.append("<");

                        e2.accept(this);
                    }
                } else {
                    e1.accept(this);
                    codeBuffer.append("<");

                    e2.accept(this);
                }
            }
        } else if (operation instanceof LeOp) {
            if (e1.getVar() instanceof String s1 && e2.getVar() instanceof String s2) {
                if (s1.length() <= s2.length()) {
                    codeBuffer.append("true");

                } else {
                    codeBuffer.append("false");

                }
            } else if (e1.getVar() instanceof Id id1 && e2.getVar() instanceof String s2) {
                getStrcmp(id1, s2);
                codeBuffer.append("<=0");

            } else if (e2.getVar() instanceof Id id2 && e1.getVar() instanceof String s1) {
                getStrcmp(s1, id2);
                codeBuffer.append("<=0");

            } else {
                String typeId1;
                if (e1.getVar() instanceof Id id1 && e2.getVar() instanceof Id id2) {
                    typeId1 = typeEnvirornment.lookup(id1.toString()).getType();
                    if (typeId1.equals("string")) {
                        getStrcmp(id1, id2);
                        codeBuffer.append("<=0");

                    } else {
                        e1.accept(this);
                        codeBuffer.append("<=");

                        e2.accept(this);
                    }
                } else {
                    e1.accept(this);
                    codeBuffer.append("<=");

                    e2.accept(this);
                }
            }
        } else if (operation instanceof EqOp) {
            if (e1.getVar() instanceof String s1 && e2.getVar() instanceof String s2) {
                if (s1.length() >= s2.length()) {
                    codeBuffer.append("true");

                } else {
                    codeBuffer.append("false");

                }
            } else if (e1.getVar() instanceof Id id1 && e2.getVar() instanceof String s2) {
                getStrcmp(id1, s2);
                codeBuffer.append("== 0");

            } else if (e2.getVar() instanceof Id id2 && e1.getVar() instanceof String s1) {
                getStrcmp(s1, id2);
                codeBuffer.append("== 0");

            } else {
                String typeId1;
                if (e1.getVar() instanceof Id id1 && e2.getVar() instanceof Id id2) {
                    typeId1 = typeEnvirornment.lookup(id1.toString()).getType();
                    if (typeId1.equals("string")) {
                        getStrcmp(id1, id2);
                        codeBuffer.append("==0");

                    } else {
                        e1.accept(this);
                        codeBuffer.append("==");

                        e2.accept(this);
                    }
                } else {
                    e1.accept(this);
                    codeBuffer.append("==");

                    e2.accept(this);
                }
            }
        } else if (operation instanceof NeOp) {
            if (e1.getVar() instanceof String s1 && e2.getVar() instanceof String s2) {
                if (s1.length() >= s2.length()) {
                    codeBuffer.append("true");

                } else {
                    codeBuffer.append("false");

                }
            } else if (e1.getVar() instanceof Id id1 && e2.getVar() instanceof String s2) {
                getStrcmp(id1, s2);
                codeBuffer.append("!=0");

            } else if (e2.getVar() instanceof Id id2 && e1.getVar() instanceof String s1) {
                getStrcmp(s1, id2);
                codeBuffer.append("!=0");

            } else {
                String typeId1;
                if (e1.getVar() instanceof Id id1 && e2.getVar() instanceof Id id2) {
                    typeId1 = typeEnvirornment.lookup(id1.toString()).getType();
                    if (typeId1.equals("string")) {
                        getStrcmp(id1, id2);
                        codeBuffer.append("!=0");

                    } else {
                        e1.accept(this);
                        codeBuffer.append("!=");

                        e2.accept(this);
                    }
                } else {
                    e1.accept(this);
                    codeBuffer.append("!=");

                    e2.accept(this);
                }
            }
        } else if (operation instanceof NotOp) {
            codeBuffer.append("!");

            e1.accept(this);
        } else if (operation instanceof StrConcatOp) {
            if (e1.getStatement() instanceof CallProcOp c && e2.getVar() != null) {
                Record r = typeEnvirornment.lookup(c.getId());
                String tp2 = "";
                if (e2.getVar() instanceof String s2 && r.getReturnType().equalsIgnoreCase("string")) {
                    countStringLength = countStringLength + s2.length() +10;
                    codeBuffer.append("concatC_C(\"").append(s2).append("\",");
                    tp2 = "stringstring";
                } else if (e2.getVar() instanceof String s2 && r.getReturnType().equalsIgnoreCase("integer")) {
                    countStringLength = countStringLength + s2.length() +10;
                    codeBuffer.append("concatC_I(\"").append(s2).append("\",");
                    tp2 = "integerstring";
                } else if (e2.getVar() instanceof String s2 && r.getReturnType().equalsIgnoreCase("real")) {
                    countStringLength = countStringLength + s2.length() +10;
                    codeBuffer.append("concatC_F(\"").append(s2).append("\",");
                    tp2 = "realstring";
                } else if (e2.getVar() instanceof Id id2) {
                    if (getType(id2.toString()).equalsIgnoreCase("string") && r.getReturnType().equalsIgnoreCase("string")) {
                        countStringLength = countStringLength + id2.toString().length() +10;
                        codeBuffer.append("concatC_C(").append(id2).append(",");
                        tp2 = "stringstring";
                    } else if (getType(id2.toString()).equalsIgnoreCase("integer") && r.getReturnType().equalsIgnoreCase("string")) {
                        countStringLength = countStringLength + 10 +10;
                        codeBuffer.append("concatC_I(").append(id2).append(",");
                        tp2 = "stringinteger";
                    } else if (getType(id2.toString()).equalsIgnoreCase("real") && r.getReturnType().equalsIgnoreCase("string")) {
                        countStringLength = countStringLength + 10 +10;
                        codeBuffer.append("concatC_F(").append(id2).append(",");
                        tp2 = "stringreal";
                    } else if (getType(id2.toString()).equalsIgnoreCase("string") && r.getReturnType().equalsIgnoreCase("integer")) {
                        countStringLength = countStringLength + id2.toString().length() +10;
                        codeBuffer.append("concatC_I(").append(id2).append(",");
                        tp2 = "integerstring";
                    } else if (getType(id2.toString()).equalsIgnoreCase("string") && r.getReturnType().equalsIgnoreCase("real")) {
                        countStringLength = countStringLength + id2.toString().length() +10;
                        codeBuffer.append("concatC_F(").append(id2).append(",");
                        tp2 = "realstring";
                    }

                }
                ((CallProcOp) e2.getStatement()).accept(this);

                codeBuffer.deleteCharAt(codeBuffer.length() - 1);
                codeBuffer.deleteCharAt(codeBuffer.length() - 1);
                codeBuffer.append(",");
                countStringLength = countStringLength + c.getId().length();
                switch (tp2) {
                    case "stringstring" -> codeBuffer.append(countStringLength).append(",").append(e2.getVar().toString().length()).append(")");
                    case "integerstring", "realstring" -> codeBuffer.append(c.getId().length()).append(",").append(0).append(")");
                    case "stringinteger", "stringreal" -> codeBuffer.append(c.getId().length()).append(",").append(1).append(")");
                }
            } else if (e1.getStatement() instanceof CallProcOp c && e2.getOperation() != null && e2.getOperation() instanceof StrConcatOp concat) {
                Record r = typeEnvirornment.lookup(c.getId());
                String tp2 = "";
                if (r.getReturnType().equalsIgnoreCase("string")) {
                    countStringLength = countStringLength + 10;
                    codeBuffer.append("concatC_C(");
                    c.accept(this);
                    codeBuffer.deleteCharAt(codeBuffer.length() - 1);
                    codeBuffer.deleteCharAt(codeBuffer.length() - 1);
                    codeBuffer.append(",");
                    tp2 = "stringstring";
                } else if (r.getReturnType().equalsIgnoreCase("integer")) {
                    countStringLength = countStringLength + 10;
                    codeBuffer.append("concatC_I(");
                    c.accept(this);
                    codeBuffer.deleteCharAt(codeBuffer.length() - 1);
                    codeBuffer.deleteCharAt(codeBuffer.length() - 1);
                    codeBuffer.append(",");
                    tp2 = "integerstring";
                } else if (r.getReturnType().equalsIgnoreCase("real")) {
                    countStringLength = countStringLength + 10;
                    codeBuffer.append("concatC_F(");
                    c.accept(this);
                    codeBuffer.deleteCharAt(codeBuffer.length() - 1);
                    codeBuffer.deleteCharAt(codeBuffer.length() - 1);
                    codeBuffer.append(",");
                    tp2 = "realstring";
                }

                concat.accept(this);
                codeBuffer.append(",");
                countStringLength = countStringLength + c.getId().length();
                switch (tp2) {
                    case "stringstring" -> codeBuffer.append(c.getId().length()).append(",").append(countStringLength).append(")");
                    case "integerstring", "realstring" -> codeBuffer.append(countStringLength).append(",").append(0).append(")");
                }
            } else if (e2.getStatement() instanceof CallProcOp c && e1.getOperation() != null && e1.getOperation() instanceof StrConcatOp concat) {
                Record r = typeEnvirornment.lookup(c.getId());
                String tp1 = "";
                if (r.getReturnType().equalsIgnoreCase("string")) {
                    countStringLength = countStringLength + 10;
                    codeBuffer.append("concatC_C(");
                    concat.accept(this);
                    codeBuffer.append(",");
                    tp1 = "stringstring";
                } else if (r.getReturnType().equalsIgnoreCase("integer")) {
                    countStringLength = countStringLength + 10;
                    codeBuffer.append("concatC_I(");
                    concat.accept(this);
                    codeBuffer.append(",");
                    tp1 = "stringinteger";
                } else if (r.getReturnType().equalsIgnoreCase("real")) {
                    countStringLength = countStringLength + 10;
                    codeBuffer.append("concatC_F(");
                    concat.accept(this);
                    codeBuffer.append(",");
                    tp1 = "stringreal";
                }

                c.accept(this);
                codeBuffer.deleteCharAt(codeBuffer.length() - 1);
                codeBuffer.deleteCharAt(codeBuffer.length() - 1);
                codeBuffer.append(",");
                switch (tp1) {
                    case "stringstring" -> codeBuffer.append(countStringLength).append(",").append(c.getId().length()).append(")");
                    case "stringinteger", "stringreal" -> codeBuffer.append(countStringLength).append(",").append(1).append(")");
                }
            } else if (e1.getVar() != null && e2.getStatement() instanceof CallProcOp c) {
                Record r = typeEnvirornment.lookup(c.getId());
                String tp1 = "";
                if (e1.getVar() instanceof String s1 && r.getReturnType().equalsIgnoreCase("string")) {
                    countStringLength = countStringLength + s1.length();
                    codeBuffer.append("concatC_C(\"").append(s1).append("\",");
                    tp1 = "stringstring";
                } else if (e1.getVar() instanceof String s1 && r.getReturnType().equalsIgnoreCase("integer")) {
                    countStringLength = countStringLength + s1.length() + 10;
                    codeBuffer.append("concatC_I(\"").append(s1).append("\",");
                    tp1 = "stringinteger";
                } else if (e1.getVar() instanceof String s1 && r.getReturnType().equalsIgnoreCase("real")) {
                    countStringLength = countStringLength + s1.length() + 10;
                    codeBuffer.append("concatC_F(\"").append(s1).append("\",");
                    tp1 = "stringreal";
                } else if (e1.getVar() instanceof Id id1) {
                    if (getType(id1.toString()).equalsIgnoreCase("string") && r.getReturnType().equalsIgnoreCase("string")) {
                        countStringLength = countStringLength + id1.toString().length();
                        codeBuffer.append("concatC_C(").append(id1).append(",");
                        tp1 = "stringstring";
                    } else if (getType(id1.toString()).equalsIgnoreCase("integer") && r.getReturnType().equalsIgnoreCase("string")) {
                        countStringLength = countStringLength + 10;
                        codeBuffer.append("concatC_I(").append(id1).append(",");
                        tp1 = "integerstring";
                    } else if (getType(id1.toString()).equalsIgnoreCase("real") && r.getReturnType().equalsIgnoreCase("string")) {
                        countStringLength = countStringLength + 10;
                        codeBuffer.append("concatC_F(").append(id1).append(",");
                        tp1 = "realstring";
                    } else if (getType(id1.toString()).equalsIgnoreCase("string") && r.getReturnType().equalsIgnoreCase("integer")) {
                        countStringLength = countStringLength + id1.toString().length() + 10;
                        codeBuffer.append("concatC_I(").append(id1).append(",");
                        tp1 = "stringinteger";
                    } else if (getType(id1.toString()).equalsIgnoreCase("string") && r.getReturnType().equalsIgnoreCase("real")) {
                        countStringLength = countStringLength + id1.toString().length() + 10;
                        codeBuffer.append("concatC_F(").append(id1).append(",");
                        tp1 = "stringreal";
                    }

                }
                ((CallProcOp) e2.getStatement()).accept(this);
                codeBuffer.deleteCharAt(codeBuffer.length() - 1);
                codeBuffer.deleteCharAt(codeBuffer.length() - 1);
                codeBuffer.append(",");
                switch (tp1) {
                    case "stringstring" -> codeBuffer.append(e1.getVar().toString().length()).append(",").append(c.getId().length()).append(")");
                    case "integerstring", "realstring" -> codeBuffer.append(c.getId().length()).append(",").append(0).append(")");
                    case "stringinteger", "stringreal" -> codeBuffer.append(c.getId().length()).append(",").append(1).append(")");
                }

            } else if (e1.getVar() != null && e2.getOperation() != null && e2.getOperation() instanceof StrConcatOp) {
                String tp1 = "";

                if (e1.getVar() instanceof String s1) {
                    countStringLength = countStringLength + s1.length();
                    codeBuffer.append("concatC_C(\"").append(s1).append("\",");
                    tp1 = "string";
                } else if (e1.getVar() instanceof Id id1) {
                    if (getType(id1.toString()).equalsIgnoreCase("string")) {
                        countStringLength = countStringLength + id1.toString().length();
                        codeBuffer.append("concatC_C(").append(id1).append(",");
                        tp1 = "string";
                    } else if (getType(id1.toString()).equalsIgnoreCase("integer")) {
                        countStringLength = countStringLength + 10;
                        codeBuffer.append("concatC_I(").append(id1).append(",");
                        tp1 = "integer";
                    } else if (getType(id1.toString()).equalsIgnoreCase("real")) {
                        countStringLength = countStringLength + 10;
                        codeBuffer.append("concatC_F(").append(id1).append(",");
                        tp1 = "real";
                    }
                }
                e2.getOperation().accept(this);
                codeBuffer.append(",");
                switch (tp1) {
                    case "string" -> codeBuffer.append(e1.getVar().toString().length()).append(",").append(countStringLength).append(")");
                    case "integer", "real" -> codeBuffer.append(countStringLength).append(",").append(0).append(")");
                }


            } else if (e1.getOperation() != null && e1.getOperation() instanceof StrConcatOp && e2.getVar() != null) {
                String tp2 = "";
                if (e2.getVar() instanceof String s2) {
                    countStringLength = countStringLength + s2.length();
                    codeBuffer.append("concatC_C(");
                    tp2 = "string";
                } else if (e2.getVar() instanceof Id id2) {
                    if (getType(id2.toString()).equalsIgnoreCase("string")) {
                        countStringLength = countStringLength + id2.toString().length();
                        codeBuffer.append("concatC_C(");
                        tp2 = "string";
                    } else if (getType(id2.toString()).equalsIgnoreCase("integer")) {
                        countStringLength = countStringLength + 10;
                        codeBuffer.append("concatC_I(");
                        tp2 = "integer";
                    } else if (getType(id2.toString()).equalsIgnoreCase("real")) {
                        countStringLength = countStringLength + 10;
                        codeBuffer.append("concatC_F(");
                        tp2 = "real";
                    }
                }
                e1.getOperation().accept(this);
                codeBuffer.append(",");
                switch (tp2) {
                    case "string" -> codeBuffer.append("\"").append(e2.getVar().toString()).append("\"").append(",").append(countStringLength).append(",").append(e2.getVar().toString().length()).append(")");
                    case "integer", "real" -> codeBuffer.append(e2.getVar().toString()).append(",").append(countStringLength).append(",").append(1).append(")");
                }

            } else if (e1.getVar() instanceof String s1 && e2.getVar() instanceof String s2) {
                countStringLength = countStringLength + s1.length() + s2.length();
                codeBuffer.append("concatC_C(").append("\"").append(s1).append("\"").append(",").append("\"").append(s2).append("\"").append(",").append(s1.length()).append(",").append(s2.length()).append(")");
            } else if (e1.getVar() instanceof Id id1 && e2.getVar() instanceof Id id2) {
                if (getType(id1.toString()).equalsIgnoreCase("string") && getType(id2.toString()).equalsIgnoreCase("integer")) {
                    countStringLength = countStringLength + id1.toString().length() + 10;
                    codeBuffer.append("concatC_I(").append(id1).append(",").append(id2).append(",").append(id1.toString().length()).append(",").append(1).append(")");
                } else if (getType(id1.toString()).equalsIgnoreCase("string") && getType(id2.toString()).equalsIgnoreCase("real")) {
                    countStringLength = countStringLength + id1.toString().length() + 10;
                    codeBuffer.append("concatC_F(").append(id1).append(",").append(id2).append(",").append(id1.toString().length()).append(",").append(1).append(")");
                } else if (getType(id1.toString()).equalsIgnoreCase("integer") && getType(id2.toString()).equalsIgnoreCase("string")) {
                    countStringLength = countStringLength + id2.toString().length() + 10;
                    codeBuffer.append("concatC_I(").append(id2).append(",").append(id1).append(",").append(id2.toString().length()).append(",").append(0).append(")");
                } else if (getType(id1.toString()).equalsIgnoreCase("real") && getType(id2.toString()).equalsIgnoreCase("string")) {
                    countStringLength = countStringLength + id2.toString().length() + 10;
                    codeBuffer.append("concatC_F(").append(id2).append(",").append(id1).append(",").append(id2.toString().length()).append(",").append(0).append(")");
                }
            } else if (e1.getVar() instanceof String s1 && e2.getVar() instanceof Id id2) {
                if (getType(id2.toString()).equalsIgnoreCase("string")) {
                    countStringLength = countStringLength + s1.length() + id2.toString().length();
                    codeBuffer.append("concatC_C(").append("\"").append(s1).append("\"").append(",").append(id2).append(",").append(s1.length()).append(",").append(id2.toString().length()).append(")");
                } else if (getType(id2.toString()).equalsIgnoreCase("real")) {
                    countStringLength = countStringLength + s1.length() + 10;
                    codeBuffer.append("concatC_F(").append("\"").append(s1).append("\"").append(",").append(id2).append(",").append(s1.length()).append(",").append(1).append(")");
                } else if (getType(id2.toString()).equalsIgnoreCase("integer")) {
                    countStringLength = countStringLength + s1.length() + 10;
                    codeBuffer.append("concatC_I(").append("\"").append(s1).append("\"").append(",").append(id2).append(",").append(s1.length()).append(",").append(1).append(")");
                }
            } else if (e2.getVar() instanceof String s2 && e1.getVar() instanceof Id id1) {
                if (getType(id1.toString()).equalsIgnoreCase("string")) {
                    countStringLength = countStringLength + s2.length() + id1.toString().length();
                    codeBuffer.append("concatC_C(").append(id1).append(",").append("\"").append(s2).append("\"").append(",").append(id1.toString().length()).append(",").append(s2.length()).append(")");
                } else if (getType(id1.toString()).equalsIgnoreCase("real")) {
                    countStringLength = countStringLength + s2.length() + 10;
                    codeBuffer.append("concatC_F(").append("\"").append(s2).append("\"").append(",").append(id1).append(",").append(s2.length()).append(",").append(0).append(")");
                } else if (getType(id1.toString()).equalsIgnoreCase("integer")) {
                    countStringLength = countStringLength + s2.length() + 10;
                    codeBuffer.append("concatC_I(").append("\"").append(s2).append("\"").append(",").append(id1).append(",").append(s2.length()).append(",").append(0).append(")");
                }
            }
        }
        return null;
    }


    @Override
    public Object visit(ExprOp exprOp) {
        if (exprOp.getVar() != null) {
            if(exprOp.getVar() instanceof String){
                codeBuffer.append("\"").append(exprOp.getVar().toString()).append("\"");
            }
            else {
                codeBuffer.append(exprOp.getVar().toString());
            }
        } else if (exprOp.getOperation() != null) {

            exprOp.getOperation().accept(this);
        } else if (exprOp.getStatement() != null && exprOp.getStatement() instanceof CallProcOp c) {
            String types = typeEnvirornment.lookup(c.getId()).getReturnType();
            Record r =typeEnvirornment.lookup(c.getId());
            if (types != null) {
                if (types.equals("string")) {
                    if (r.getParamType().size() == 0) {
                        codeBuffer.append(c.getId()).append("()");
                    }
                }else {

                    c.accept(this);
                    //tolgo un ';' di troppo aggiunto sia da callproc che da operation
                    codeBuffer.deleteCharAt(codeBuffer.length() - 1);
                    codeBuffer.deleteCharAt(codeBuffer.length() - 1);

                }
            }
        }
        return "";
    }

    @Override
    public Object visit(IdListInitOp idListInitOp) {
        int i = 1;

        for (Map.Entry<String, ExprOp> entry : idListInitOp.getList().entrySet()) {
            String var = entry.getKey();
            //System.out.println(var + "Record: " + typeEnvirornment.lookup(var));
            if (typeEnvirornment.lookup(var) != null) {
                String type = typeEnvirornment.lookup(var).getType();
                ExprOp value = entry.getValue();
                if (value.getVar() != null) {
                    if (type.equals("string") && value.getVar() instanceof String) {
                        codeBuffer.append(var);
                        codeBuffer.append(" = \"").append(value.getVar()).append("\";\n");
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
                } else if (value.getVar() == null) {//caso in cui ho int v1,v = 5+5;
                    if (idListInitOp.getList().size() == 1) {
                        codeBuffer.append(var).append(";\n");
                    } else if (i < idListInitOp.getList().size()) {
                        codeBuffer.append(var).append(",");
                        i++;
                    } else {
                        codeBuffer.append(var).append(";\n");
                    }
                } else if (value.getOperation() != null) {
                    codeBuffer.append(var).append(" = ");

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
            codeBuffer.append(getTypeInC(false, procOp.getT().getTipo()));
        } else {
            codeBuffer.append("void");
        }
        //PARAMETRI PASSATI ALLA FUNZIONE
        if (!procOp.getId().toString().equals("main")) {
            codeBuffer.append(" ").append(procOp.getId()).append("(");
            if (procOp.getList() != null)
                procOp.getList().accept(this);
            else
                codeBuffer.append(") {");
            codeBuffer.append("\n");

        } else {
            codeBuffer.append(" ").append(procOp.getId()).append("(");
            codeBuffer.append("){\n");
            if (procOp.getList().getList().get(0).getId() != null) {
                for (ParDeclOp parDeclOp : procOp.getList().getList()) {
                    String id = parDeclOp.getId().toString();
                    boolean out = false;
                    if (parDeclOp.getOut() != null) {
                        out = parDeclOp.getOut().equalsIgnoreCase("out");
                    }
                    codeBuffer.append(getTypeInC(out, parDeclOp.getT().getTipo())).append(" ");

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

                e.getOperation().accept(this);
                codeBuffer.append(")\n");
            } else if (e.getStatement() instanceof CallProcOp c) {

                String ret = typeEnvirornment.lookup(c.getId()).getReturnType();


                if (ret.equals("void")) {
                    codeBuffer.append("printf(\"\");\n");
                }


            }
        }
        return null;
    }

    @Override
    public Object visit(VarDeclOp varDecl) {
        if (!varDecl.getTipo().getTipo().equals("var")) {
            codeBuffer.append(getTypeInC(false, varDecl.getTipo().getTipo())).append(" ");
            varDecl.getList().accept(this);
        } else {
            String type = "";
            for (Map.Entry<String, ExprOp> entry : varDecl.getList().getList().entrySet()) {
                String var = entry.getKey();
                //System.out.println(var + "Record: " + typeEnvirornment.lookup(var));
                if (typeEnvirornment.lookup(var) != null) {
                    Record rec = typeEnvirornment.lookup(var);
                    type = rec.getType();
                }
            }
            codeBuffer.append(getTypeInC(false, type)).append(" ");
            varDecl.getList().accept(this);
        }

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
        codeBuffer.append(getTypeInC(false, typeOp.getTipo()));
        return null;
    }

    @Override
    public Object visit(ReturnStatOp returnStatOp) {
        if (returnStatOp != null) {
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
            if (mode.equalsIgnoreCase("WRITE_LN")) {
                codeBuffer.append("printf(");
                codeBuffer.append("\"").append(s).append("\\n\"");
                codeBuffer.append(");\n");
            } else if (mode.equalsIgnoreCase("WRITE_")) {
                codeBuffer.append("printf(");
                codeBuffer.append("\"").append(s).append("\"");
                codeBuffer.append(");\n");
            } else if (mode.equalsIgnoreCase("WRITE_T")) {
                codeBuffer.append("printf(");
                codeBuffer.append("\"").append(s);
                codeBuffer.append("\\t\");\n");
            } else if (mode.equalsIgnoreCase("WRITE_B")) {
                codeBuffer.append("printf(");
                codeBuffer.append("\"").append(s).append("\"");
                codeBuffer.append("\\b\");\n");
            }
        } else if (e.getVar() instanceof Id id1) {
            if (mode.equalsIgnoreCase("WRITE_LN")) {
                codeBuffer.append("printf(");
                codeBuffer.append(id1);
                codeBuffer.append(");\n");
                codeBuffer.append("printf(\"\\n\");\n");
            } else if (mode.equalsIgnoreCase("WRITE_")) {
                codeBuffer.append("printf(");
                codeBuffer.append(id1);
                codeBuffer.append(");\n");
            } else if (mode.equalsIgnoreCase("WRITE_T")) {
                codeBuffer.append("printf(");
                codeBuffer.append(id1);
                codeBuffer.append(");\n");
                codeBuffer.append("printf(\"\\t\");\n");
            } else if (mode.equalsIgnoreCase("WRITE_B")) {
                codeBuffer.append("printf(");
                codeBuffer.append(id1);
                codeBuffer.append(");\n");
                codeBuffer.append("printf(\"\\b\");\n");
            }

        } else if (e.getVar() instanceof Integer i) {
            if (mode.equalsIgnoreCase("WRITE_LN")) {
                codeBuffer.append("printf(");
                codeBuffer.append(i);
                codeBuffer.append(");\n");
                codeBuffer.append("printf(\"\\n\");\n");
            } else if (mode.equalsIgnoreCase("WRITE_")) {
                codeBuffer.append("printf(");
                codeBuffer.append(i);
                codeBuffer.append(");\n");
            } else if (mode.equalsIgnoreCase("WRITE_T")) {
                codeBuffer.append("printf(");
                codeBuffer.append(i);
                codeBuffer.append(");\n");
                codeBuffer.append("printf(\"\\t\");\n");
            } else if (mode.equalsIgnoreCase("WRITE_B")) {
                codeBuffer.append("printf(");
                codeBuffer.append(i);
                codeBuffer.append(");\n");
                codeBuffer.append("printf(\"\\b\");\n");
            }
        } else if (e.getVar() instanceof Float f) {
            if (mode.equalsIgnoreCase("WRITE_LN")) {
                codeBuffer.append("printf(");
                codeBuffer.append(f);
                codeBuffer.append(");\n");
                codeBuffer.append("printf(\"\\n\");\n");
            } else if (mode.equalsIgnoreCase("WRITE_")) {
                codeBuffer.append("printf(");
                codeBuffer.append(f);
                codeBuffer.append(");\n");
            } else if (mode.equalsIgnoreCase("WRITE_T")) {
                codeBuffer.append("printf(");
                codeBuffer.append(f);
                codeBuffer.append(");\n");
                codeBuffer.append("printf(\"\\t\");\n");
            } else if (mode.equalsIgnoreCase("WRITE_B")) {
                codeBuffer.append("printf(");
                codeBuffer.append(f);
                codeBuffer.append(");\n");
                codeBuffer.append("printf(\"\\b\");\n");
            }
        } else if (e.getOperation() != null) {

            if (mode.equalsIgnoreCase("WRITE_LN")) {
                codeBuffer.append("printf(");
                //codeBuffer.append(getConvOp(e.getOperation().getOpType())).append("\",");

                e.getOperation().accept(this);
                codeBuffer.append(");\n");
                codeBuffer.append("printf(\"\\n\");\n");
            } else if (mode.equalsIgnoreCase("WRITE_")) {
                codeBuffer.append("printf(\"");
                codeBuffer.append(getConvOp(e.getOperation().getOpType())).append("\",");

                e.getOperation().accept(this);
                codeBuffer.append(")\n");
            } else if (mode.equalsIgnoreCase("WRITE_T")) {
                codeBuffer.append("printf(\"");
                codeBuffer.append(getConvOp(e.getOperation().getOpType())).append("\",");

                e.getOperation().accept(this);
                codeBuffer.append("\\t);\n");
            } else if (mode.equalsIgnoreCase("WRITE_B")) {
                codeBuffer.append("printf(\"");
                codeBuffer.append(getConvOp(e.getOperation().getOpType())).append("\",");

                e.getOperation().accept(this);
                codeBuffer.append("\\b);\n");
            }

        } else if (e.getStatement() instanceof CallProcOp c) {

            String ret = typeEnvirornment.lookup(c.getId()).getReturnType();
            if (ret.equals("void")) {
                codeBuffer.append("printf(\"\");\n");
            }
        }


        return null;
    }

    @Override
    public Object visit(WhileStatOp whileStatOp) {
        typeEnvirornment.enterScope(whileStatOp.getTable());
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
        typeEnvirornment.exitScope();
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
        } else if (statOp.getStatement() instanceof ReturnStatOp returnStatOp) {
            returnStatOp.accept(this);
        }
        return null;
    }

    @Override
    public Object visit(ReadStatOp readlnStatOp) {

        if (readlnStatOp.getExpr() != null) {
            codeBuffer.append("printf(").append(readlnStatOp.getExpr().accept(this)).append(");\n");
        }

        if (readlnStatOp.getIdList().getIdList().size() == 1) {
            System.out.println(readlnStatOp.getIdList().getIdList().get(0));
            Record rec = typeEnvirornment.lookup(readlnStatOp.getIdList().getIdList().get(0));
            if (getConvOp(rec.getType()).equals("%s")) {
                codeBuffer.append(readlnStatOp.getIdList().getIdList().get(0)).append(" = (char*) malloc(sizeof(char));\n");
            }
            codeBuffer.append("scanf(\"");
            codeBuffer.append(getConv(readlnStatOp.getIdList().getIdList().get(0)));
            if (getConvOp(rec.getType()).equals("%s")) {
                codeBuffer.append("\",").append(readlnStatOp.getIdList().getIdList().get(0)).append(");\n");
            } else
                codeBuffer.append("\",&").append(readlnStatOp.getIdList().getIdList().get(0)).append(");\n");
        } else {

            for (String id : readlnStatOp.getIdList().getIdList()) {

                if (getConv(id).equals("%s")) {
                    codeBuffer.append(id).append(" = (char*) malloc(sizeof(char));\n");
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
        typeEnvirornment.enterScope(ifStatOp.getTable());
        codeBuffer.append("if(");
        if (ifStatOp.getE().getOperation() != null) {

            ifStatOp.getE().getOperation().accept(this);
            codeBuffer.append("){\n");
        } else if (ifStatOp.getE().getVar() != null && ifStatOp.getE().getVar() instanceof Id id) {
            codeBuffer.append(id);
            codeBuffer.append("){\n");
        } else if (ifStatOp.getE().getStatement() != null && ifStatOp.getE().getStatement() instanceof CallProcOp c) {

            codeBuffer.append("\n");

            c.accept(this);
            codeBuffer.deleteCharAt(codeBuffer.length() - 1);
            codeBuffer.deleteCharAt(codeBuffer.length() - 1);
            codeBuffer.append("){\n");
        } else {
            codeBuffer.append(ifStatOp.getE().getVar());
            codeBuffer.append("){\n");
        }
        ifStatOp.getVars().accept(this);
        ifStatOp.getStatList().accept(this);
        codeBuffer.append("}\n");

        if (ifStatOp.getElseStat() != null) {
            ifStatOp.getElseStat().accept(this);
        }
        typeEnvirornment.exitScope();
        return null;
    }
}
