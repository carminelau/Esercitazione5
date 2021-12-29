package generated;
import java.io.*;import java.util.HashMap;
import java_cup.runtime.Symbol;
%%
%class Lexer
%cupsym ParserSym
%public
%cup
%line
%column
%unicode
%state COMMENT
%state COMMENT_LINE
%state STRING
%state STRINGSINGLE

%init{
// inserimento delle parole chiavi nella stringTable per evitare di scrivere un diagramma di transizione per ciascuna di esse (le parole chiavi verranno "catturate" dal diagramma di transizione e gestite e di conseguenza).
    stringTable.put("if", new Symbol(ParserSym.IF,"IF"));
    stringTable.put("then", new Symbol(ParserSym.THEN,"THEN"));
    stringTable.put("else", new Symbol(ParserSym.ELSE,"ELSE"));
    stringTable.put("while", new Symbol(ParserSym.WHILE,"WHILE"));
    stringTable.put("integer", new Symbol(ParserSym.INTEGER,"INTEGER"));
    stringTable.put("real", new Symbol(ParserSym.REAL,"REAL"));
    stringTable.put("bool", new Symbol(ParserSym.BOOL,"BOOL"));
    stringTable.put("fun", new Symbol(ParserSym.FUN,"FUN"));
    stringTable.put("loop", new Symbol(ParserSym.LOOP,"LOOP"));
    stringTable.put("and", new Symbol(ParserSym.AND,"AND"));
    stringTable.put("or", new Symbol(ParserSym.OR,"OR"));
    stringTable.put("not", new Symbol(ParserSym.NOT,"NOT"));
    stringTable.put("true", new Symbol(ParserSym.TRUE,"TRUE"));
    stringTable.put("false", new Symbol(ParserSym.FALSE,"FALSE"));
    stringTable.put("null", new Symbol(ParserSym.NULL,"NULL"));
    stringTable.put("return", new Symbol(ParserSym.RETURN,"RETURN"));
    stringTable.put("end", new Symbol(ParserSym.END,"END"));
    stringTable.put("main", new Symbol(ParserSym.MAIN,"MAIN"));
    stringTable.put("string", new Symbol(ParserSym.STRING,"STRING"));
    stringTable.put("outpar", new Symbol(ParserSym.OUTPAR,"OUTPAR"));
    stringTable.put("var", new Symbol(ParserSym.VAR,"VAR"));
    stringTable.put("out", new Symbol(ParserSym.OUT,"OUT"));
    stringTable.put("write", new Symbol(ParserSym.WRITE,"WRITE"));
    stringTable.put("writet", new Symbol(ParserSym.WRITET,"WRITET"));
    stringTable.put("writeln", new Symbol(ParserSym.WRITELN,"WRITELN"));
    stringTable.put("writeb", new Symbol(ParserSym.WRITEB,"WRITEB"));
%init}

