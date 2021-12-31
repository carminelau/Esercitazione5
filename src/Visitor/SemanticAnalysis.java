package Visitor;

import Node.*;
import Operation.*;
import Scope.Record;
import Statement.*;
import Scope.*;

import java.util.ArrayList;
import java.util.Map;

//modifiche
//riga 188, 198, 583, 586

public class SemanticAnalysis implements Visitor{

    private TypeEnviroment type = new TypeEnviroment();

    private void checkTypeProc(ExprOp exprOp) {
        /*
         * Controllo se ho più chiamate a funzione e variabili nell'assegnazione
         * Ad esempio ---->id,id1,id2,id3 := fun1(),9+4,fun2()
         * in questo caso devo sommare il numero dei valori di ritorno con il numero di operazioni
         * se questa somma è uguale al numero di variabili a sx dell'assegnazione non ci sono errori
         * */
            if (exprOp.getStatement() != null) {
                CallProcOp c = (CallProcOp) exprOp.getStatement();
                if (type.lookup(c.getId()) == null) {
                    throw new Error("Funzione " + c.getId() + " non dichiarata");
                }
            }
        }

    private void checkTypeProc(ArrayList<ExprOp> exprOps, ArrayList<String> idNames, int numVar) {
        /*
         * Controllo se ho più chiamate a funzione e variabili nell'assegnazione
         * Ad esempio ---->id,id1,id2,id3 := fun1(),9+4,fun2()
         * in questo caso devo sommare il numero dei valori di ritorno con il numero di operazioni
         * se questa somma è uguale al numero di variabili a sx dell'assegnazione non ci sono errori
         * */
        int returnNum = 0;
        int size = exprOps.size();
        while (size > 0) {
            if (exprOps.get(size - 1).getStatement() != null) {
                CallProcOp c = (CallProcOp) exprOps.get(size - 1).getStatement();
                if (type.lookup(c.getId()) == null) {
                    throw new Error("Funzione " + c.getId() + " non dichiarata");
                }
                returnNum += type.lookup(c.getId()).getReturnType().size();
            }
            size--;
        }
        if (idNames.size() != returnNum + numVar) {
            throw new Error("Errore sul numero di valori ritornati dalla funzione");
        }
    }

    public String arithmeticCheckType(ExprOp e) {
        //Se la variabile è un boolean oppure una stringa è un errore
        if (e.getVar() instanceof Boolean ) {
            throw new Error("Type missmatch1 ");
        }
        //Se è un id controllo nella tabella dei simboli il tipo
        if (e.getVar() instanceof Id id) {
            Record record = type.lookup(id.toString());
            if (record.getType().equals("string") || record.getType().equals("bool")) {
                throw new Error("Type missmatch2 ");
            } else {
                return record.getType();
            }
        }
        //Se è una chiamata a funzione controllo il tipo di ritorno
        if (e.getStatement() != null && e.getStatement() instanceof CallProcOp c) {
            ArrayList<String> types = type.lookup(c.getId()).getReturnType();
            if (types.size() == 1) {
                return types.get(0);
            } else {
                throw new Error("Errore");
            }
        }

        //Altrimenti prendo il tipo da getClass()
        return getType(e);
    }

    public String arithmeticCheckType(String e1Type, String e2Type) {

        if (e1Type.equals("integer") && e2Type.equals("integer")) {
            return "integer";
        } else if ((e1Type.equals("real") && e2Type.equals("integer")) ||
                (e2Type.equals("real") && e1Type.equals("integer"))) {
            return "real";
        } else if (e1Type.equals("real") && e2Type.equals("real")) {
            return "real";
        } else {
            throw new Error("Arithmetic operation type Missmatch: " + e1Type + "," + e2Type);

        }
    }

    public void booleanCheckType(ExprOp e) {

        if (e.getVar() instanceof Integer || e.getVar() instanceof String || e.getVar() instanceof Float) {
            throw new Error("Type missmatch3 ");
        }
        if (e.getVar() instanceof Id id) {
            Record record = type.lookup(id.toString());
            if (record.getType().equals("string") || record.getType().equals("integer") || record.getType().equals("real")) {
                throw new Error("Type missmatch4 ");
            }
        }
    }

