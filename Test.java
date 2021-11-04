import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;
import java.io.IOException;

public class Test {
    public static String outputPath = "";

    public static void main(String[] args) throws IOException {
        FileInputStream fis = new FileInputStream(args[0]);
        outputPath = args[1];

        byte[] arr = new byte[100];
        String s = "";
        int i = 0;

        while ((i = fis.read(arr, 0, 100)) != -1) {
            s += new String(arr, 0, i);
        }

        String str = "";
        boolean flag = false;
        boolean flag2 = false;
        while (true) {
            if (s.charAt(0) == '/' && s.charAt(1) == '/' && !flag2) {
                flag = true;
                if (s.length() == 2) {
                    break;
                }
                s = s.substring(2);
                continue;
            }
            if (s.charAt(0) == '/' && s.charAt(1) == '*' && !flag) {
                flag2 = true;
                if (s.length() == 2) {
                    break;
                }
                s = s.substring(2);
                continue;
            }
            if (s.charAt(0) == '\n' && flag) {
                flag = false;
                str += "\n";
                if (s.length() == 1) {
                    break;
                }
                s = s.substring(1);
                continue;
            }
            if (s.charAt(0) == '*' && s.charAt(1) == '/' && flag2) {
                flag2 = false;
                if (s.length() == 2) {
                    break;
                }
                s = s.substring(2);
                continue;
            }
            if (!flag && !flag2) {
                str += s.charAt(0);
                if (s.length() == 1) {
                    break;
                }
                s = s.substring(1);
                continue;
            }
            if (s.length() == 1) {
                break;
            }
            s = s.substring(1);
        }
        if (flag2) {
            System.out.println("error");
            System.exit(1);
        }


        CharStream inputStream = CharStreams.fromString(str); // 获取输入流
        lab4Lexer lexer = new lab4Lexer(inputStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(ThrowingErrorListener.INSTANCE);

        CommonTokenStream tokenStream = new CommonTokenStream(lexer); // 词法分析获取 token 流
        lab4Parser parser = new lab4Parser(tokenStream);
        parser.removeErrorListeners();
        parser.addErrorListener(ThrowingErrorListener.INSTANCE);

        ParseTree tree = parser.compUnit(); // 获取语法树的根节点
        // System.out.println(tree.toStringTree(parser)); // 打印字符串形式的语法树

        Visitor visitor = new Visitor();
        visitor.visit(tree);

        fis.close();
    }
}