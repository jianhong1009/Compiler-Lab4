import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class PostfixExpression2 {
    public String func(String s) {
        s = s.replaceAll("\\s+", "");

        for (int i = 0; i < s.length(); i++) {
            if ((s.charAt(i) == '-' || s.charAt(i) == '+') &&
                    (i == 0 || s.charAt(i - 1) == '(' || s.charAt(i - 1) == '=' || s.charAt(i - 1) == '~')) {
                if (i == 0) {
                    s = "0" + s;
                } else {
                    s = s.substring(0, i) + "0" + s.substring(i);
                }
            }
        }

        char[] str = s.toCharArray();
        for (int i = 0; i < str.length - 1; i++) {
            if (str[i] == '+' && str[i + 1] == '-') {
                str[i] = ' ';
                str[i + 1] = '-';
            } else if (str[i] == '+' && str[i + 1] == '+') {
                str[i] = ' ';
                str[i + 1] = '+';
            } else if (str[i] == '-' && str[i + 1] == '+') {
                str[i] = ' ';
                str[i + 1] = '-';
            } else if (str[i] == '-' && str[i + 1] == '-') {
                str[i] = ' ';
                str[i + 1] = '+';
            } else if (str[i] == '!' && str[i + 1] == '!') {
                str[i] = ' ';
                str[i + 1] = ' ';
            }
        }

        s = new String(str);
        s = s.replaceAll("\\s+", "");
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '!') {
                if (i == 0) {
                    s = "#" + s;
                    i++;
                } else {
                    s = s.substring(0, i) + "#" + s.substring(i);
                    i++;
                }
            }
        }

        String s1 = s.replaceAll("\\s+", "");

//        System.out.println("Original: " + s1);

        char[] str1 = s1.toCharArray();

        List<String> list = new ArrayList<>();
        List<String> listI32 = new ArrayList<>();
        Stack<String> opStack = new Stack<>();
        String op0 = "!";
        String op1 = "*/%";
        String op2 = "+-";
        String op3 = "<>《》"; // <, >, <=, >=
        String op4 = "=~"; // ==, !=
        String op5 = "&"; // &&
        String op6 = "|"; // ||

        String op0_ = "!";
        String op1_ = "*/%!";
        String op2_ = "+-*/%!";
        String op3_ = "<>《》+-*/%!";
        String op4_ = "=~<>《》+-*/%!";
        String op5_ = "&=~<>《》+-*/%!";
        String op6_ = "|&=~<>《》+-*/%!";

        for (int i = 0; i < str1.length; i++) {
            int num = 0;
            String string = "";
            boolean flag = false;

            if (str1[i] == '#') {
                list.add("#");
                listI32.add("#");
                continue;
            }

            if (Character.isLetter(str1[i]) || str1[i] == '_') {
                string += str1[i];
                i++;
                flag = true;
                for (; i < str1.length && (Character.isDigit(str1[i]) || Character.isLetter(str1[i]) || str1[i] == '_'); i++) {
                    string += str1[i];
                }
            }
            if (flag) {
                Variable.checkExist(string);
                System.out.println("    %" + (Visitor.num + 1) + " = load i32, i32* " + Variable.getStore(string));
                Visitor.num++;
                Variable.setLoad(string, "%" + Visitor.num);
                list.add(Variable.getLoad(string));
                listI32.add("i32");
                i--;
                flag = false;
                continue;
            }

            for (; i < str1.length && Character.isDigit(str1[i]); i++) {
                num = num * 10 + Character.getNumericValue(str1[i]);
                flag = true;
            }
            if (flag) {
                list.add(String.valueOf(num));
                listI32.add("i32");
                i--;
                flag = false;
                continue;
            }

            if (opStack.size() == 0 || str1[i] == '(') {
                opStack.push(str1[i] + "");
                continue;
            }

            if (str1[i] == ')') {
                while (opStack.size() != 0 && !opStack.peek().equals("(")) {
                    listI32.add("#");
                    list.add(opStack.pop());
                }
                opStack.pop();
                continue;
            }

            while (opStack.size() != 0 && op0.contains(str1[i] + "") && op0_.contains(opStack.peek())) {
                listI32.add("#");
                list.add(opStack.pop());
            }
            while (opStack.size() != 0 && op1.contains(str1[i] + "") && op1_.contains(opStack.peek())) {
                listI32.add("#");
                list.add(opStack.pop());
            }
            while (opStack.size() != 0 && op2.contains(str1[i] + "") && op2_.contains(opStack.peek())) {
                listI32.add("#");
                list.add(opStack.pop());
            }
            while (opStack.size() != 0 && op3.contains(str1[i] + "") && op3_.contains(opStack.peek())) {
                listI32.add("#");
                list.add(opStack.pop());
            }
            while (opStack.size() != 0 && op4.contains(str1[i] + "") && op4_.contains(opStack.peek())) {
                listI32.add("#");
                list.add(opStack.pop());
            }
            while (opStack.size() != 0 && op5.contains(str1[i] + "") && op5_.contains(opStack.peek())) {
                listI32.add("#");
                list.add(opStack.pop());
            }
            while (opStack.size() != 0 && op6.contains(str1[i] + "") && op6_.contains(opStack.peek())) {
                listI32.add("#");
                list.add(opStack.pop());
            }
            opStack.push(str1[i] + "");
        }

        while (opStack.size() != 0) {
            listI32.add("#");
            list.add(opStack.pop());
        }