    public void booleanCheckType(String e1Type) {
        if (e1Type.equals("integer") || e1Type.equals("string") || e1Type.equals("real")) {
            throw new Error("Type missmatch5 ");
        }

    }

    public void setNodeType(Operations op) {

        String e1Type, e2Type;
        if (op.getE1().getOperation() == null) {
            //caso semplice in cui ho due operandi (a op b)
            if (op instanceof EqOp || op instanceof NeOp || op instanceof LtOp || op instanceof LeOp || op instanceof GtOp || op instanceof GeOp)
                e1Type = relationalCheckType(op.getE1());
            else
                e1Type = arithmeticCheckType(op.getE1());
        } else {
            //nel caso in cui ho più di 2 operandi il tipo del primo operando è il tipo della prima operazione (a op b) op c
            e1Type = op.getE1().getOperation().getOpType();
        }
        if (op.getE2().getOperation() == null) {
            if (op instanceof EqOp || op instanceof NeOp || op instanceof LtOp || op instanceof LeOp || op instanceof GtOp || op instanceof GeOp)
                e2Type = relationalCheckType(op.getE2());
            else
                e2Type = arithmeticCheckType(op.getE2());
        } else
            e2Type = op.getE2().getOperation().getOpType();

        boolean b = (e1Type.equals("integer") || e1Type.equals("real")) && (e2Type.equals("integer") || e2Type.equals("real"));
        if (op instanceof EqOp || op instanceof NeOp || op instanceof LtOp || op instanceof LeOp || op instanceof GtOp || op instanceof GeOp) {
            //op.setType(relationalCheckType(e1Type,e2Type));

            if (!e1Type.equals(e2Type)) {
                if (b) {
                    op.setType("bool");
                    return;
                } else {
                    throw new Error(("Type mismatch1"));
                }
            }
            op.setType("bool");

        } else if (op instanceof StrConcatOp){
            if (!e1Type.equals(e2Type)) {
                if ((e1Type.equals("integer") || e1Type.equals("real") || e1Type.equals("string")) && (e2Type.equals("integer") || e2Type.equals("real") || e1Type.equals("string"))) {
                    op.setType("string");
                }
                else {
                    throw new Error(("Type mismatch2"));
                }
            } else {
                op.setType("string");
            }
        }else {
            op.setType(arithmeticCheckType(e1Type, e2Type));
        }
    }

    public String getType(ExprOp e) {
    /*
        ritorno il tipo dalla classe
        ad esempio java.lang.String diventa string
    */
        String regex = "(\\.)";
        String type = e.getVar().getClass().toString();
        for (String s : type.split(regex)) {
            type = s;
        }
        return type.toLowerCase();
    }

    public String relationalCheckType(ExprOp e) {

        if (e.getVar() instanceof Id id) {
            Record record = type.lookup(id.toString());
            return record.getType();
        } else if (e.getStatement() != null && e.getStatement() instanceof CallProcOp c) {
            if (type.lookup(c.getId()).getReturnType().size() > 1) {
                throw new Error("Non è possibile confrontare più valori");
            }
            return type.lookup(c.getId()).getReturnType().get(0);
        } else {
            return getType(e);
        }
    }

    @Override
    public Object visit(ProgramOp programOp) {
        type.enterScope(programOp.getGlobalTable());

        programOp.getVarDeclOpList().accept(this);//popola la tabella dei simboli con le variabili
        programOp.getProcOpList().accept(this);
        if(programOp.getMain() != null) {
            programOp.getMain().accept(this);
            Record r = new Record();
            r.setSym("main");
            r.setKind("method");

            type.addId(r);

        } else {
            //controllo se è stato implementato il metodo main altrimenti è un errore
            if (type.lookup("main") == null) {
                throw new Error("Deve esserci la funzione main");
            }
        }

        type.exitScope();
        return programOp;
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
        for (ParDeclOp parDeclOp : paramDeclListOp.getList()) {
            parDeclOp.accept(this);
        }
        return null;
    }