%{
StringBuffer string = new StringBuffer();
  public Symbol Symbol( int tokenType ) {
      System.err.println( "Obtain token " + ParserSym.terminalNames[tokenType] );
      return new Symbol( tokenType, yyline, yycolumn);
  }

  public Symbol Symbol( int tokenType, Object value ) {
      System.err.println( "Obtain token " +ParserSym.terminalNames[tokenType] + " \"" + value + "\"" );
        return new Symbol( tokenType, yyline, yycolumn, value);
    }
    private static HashMap<String, Symbol> stringTable= new HashMap<>();

    private Symbol installID(String lex){
        Symbol sym;

        if(stringTable.containsKey(lex)){
            System.err.println( "Obtain token " + ParserSym.terminalNames[stringTable.get(lex).sym] + " \"" + lex + "\"" );

            return new Symbol(stringTable.get(lex).sym,lex);
        }
        else{
            sym = new Symbol(ParserSym.ID,lex);
            System.err.println( "Obtain token " + "ID" + " \"" + lex + "\"" );
            stringTable.put(lex,sym);
            return sym;
        }
    }

    public boolean initialize(String filePath) {
            try {
                /* the input device */
                this.zzReader = new java.io.FileReader(filePath);
                return true;
            } catch (java.io.FileNotFoundException e) {
                return false;
            }
        }
    public  Lexer() {
        stringTable.put("if", new Symbol(ParserSym.IF,"IF"));
        stringTable.put("then", new Symbol(ParserSym.THEN,"THEN"));
        stringTable.put("else", new Symbol(ParserSym.ELSE,"ELSE"));
        stringTable.put("while", new Symbol(ParserSym.WHILE,"WHILE"));
        stringTable.put("integer", new Symbol(ParserSym.INTEGER,"INTEGER"));
        stringTable.put("real", new Symbol(ParserSym.REAL,"REAL"));
        stringTable.put("bool", new Symbol(ParserSym.BOOL,"BOOL"));
        stringTable.put("fun", new Symbol(ParserSym.FUN,"FUN"));
        stringTable.put("loop", new Symbol(ParserSym.LOOP,"LOOP"));
        stringTable.put("and", new Symbol(ParserSym.AND,"AND"));
        stringTable.put("or", new Symbol(ParserSym.OR,"OR"));
        stringTable.put("not", new Symbol(ParserSym.NOT,"NOT"));
        stringTable.put("true", new Symbol(ParserSym.TRUE,"TRUE"));
        stringTable.put("false", new Symbol(ParserSym.FALSE,"FALSE"));
        stringTable.put("null", new Symbol(ParserSym.NULL,"NULL"));
        stringTable.put("return", new Symbol(ParserSym.RETURN,"RETURN"));
        stringTable.put("end", new Symbol(ParserSym.END,"END"));
        stringTable.put("main", new Symbol(ParserSym.MAIN,"MAIN"));
        stringTable.put("string", new Symbol(ParserSym.STRING,"STRING"));
        stringTable.put("outpar", new Symbol(ParserSym.OUTPAR,"OUTPAR"));
        stringTable.put("var", new Symbol(ParserSym.VAR,"VAR"));
        stringTable.put("out", new Symbol(ParserSym.OUT,"OUT"));
        stringTable.put("write", new Symbol(ParserSym.WRITE,"WRITE"));
        stringTable.put("writet", new Symbol(ParserSym.WRITET,"WRITET"));
        stringTable.put("writeln", new Symbol(ParserSym.WRITELN,"WRITELN"));
        stringTable.put("writeb", new Symbol(ParserSym.WRITEB,"WRITEB"));
    }
%}

//%eofval{
//	return Symbol(ParserSym.EOF);
//%eofval}

//WhiteSpaces
LineTerminator = (\r|\n|\r\n)
WhiteSpace = ({LineTerminator}|[ \t\f])+

//Numbers
Zero = 0
DecInt = [1-9][0-9]*
INTEGER_CONST = ( {Zero} | {DecInt} )
Exponent = [E] [\+\-]? [0-9]+
Float1 = [\+\-]? [0-9]+ \. [0-9]+ {Exponent}?
Float2 = [\+\-]? [0-9]+ {Exponent}
REAL_CONST = ( {Float1} | {Float2} )

//Identifiers
Ident = [$_A-Za-z][$_A-Za-z0-9]*

/*Declares a lexical state STRING */

%%

/*If the scanner is in lexical state STRING, only
  expressions that are preceded by the start condition <STRING> can be matched*/
<STRING> {
  \" {
    yybegin(YYINITIAL);
    return Symbol(ParserSym.STRING_CONST, string.toString());
  }
  [^\n\r\"\\] + { string.append( yytext() ); }
  \\t { string.append('\t'); }
  \\n { string.append('\n'); }
  \\r { string.append('\r'); }
  \\\" { string.append('\"'); }
  \\  { string.append('\\'); }
}

<STRINGSINGLE> {
  \' {
    yybegin(YYINITIAL);
    return Symbol(ParserSym.STRING_CONST, string.toString());
  }
  [^\n\r\'\\] + { string.append( yytext() ); }
  \\t { string.append('\t'); }
  \\n { string.append('\n'); }
  \\r { string.append('\r'); }
  \\\' { string.append('\''); }
  \\  { string.append('\\'); }
}
//COMMENT
    <COMMENT> {
       "#"     {yybegin(YYINITIAL);}
       [^"#"]  {/* ignore */}
    }

    <COMMENT_LINE> {
       {LineTerminator}    { yybegin(YYINITIAL); }
        [^\r\n\r\n]+       { /* Do Nothing */}
    }


