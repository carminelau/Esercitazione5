package tester;

import Node.ProgramOp;
import Visitor.CcodeGen;
import Visitor.SemanticAnalysis;
import Visitor.XmlGenerator;
import generated.Lexer;
import java_cup.parser;
import org.w3c.dom.Document;

import java.io.*;
import java.util.regex.Pattern;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class cGenTester {

    public static void main(String[] args) throws Exception {
        FileReader inFile = new FileReader(args[0]);
        Lexer lexer = new Lexer(inFile);
        parser p = new parser(lexer);
        //System.out.println(p.debug_parse().parse_state);
        ProgramOp prog = (ProgramOp) p.parse().value;

        //Albero Sintattico
        XmlGenerator xml = new XmlGenerator();
        Document doc = (Document) prog.accept(xml);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(doc);
        StreamResult streamResult = new StreamResult(new File(System.getProperty("user.dir") + "\\albero_sintattico.xml"));
        transformer.transform(domSource, streamResult);

        //Analisi semantica
        SemanticAnalysis sem = new SemanticAnalysis();
        prog.accept(sem);

        //Traduzione C
        CcodeGen ccodeGen = new CcodeGen();
        prog.accept(ccodeGen);
        String cName = args[0].split(Pattern.quote("."))[0];


        try {
            FileOutputStream file = new FileOutputStream(cName+".c");
            PrintStream Output = new PrintStream(file);
            Output.println(ccodeGen.getCodeBuffer());
        } catch (IOException e) {
            System.out.println("Errore: " + e);
            System.exit(1);
        }
    }
}