    @Override
    public Object visit(IdListOp idListOp) {
        for (String id : idListOp.getIdList()) {
            Record record = new Record();
            record.setKind("var");
            record.setSym(id);

            type.addId(record);
        }

        return null;
    }

    @Override
    public Object visit(AssignStatOp assignStatOp) {
        //id := var ----> controllo id se è stata dichiarata
        String id = assignStatOp.getId().toString();
            if (type.lookup(id) == null) {
                throw new Error("Variabile '" + id + "' non dichiarata");
            }

        /*
            variabile j che mi serve per confrontare correttamente i tipi di ritorno quando ho più chiamate a funzione
            ES ----> fun1() int,int;
                     fun2() bool;
                     a,b,c := fun1(),fun2();
                     a,b devono avere lo stesso tipo di ritorno della funzione fun1()
                     b deve avere il tipo di ritorno di fun2()
        */
        int j = 0;

        //controllo se il tipo dell'assegnazione è corretta
        ExprOp e = assignStatOp.getExpr();
        String idn = assignStatOp.getId().toString();

            /*
                controllo se il numero di variabili dell'assegnazione corrisponde
                ---> id := 5,7; Errore Semantico
            */
            if (e.getOperation() != null) {
                Record r = type.lookup(idn);
                if (e.getOperation().getOpType()!= null) {
                    if (e.getOperation().getOpType().equals("integer") && r.getType().equals("real")) {
                        //compatibile
                    } else if (!e.getOperation().getOpType().equals(r.getType())) {
                        throw new Error("Tipo " + assignStatOp.getId().toString() + " " +
                                r.getType() + " ma tipo espressione " + e.getOperation().getOpType());
                    }
                }
            } else if (e.getVar() != null) {
                //caso in cui ho a,b,c := func(),var;

                String idName = assignStatOp.getId().toString();

                if (e.getVar() instanceof Id id2) {
                    if (type.lookup(id2.toString()) == null || type.lookup(id2.toString())
                            .getKind().equals("method")) {
                        throw new Error(id2 + " non dichiarata");
                    }

                    if (type.lookup(id2.toString()).getType().equals("integer") &&
                            type.lookup(idName).getType().equals("real")) {
                        //Compatibile
                    } else if (!type.lookup(id2.toString()).getType().equals
                            (type.lookup(idName).getType())) {
                        throw new Error("Tipo variabile " + type.lookup(idName).getSym() + " " +
                                type.lookup(idName).getType() + " ma la variabile " +
                                type.lookup(id2.toString()).getSym() + " è " +
                                type.lookup(id2.toString()).getType());
                    }

                } else {
                    //è una costante
                    if (e.getType().contains("Integer") &&
                            type.lookup(idName).getType().equals("real")) {
                        //Compatibile
                    } else if (!e.getType().toLowerCase().contains(type.lookup(idName).getType())) {
                        throw new Error("Tipo variabile " + type.lookup(idName).getSym() + " " +
                                type.lookup(idName).getType() + " ma costante " + e.getType());
                    }

                }
            } else if (e.getStatement() != null && e.getStatement() instanceof CallProcOp callProcOp) {
                /*
                    caso in cui assegno ad una variabile il valore di ritorno di una funzione
                    a:=func() || a,b,c:=fun();
                */

                //controllo che i parametri passati alla funzione siano corretti
                callProcOp.accept(this);

                ArrayList<String> returnTypes = type.lookup(callProcOp.getId()).getReturnType();
                String idName2 = assignStatOp.getId().toString();

                    /*
                        caso in cui una funzione ritorna più valori ma cerco si assegarli ad un'unica variabile
                        fun() int,int,int
                        c := fun(). ERRORE
                     */
                if(returnTypes != null) {
                    if (returnTypes.size() > 1) {
                        throw new Error("Errore sul numero di valori dell'assegnazione");
                    } else {
                        checkTypeProc(e);
                    }

                    if (returnTypes.get(0).equals("integer") &&
                            type.lookup(idName2).getType().equals("real")) {
                        //Va bene
                    } else if (!returnTypes.get(0).equals(type.lookup(idName2).getType())) {
                        throw new Error("Tipo id " + type.lookup(idName2).getSym() + " " +
                                type.lookup(idName2).getType() +
                                " ma tipo ritorno funzione '" + callProcOp.getId() + "': " + returnTypes.get(0));
                    }
                }
            }
        return null;
    }