<YYINITIAL> {

    //KEYWORDS
    if { return installID(yytext()); }
    then { return installID(yytext()); }
    else { return installID(yytext()); }
    while { return installID(yytext()); }
    integer {return installID(yytext());}
    real {return installID(yytext());}
    bool {return installID(yytext());}
    fun {return installID(yytext());}
    loop {return installID(yytext());}
    and {return installID(yytext());}
    or {return installID(yytext());}
    not {return installID(yytext());}
    true {return installID(yytext());}
    false {return installID(yytext());}
    null {return installID(yytext());}
    return {return installID(yytext());}
    end {return installID(yytext());}
    main {return installID(yytext());}
    string {return installID(yytext());}
    var {return installID(yytext());}
    out {return installID(yytext());}
    outpar {return installID(yytext());}
    write {return installID(yytext()); }
    writeln {return installID(yytext());}
    writeb {return installID(yytext());}
    writet {return installID(yytext());}

    //WHITESPACE
    {WhiteSpace} { }

    //NUMBER
    {INTEGER_CONST} { return Symbol(ParserSym.INTEGER_CONST, yytext()); }
    {REAL_CONST} { return Symbol(ParserSym.REAL_CONST, yytext()); }

    \" { string.setLength(0); yybegin(STRING); }

    \' { string.setLength(0); yybegin(STRINGSINGLE); }
    //SYMBOL
    "(" { return Symbol(ParserSym.LPAR, "LPAR"); }
    ")" { return Symbol(ParserSym.RPAR, "RPAR"); }
    "," { return Symbol(ParserSym.COMMA, "COMMA"); }
    ";" { return Symbol(ParserSym.SEMI, "SEMI"); }
    ":" { return Symbol(ParserSym.COLON, "COLON"); }
    "%" { return Symbol(ParserSym.READ, "READ"); }
    "?" { return Symbol(ParserSym.WRITE, "WRITE"); }
    "?." { return Symbol(ParserSym.WRITELN, "WRITELN"); }
    "?," { return Symbol(ParserSym.WRITEB, "WRITEB"); }
    "?:" { return Symbol(ParserSym.WRITET, "WRITET"); }

    //RELOP
    "<" { return Symbol(ParserSym.LT, "LT"); }
    "=" { return Symbol(ParserSym.EQ, "EQ"); }
    ">" { return Symbol(ParserSym.GT, "GT"); }
    "<=" { return Symbol(ParserSym.LE, "LE"); }
    "<>" | "!=" { return Symbol(ParserSym.NE, "NE"); }
    "!=" { return Symbol(ParserSym.NE, "NE"); }
    ":=" { return Symbol(ParserSym.ASSIGN, "ASSIGN"); }
    ">=" { return Symbol(ParserSym.GE, "GE"); }

    //OPERATIONS
    "+" { return Symbol(ParserSym.PLUS, "PLUS"); }
    "-" { return Symbol(ParserSym.MINUS, "MINUS"); }
    "*" { return Symbol(ParserSym.TIMES, "TIMES"); }
    "/" { return Symbol(ParserSym.DIV, "DIV"); }
    "^" { return Symbol(ParserSym.POW,"POW"); }
    "&" { return Symbol(ParserSym.STR_CONCAT,"STR_CONCAT"); }
    "@" { return Symbol(ParserSym.OUTPAR,"OUTPAR"); }
    "div" {return Symbol(ParserSym.DIVINT,"DIVINT");}



    //IDENTIFIERS
    {Ident} { return installID(yytext()); }

    //COMMEMTS
    "#*"  {yybegin(COMMENT);}
    "#"   {yybegin(COMMENT_LINE);}
    "//"  {yybegin(COMMENT_LINE);}

    //ERRORS
    <<EOF>> { return Symbol(ParserSym.EOF); }
    . { return Symbol(ParserSym.error, "ERROR"); }

    }

