package tester;

import generated.Lexer;
import generated.Parser;

public class ParserTest {

    public static void main(String[] args) throws Exception {
        Lexer lexer = new Lexer();
        if (lexer.initialize(args[0])){
            Parser parser = new Parser(lexer);
            System.out.println(parser.parse().value);
        }
        else {
            System.out.println("File not found");
        }
    }
}