    @Override
    public Object visit(CallProcOp callProcOp) {
        //controllo che la funzione sia stata dichiarata correttamente prima dell'uso
        if (type.lookup(callProcOp.getId()) == null) {
            throw new Error("Funzione '" + callProcOp.getId() + "' non dichiarata");
        }

        /*
         *  controllo che il numero e il tipo dei parametri nella chiamata a funzione
         *  corrispondano alla dichiarazione della fuzione
         */

        if (callProcOp.getExprList() != null) {
            callProcOp.getExprList().accept(this);

            ArrayList<String> paramTypes = type.lookup(callProcOp.getId()).getParamType();

                int funNum = 0, varNum = 0;
                ArrayList<ExprOp> exprlist = callProcOp.getExprList().getExprlist();

                for (ExprOp e : callProcOp.getExprList().getExprlist()) {
                    if (e.getVar() != null || e.getOperation() != null) {
                        varNum++;
                    } else if (e.getStatement() != null && e.getStatement() instanceof CallProcOp) {
                        funNum++;
                    }
                }
                if (funNum == 0 && paramTypes.size() != varNum) {
                    //caso fun(a,b,c)
                    throw new Error("Errore");
                }
                int j = 0;

                for (int i = 0; i < exprlist.size(); i++) {
                    ExprOp e = exprlist.get(i);
                    if (e.getOperation() != null) {
                        e.getOperation().accept(this);
                        //func(4+8);
                        if (e.getOperation().getOpType().equals("integer") && paramTypes.get(j).equals("real")) {
                            //Compatibile
                        } else if (!e.getOperation().getOpType().equals(paramTypes.get(j))) {
                            throw new Error("Errore");
                        }
                        if (funNum > 0) {
                            j++;
                        }

                    } else if (e.getVar() != null) {
                        if (e.getVar() instanceof Id id) {
                            Record r = type.lookup(id.toString());
                            if (paramTypes.get(j).equals("real") && r.getType().equals("integer")) {
                                //Compatibile
                            } else if (!paramTypes.get(j).equals(r.getType())) {
                                throw new Error("Errore");
                            }
                        } else {
                            //é una costante
                            if (e.getType().contains("integer") &&
                                    paramTypes.get(j).equals("real")) {
                                //Compatibile
                            } else if (!e.getType().toLowerCase().contains(paramTypes.get(j))) {
                                throw new Error("Errore");
                            }
                        }
                        j++;
                    } else if (e.getStatement() != null && e.getStatement() instanceof CallProcOp c) {
                        c.accept(this);

                        ArrayList<String> returnTypes = type.lookup(c.getId()).getReturnType();
                        if (varNum == 0 && funNum == 1 && returnTypes.size() != paramTypes.size()) {
                            //caso fun(fun1());
                            throw new Error("Errore");
                        }
                        for (int retIndex = 0; retIndex < returnTypes.size(); retIndex++) {
                            if (varNum != 0 && funNum == 1 && returnTypes.size() + varNum != paramTypes.size()) {
                                //caso fun(fun1(),a,b);
                                throw new Error("Errore");
                            } else if (funNum > 1) {
                                //caso fun(fun1(),fun2()); || fun(fun1(),fun2(),a,b)
                                checkTypeProc(exprlist,returnTypes,varNum);
                            } else if (returnTypes.get(retIndex).equals("integer") &&
                                    paramTypes.get(j).equals("real")) {
                                //compatibile
                            } else if (!returnTypes.get(retIndex).equals(paramTypes.get(j))) {
                                throw new Error("Errore");
                            }
                            j++;
                        }
                    }
                }

        } else {
            //Caso in cui non passo parametri ad una funzione che richiede parametri
            if (!type.lookup(callProcOp.getId()).getParamType().get(0).equals("void")) {
                throw new Error("La funzione  '" + callProcOp.getId() + "' prende i parametri in input" +
                        " " + type.lookup(callProcOp.getId()).getParamType());
            }
        }
        return null;
    }

