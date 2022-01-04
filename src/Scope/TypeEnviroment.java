package Scope;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Stack;

public class TypeEnviroment {
    private Stack<HashMap<String,Record>> stack = new Stack<>();

    public TypeEnviroment(){

    }

    public void enterScope(LinkedHashMap<String,Record> table) {
        stack.push(table);
    }

    public Record cercaMetodo(String sym){
        HashMap<String,Record> currentTable =  stack.get(0);
        return currentTable.get(sym);
    }

    public Record lookup(String sym) {

        for (int i = stack.size()-1 ; i >= 0; i--) {

            HashMap<String, Record> currentTable = stack.get(i);
            if (currentTable != null) {
                if (currentTable.containsKey(sym)) {
                    return currentTable.get(sym);
                }
            }
        }
        return null;
    }
    /*
     * Aggiunge il symbol nella tabella corrente
     * */
    public void addId(Record x){
        HashMap<String, Record> map = stack.pop(); //table

        if(!x.equals(map.get(x.getSym()))) {
            map.put(x.getSym(), x);
        }else{
            if(x.getKind().equals("var"))
                throw new Error("Variabile " + x.getSym() + " già dichiarata");
            else{
                throw new Error("Metodo " + x.getSym() + " già dichiarata");
            }
        }
        stack.push(map);
    }

    public boolean probe(){
        return true;
    }

    public void exitScope(){
        if(stack.size()>0)
            stack.pop();
    }

    @Override
    public String toString() {
        return "stack=\n" + stack;
    }
}