//        System.out.println(list);

        int sum = 0;
        boolean flag = false;
        for (int i = 2; list.size() != 1; i++) {
            switch (list.get(i)) {
                case "+" -> {
                    System.out.println("    %" + (Visitor.num + 1) + " = " + "add i32 " + list.get(i - 2) + ", " + list.get(i - 1));
                    flag = true;
                    listI32.set(i, "i32");
                }
                case "-" -> {
                    System.out.println("    %" + (Visitor.num + 1) + " = " + "sub i32 " + list.get(i - 2) + ", " + list.get(i - 1));
                    flag = true;
                    listI32.set(i, "i32");
                }
                case "*" -> {
                    System.out.println("    %" + (Visitor.num + 1) + " = " + "mul i32 " + list.get(i - 2) + ", " + list.get(i - 1));
                    flag = true;
                    listI32.set(i, "i32");
                }
                case "/" -> {
                    System.out.println("    %" + (Visitor.num + 1) + " = " + "sdiv i32 " + list.get(i - 2) + ", " + list.get(i - 1));
                    flag = true;
                    listI32.set(i, "i32");

                }
                case "%" -> {
                    System.out.println("    %" + (Visitor.num + 1) + " = " + "sdiv i32 " + list.get(i - 2) + ", " + list.get(i - 1));
                    Visitor.num++;
                    System.out.println("    %" + (Visitor.num + 1) + " = " + "mul i32 " + "%" + Visitor.num + ", " + list.get(i - 1));
                    Visitor.num++;
                    System.out.println("    %" + (Visitor.num + 1) + " = " + "sub i32 " + list.get(i - 2) + ", " + "%" + Visitor.num);
                    flag = true;
                    listI32.set(i, "i32");
                }
                case "=" -> {
                    System.out.println("    %" + (Visitor.num + 1) + " = " + "icmp eq i32 " + list.get(i - 2) + ", " + list.get(i - 1));
                    flag = true;
                    listI32.set(i, "i1");
                }
                case "~" -> {
                    System.out.println("    %" + (Visitor.num + 1) + " = " + "icmp ne i32 " + list.get(i - 2) + ", " + list.get(i - 1));
                    flag = true;
                    listI32.set(i, "i1");
                }
                case "<" -> {
                    System.out.println("    %" + (Visitor.num + 1) + " = " + "icmp slt i32 " + list.get(i - 2) + ", " + list.get(i - 1));
                    flag = true;
                    listI32.set(i, "i1");
                }
                case ">" -> {
                    System.out.println("    %" + (Visitor.num + 1) + " = " + "icmp sgt i32 " + list.get(i - 2) + ", " + list.get(i - 1));
                    flag = true;
                    listI32.set(i, "i1");
                }
                case "《" -> {
                    System.out.println("    %" + (Visitor.num + 1) + " = " + "icmp sle i32 " + list.get(i - 2) + ", " + list.get(i - 1));
                    flag = true;
                    listI32.set(i, "i1");
                }
                case "》" -> {
                    System.out.println("    %" + (Visitor.num + 1) + " = " + "icmp sge i32 " + list.get(i - 2) + ", " + list.get(i - 1));
                    flag = true;
                    listI32.set(i, "i1");
                }
                case "&" -> {
                    if (!listI32.get(i - 2).equals("i1")) {
                        System.out.println("    %" + (Visitor.num + 1) + " = " + "icmp ne i32 " + list.get(i - 2) + ", 0");
                        Visitor.num++;
                        listI32.set(i - 2, "i1");
                        list.set(i - 2, "%" + Visitor.num);
                    }
                    if (!listI32.get(i - 1).equals("i1")) {
                        System.out.println("    %" + (Visitor.num + 1) + " = " + "icmp ne i32 " + list.get(i - 1) + ", 0");
                        Visitor.num++;
                        listI32.set(i - 1, "i1");
                        list.set(i - 1, "%" + Visitor.num);
                    }
                    System.out.println("    %" + (Visitor.num + 1) + " = " + "and i1 " + list.get(i - 2) + ", " + list.get(i - 1));
                    flag = true;
                    listI32.set(i, "i1");
                }
                case "|" -> {
                    if (!listI32.get(i - 2).equals("i1")) {
                        System.out.println("    %" + (Visitor.num + 1) + " = " + "icmp ne i32 " + list.get(i - 2) + ", 0");
                        Visitor.num++;
                        listI32.set(i - 2, "i1");
                        list.set(i - 2, "%" + Visitor.num);
                    }
                    if (!listI32.get(i - 1).equals("i1")) {
                        System.out.println("    %" + (Visitor.num + 1) + " = " + "icmp ne i32 " + list.get(i - 1) + ", 0");
                        Visitor.num++;
                        listI32.set(i - 1, "i1");
                        list.set(i - 1, "%" + Visitor.num);
                    }
                    System.out.println("    %" + (Visitor.num + 1) + " = " + "or i1 " + list.get(i - 2) + ", " + list.get(i - 1));
                    flag = true;
                    listI32.set(i, "i1");
                }
                case "!" -> {
                    System.out.println("    %" + (Visitor.num + 1) + " = " + "icmp ne i32 " + list.get(i - 1) + ", 0");
                    Visitor.num++;
                    System.out.println("    %" + (Visitor.num + 1) + " = " + "xor i1 " + "%" + Visitor.num + ", true");
                    Visitor.num++;
                    System.out.println("    %" + (Visitor.num + 1) + " = " + "zext i1 " + "%" + Visitor.num + " to i32");
                    flag = true;
                }
            }
            if (flag) {
                Visitor.num++;
                list.set(i, "%" + Visitor.num);
                list.remove(i - 1);
                listI32.remove(i - 1);
                list.remove(i - 2);
                listI32.remove(i - 2);
                i = 1;
                flag = false;
            }
        }

//        System.exit(0);
        if (listI32.get(0).equals("i32")) {
            System.out.println("    %" + (Visitor.num + 1) + " = " + "icmp ne i32 " + list.get(0) + ", 0");
            Visitor.num++;
            listI32.set(0, "i1");
            list.set(0, "%" + Visitor.num);
        }
        return list.get(0);
    }
}