    @Override
    public Object visit(StatListOp statListOp) {
        for (StatOp stat : statListOp.getStatements()) {
            stat.accept(this);
        }
        return null;
    }

    @Override
    public Object visit(ElseOp elseOp) {
        if (elseOp.getStatList() != null) {
            elseOp.getStatList().accept(this);
        }
        return null;
    }

    @Override
    public Object visit(Operations operation) {
        operation.getE1().accept(this);
        if (operation.getE2() != null)
            operation.getE2().accept(this);

        //Operatori Aritmetici
        if (operation instanceof AddOp addOp) {
            //System.out.print("add: ");
            setNodeType(addOp);
        } else if (operation instanceof DiffOp diffOp) {
            //System.out.print("diff: ");
            setNodeType(diffOp);
        } else if (operation instanceof MulOp mulOp) {
            //System.out.print("mul: ");
            setNodeType(mulOp);
        } else if (operation instanceof DivOp divOp) {
            //System.out.print("div: ");
            setNodeType(divOp);
        }
        else if (operation instanceof DivIntOp divintOp) {
            //System.out.print("divint: ");
            setNodeType(divintOp);
        }
        else if (operation instanceof PowOp powOp) {
            //System.out.print("pow: ");
            setNodeType(powOp);
        }
        else if (operation instanceof StrConcatOp strconcatOp) {
            //System.out.print("str-concat: ");
            setNodeType(strconcatOp);
        }
        //Operatori Relazionali
        else if (operation instanceof GtOp gtOp) {
            //System.out.print("gt: ");
            setNodeType(gtOp);
        } else if (operation instanceof GeOp geOp) {
            //System.out.print("ge: ");
            setNodeType(geOp);
        } else if (operation instanceof LtOp ltOp) {
            //System.out.print("lt: ");
            setNodeType(ltOp);
        } else if (operation instanceof LeOp leOp) {
            //System.out.print("le: ");
            setNodeType(leOp);
        } else if (operation instanceof EqOp eqOp) {
            //System.out.print("eq: ");
            setNodeType(eqOp);
        } else if (operation instanceof NeOp neOp) {
            //System.out.print("ne: ");
            setNodeType(neOp);
        }
        //Operatori Booleani
        else if (operation instanceof AndOp andOp) {
            //System.out.print("and: ");
            if (andOp.getE1().getOperation() == null)
                booleanCheckType(andOp.getE1());
            else
                booleanCheckType(andOp.getE1().getOperation().getOpType());
            booleanCheckType(andOp.getE2());
            //Se non ci sono stati errori setto il tipo del nodo a "bool"
            andOp.setType("bool");
        } else if (operation instanceof OrOp orOp) {
            //System.out.print("or: ");
            if (orOp.getE1().getOperation() == null)
                booleanCheckType(orOp.getE1());
            else
                booleanCheckType(orOp.getE1().getOperation().getOpType());
            booleanCheckType(orOp.getE2());
            //Se non ci sono stati errori setto il tipo del nodo a "bool"
            orOp.setType("bool");
        } else if (operation instanceof NotOp notOp) {
            //System.out.print("not: ");
            booleanCheckType(notOp.getE1());
            //Se non ci sono stati errori setto il tipo del nodo a "bool"
            notOp.setType("bool");
        }
        //System.out.println(operation.getOpType());
        return null;
    }

    @Override
    public Object visit(IdListInitOp idListInitOp) {
        for (Map.Entry<String, ExprOp> entry : idListInitOp.getList().entrySet()) {

            String var = entry.getKey();
            Record record = new Record();

            record.setSym(var);
            record.setKind("var");

            type.addId(record);
        }

        return null;
    }

    @Override
    public Object visit(ParDeclOp parDeclOp) {
        if (parDeclOp.getT() == null) {
            parDeclOp.accept(this);

            String id = parDeclOp.getId().toString();
            Record record = type.lookup(id);
            record.setType(parDeclOp.getT().getTipo());
        }
        return null;
    }

