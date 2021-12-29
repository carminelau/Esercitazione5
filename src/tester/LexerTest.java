package tester;
import generated.ParserSym;
import generated.Lexer;
import java_cup.runtime.Symbol;
import java.io.IOException;

public class LexerTest {
    public static void main(String[] args) throws IOException {

        Lexer lexicalAnalyzer = new Lexer();
        String filePath = args[0];

        if (lexicalAnalyzer.initialize(filePath)) {
            Symbol token;
            try {
                while ((token = lexicalAnalyzer.next_token()) != null) {
                    if(token.sym == ParserSym.EOF) {
                        break;
                    }
                    System.out.println("<" + ParserSym.terminalNames[token.sym] + (token.value == null ? "" : ", "+token.value) + ">");
                }
            } catch (Exception e) {
                System.out.println("Parsing process ended!!");
            }

        } else {
            System.out.println("File not found!!");
        }
    }
}