    @Override
    public Object visit(ProcOp procOp) {
         /*
            inserisco nella tabella il record
            contenente l'id del metodo, il kind = "method" e il tipo
        */

        Record r = new Record();
        r.setSym(procOp.getId().toString());
        r.setKind("method");
        ArrayList<String> paramType = new ArrayList<>();

        /*Aggiungo al record i tipi dei parametri in input alla funzione*/
        if (procOp.getList() != null) {
            for (ParDeclOp p : procOp.getList().getList()) {
                if (p.getId().toString() != null) {
                     paramType.add(p.getT().getTipo());
                    }
                }
            }
        r.setParamType(paramType);

        type.addId(r);

        //inserisco nella tabella figlia della funzione le variabili locali alla funzione e i parametri
        type.enterScope(procOp.getTable());//crea una nuova tabella figlia
        for(ParDeclOp params : procOp.getList().getList()){
            if (type.lookup(params.getId().toString()) == null) {
                Record r1= new Record(params.getId().toString(),"var",params.getT().getTipo());
                type.addId(r1);
            }
        }

        procOp.getVars().accept(this);

        //infine devo controllare il tipo di ritorno
        ArrayList<String> resultTyes = type.cercaMetodo(procOp.getId().toString()).getReturnType();
        if (resultTyes != null) {
            //Se c'è una variabile con lo stesso nome del metodo?
            if (resultTyes.size() > 1) {
                //una funzione non può restituire ad es. void,int
                throw new Error("La funzione " + procOp.getId() + " non può ritornare " + resultTyes);
            }
        }
        //continuo la visita controllando gli statment interni alla funzione
        if (procOp.getStats() != null)
            procOp.getStats().accept(this);

        type.exitScope();
        return null;
    }

    @Override
    public Object visit(VarDeclOp varDecl) {
        varDecl.getList().accept(this);

        String tipo = varDecl.getTipo().toString();
        Record r;

        for (Map.Entry<String, ExprOp> entry : varDecl.getList().getList().entrySet()) {

            r = type.lookup(entry.getKey());
            if (tipo.equals("var")) {
                if (entry.getValue().getType().toLowerCase().contains("integer")) {
                    r.setType("integer");
                } else if (entry.getValue().getType().toLowerCase().contains("real")) {
                    r.setType("real");
                } else if (entry.getValue().getType().toLowerCase().contains("bool")) {
                    r.setType("bool");
                } else if (entry.getValue().getType().toLowerCase().contains("string")) {
                    r.setType("string");
                }
            } else {
                r.setType(tipo);
            }

            //controllo che il tipo della dichairazione corrisponda all'assegnazione int a := 5 --> ok, int a := false --> Error

            if (entry.getValue().getOperation() != null) {

                //se ho real num := 4.8 + 8 ;
                entry.getValue().getOperation().accept(this);
                if (entry.getValue().getOperation().getOpType() != null && entry.getValue().getOperation().getOpType().equals("integer") && r.getType().equals("real")) {
                    //compatibile
                } else if (entry.getValue().getOperation().getOpType() != null && entry.getValue().getOperation().getOpType().equals(r.getType())) {
                    //System.out.println(entry.getKey() + " --> " + entry.getValue().getOperation().getOpType());
                } else {
                    throw new Error("Type missmatch6");
                }
            } else if (entry.getValue().getVar() != null) {
                if (entry.getValue().getVar() instanceof Id id) {
                    if (type.lookup(id.toString()) == null) {
                        throw new Error("Variabile " + id.toString() + " non dichiarata");
                    } else if (r.getType().equals(type.lookup(id.toString()).getType())) {
                        //OK
                    } else if (type.lookup(id.toString()).getType().equals("integer") && r.getType().equals("real")) {
                        System.out.println("no");
                    } else {
                        throw new Error("Type missmatch7 ");
                    }
                } else if (r.getType().equals("integer") && entry.getValue().getType().toLowerCase().contains("integer")) {
                    //System.out.println(entry.getKey() + " --> " + entry.getValue().getType());
                    //Compatibile
                } else if (r.getType().equals("real") && (entry.getValue().getType().toLowerCase().contains("real") || entry.getValue().getType().toLowerCase().contains("integer"))) {
                    //Compatibile
                    //System.out.println(entry.getKey() + " --> " + entry.getValue().getType());
                } else if (entry.getValue().getType().toLowerCase().contains(r.getType()) || entry.getValue().getType().equals("Null")) {
                    //Compatibile
                    //System.out.println(entry.getKey() + " --> " + entry.getValue().getType());
                } else if (r.getType().equals("var")) {
                    if(entry.getValue().getType().toLowerCase().contains("integer")){
                        r.setType("integer");
                    } else if (entry.getValue().getType().toLowerCase().contains("real")){
                        r.setType("real");
                    } else if (entry.getValue().getType().toLowerCase().contains("bool")){
                        r.setType("bool");
                    }
                    //Compatibile
                }else {

                    throw new Error("Type missmatch8 ");
                }
            } else if (entry.getValue().getStatement() != null) {
                //caso in cui assegno ad un'inizializzazione il valore di ritorno di una funzione---> int a:=func();
                if (entry.getValue().getStatement() instanceof CallProcOp callProcOp) {
                    if (type.lookup(callProcOp.getId()) == null) {
                        throw new Error("Funzione '" + callProcOp.getId() + "' non dichiarata");
                    }
                    ArrayList<String> returnTypes = type.lookup(callProcOp.getId()).getReturnType();
                    String returnType = returnTypes.get(returnTypes.size() - 1);
                    if (returnTypes.size() > 1) {
                        //caso in cui ho una funzione che ritorna più tipi non posso assegnarla ad un'unica variabile
                        throw new Error("Tipo di ritorno funzione: '" +
                                callProcOp.getId() + "' " + returnTypes + " ma tipo id: '" + entry.getKey() + "' " +
                                type.lookup(entry.getKey()).getType());
                    } else if (type.lookup(entry.getKey()).getType().equals("real") &&
                            returnType.equals("integer")) {
                        //Va bene
                    } else if (!returnType.equals(type.lookup(entry.getKey()).getType())) {
                        throw new Error("Tipo di ritorno funzione '" + callProcOp.getId() + "' " + returnType + " ma tipo " +
                                "id '" + entry.getKey() + "' " + type.lookup(entry.getKey()).getType());
                    }
                }
            }
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
    public Object visit(WriteStatOp writeStatOp) {
        if (writeStatOp.getExprList() != null) {
            writeStatOp.getExprList().accept(this);
        }
        return null;
    }

    @Override
    public Object visit(WhileStatOp whileStatOp) {
        if (whileStatOp.getE() != null) {
            whileStatOp.getE().accept(this);

            if (whileStatOp.getE().getOperation() != null) {
                if (!whileStatOp.getE().getOperation().getOpType().equals("bool")) {
                    throw new Error("La condizione del while deve essere di tipo bool");
                }
            } else if (whileStatOp.getE().getVar() != null) {
                if (whileStatOp.getE().getVar() instanceof Id id) {
                    if (!type.lookup(id.toString()).getType().equals("bool")) {
                        throw new Error("La variabile " + id.toString() + "deve essere di tipo bool");
                    }
                } else if (!whileStatOp.getE().getType().contains("bool")) {
                    throw new Error("La condizione del while deve essere di tipo bool");
                }
            }
        }
        if (whileStatOp.getStatListOp() != null) {
            for (StatOp statOp : whileStatOp.getStatListOp().getStatements()) {
                statOp.accept(this);
            }
        }
        if (whileStatOp.getStatListOp() != null) {
            for (StatOp statOp : whileStatOp.getStatListOp().getStatements()) {
                statOp.accept(this);
            }
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
    public Object visit(ReadStatOp readlnStatOp) {
        for (String id : readlnStatOp.getIdList().getIdList()) {
            if (type.lookup(id) == null) {
                throw new Error("Variabile '" + id + "' non dichiarata");
            }else if(type.lookup(id).getKind()!="var"){
                throw new Error("Metodo '" + id + "' non dichiarata");
            }
        }
        return null;
    }

    @Override
    public Object visit(IfStatOp ifStatOp) {
        ifStatOp.getE().accept(this);

        if (ifStatOp.getE().getOperation() != null) {
            String ifType = ifStatOp.getE().getOperation().getOpType();
            if (!ifType.equals("bool")) {
                throw new Error("Tipo richiesto in ifStat è bool ma il tipo dell'espressione è: " +
                        ifStatOp.getE().getOperation().getOpType());
            }
        } else if (ifStatOp.getE().getVar() != null) {
            if (ifStatOp.getE().getVar() instanceof Id id) {
                if (type.lookup(id.toString()) == null) {
                    throw new Error("variabile " + id.toString() + " non dichiarata");
                } else {
                    if (!type.lookup(id.toString()).getType().equals("bool")) {
                        throw new Error("variabile " + id.toString() + " deve essere di tipo bool");
                    }
                }
            } else {
                if (!ifStatOp.getE().getType().contains("bool")) {
                    throw new Error("Costante " + ifStatOp.getE() + " deve essere di tipo bool");
                }
            }
        } else if (ifStatOp.getE().getStatement() != null && ifStatOp.getE().getStatement() instanceof CallProcOp c) {
            if (type.lookup(c.getId()) == null) {
                throw new Error("funzione " + c.getId() + "non dichiarata");
            } else {
                if (type.lookup(c.getId()).getReturnType().size() > 1) {
                    throw new Error("Errore sul numero di parametri ritornati dalla funzione " + c.getId());
                } else if (type.lookup(c.getId()).getReturnType().size() == 1) {
                    if (!type.lookup(c.getId()).getReturnType().get(0).equals("bool")) {
                        throw new Error("La funzione " + c.getId() + " deve ritronare un bool");
                    }
                }
            }
        }
        if (ifStatOp.getStatList() != null)
            ifStatOp.getStatList().accept(this);
        if (ifStatOp.getElseStat() != null)
            ifStatOp.getElseStat().accept(this);
        return null;
    }

    @Override
    public Object visit(ExprListOp exprListOp) {
        for (ExprOp exprOp : exprListOp.getExprlist()) {
            exprOp.accept(this);
        }
        return null;
    }

    @Override
    public Object visit(ExprOp exprOp) {
        if (exprOp.getVar() instanceof Id) {
            String id = ((Id) exprOp.getVar()).toString();
            if (type.lookup(id) == null) {
                throw new Error("Variabile '" + id + "' non dichiarata");
            }else if(!type.lookup(id).getKind().equals("var")){
                throw new Error("Metodo '" + id + "' non dichiarata");
            }
        } else if (exprOp.getOperation() != null) {
            exprOp.getOperation().accept(this);
        } else if (exprOp.getStatement() != null && exprOp.getStatement() instanceof CallProcOp c) {
            c.accept(this);
        }
        return null;
    }

    @Override
    public Object visit(TypeOp typeOp) {
        if (typeOp.toString() == null){
            throw new Error("Tipo null");
        } else if (!typeOp.toString().equals("bool") || !typeOp.toString().equals("real") || !typeOp.toString().equals("integer") || !typeOp.toString().equals("string") ){
            throw new Error("Tipo non valido");
        }
        return null;
    }

    @Override
    public Object visit(ReturnStatOp returnStatOp) {
        if (returnStatOp.getExpr().getVar() instanceof Id) {
            String id = ((Id) returnStatOp.getExpr().getVar()).toString();
            if (type.lookup(id) == null) {
                throw new Error("Variabile di ritorno'" + id + "' non dichiarata");
            }else if(!type.lookup(id).getKind().equals("var")){
                throw new Error("Metodo '" + id + "' non dichiarata");
            }
        } else if (returnStatOp.getExpr().getOperation() != null) {
            returnStatOp.getExpr().getOperation().accept(this);
        } else if (returnStatOp.getExpr().getStatement() != null && returnStatOp.getExpr().getStatement() instanceof CallProcOp c) {
            c.accept(this);
        }
        return null;
    }

    @Override
    public Object visit(MainOp main) {
        type.enterScope(main.getGlobalTable());

        main.getVarDeclOpList().accept(this);//popola la tabella dei simboli con le variabili
        main.getStats().accept(this);

        type.exitScope();
        return main;
    }
}

