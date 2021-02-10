package calculator;

import java.util.*;
import java.math.BigInteger;
public class Main {

    public static String expression;
    public static Map<String, BigInteger> variables = new HashMap<>();
    public static List<String> postfixExpression = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            expression = scanner.nextLine().trim();
            if (expression.isEmpty())
                continue;
            if (expression.equals("/exit"))
                break;
            if (expression.equals("/help")) {
                help();
                continue;
            }
            if (expression.matches("/.*")) {
                System.out.println("Unknown command");
                continue;
            }
            if ((expression.matches("[a-zA-Z]+"))||(expression.matches("[a-zA-Z]+\\s*=\\s*[-]?([0-9]+|[a-zA-Z]+)"))){
                checkVariables(expression);
                continue;
            }

            normalize(expression);
            try {
                toPostfix(expression);
                System.out.println(calculatePostfix(postfixExpression));
            }
            catch(Exception e){
                System.out.println("Invalid expression: " + e.getMessage());
            }
        }
        System.out.println("Bye!");
    }

    private static BigInteger calculatePostfix(List<String> postfixExpression) {
        Stack<String> stack = new Stack<>();

        for (String elem : postfixExpression){
            if (elem.matches("[-]?[0-9]+"))
                stack.push(elem);
            if (isOperator(elem)) {
                BigInteger b = new BigInteger(stack.pop());
                BigInteger a = new BigInteger(stack.pop());
                switch (elem) {
                    case "-":
                        stack.push(String.valueOf(a.subtract(b)));
                        break;
                    case "+":
                        stack.push(String.valueOf(a.add(b)));
                        break;
                    case "*":
                        stack.push(String.valueOf(a.multiply(b)));
                        break;
                    case "/":
                        stack.push(String.valueOf(a.divide(b)));
                }

            }
        }

        return new BigInteger(stack.peek());
    }

    //Transform infix notation to postfix notation
    private static void toPostfix(String line) throws Exception {
        postfixExpression.clear();

        Stack<String> stack = new Stack<>();
        String[] list = line.split("\\s+");

        for (String elem : list) {
            if (elem.matches("[a-zA-Z]+") && variables.containsKey(elem)) {
                elem = String.valueOf(variables.get(elem));
            } else if (elem.matches("[a-zA-Z]+") && !variables.containsKey(elem)) {
                throw new Exception("There is no such variables as " + elem);
            }

            if (elem.matches("[-]?[0-9]+")) {
                postfixExpression.add(elem);
            } else if (isOperator(elem)) {
                if (!stack.isEmpty() && !stack.peek().equals("(") && !hasHigherPrecedence(elem, stack.peek())) {
                    while (!stack.isEmpty()) {
                        if (stack.peek().equals("(") || hasHigherPrecedence(elem, stack.peek()))
                            break;
                        postfixExpression.add(stack.pop());
                    }
                }
                stack.push(elem);
            } else if (elem.equals("(")) {
                stack.push(elem);
            } else if (elem.equals(")")) {
                while (!stack.peek().equals("(")){
                    postfixExpression.add(stack.pop());
                }
                stack.pop();
            }
        }
        while(!stack.isEmpty()){
            if (stack.peek().matches("[()]"))
                throw new Exception("There is extra brackets iin your expression");
            postfixExpression.add(stack.pop());
        }
    }

    private static void checkVariables(String line) {
        //if we print name of variable, we print its value on the screen
        if (line.matches("[a-zA-Z]+")) {
            if (variables.containsKey(line)) {
                System.out.println(variables.get(line));
            } else {
                System.out.println("Unknown variable");
            }
        }
        //if we assign number to a variable, put this variable to hashmap
        else if (line.matches("[a-zA-Z]+\\s*=\\s*[-]?[0-9]+")) {
            variables.put(line.split("\\s*=\\s*")[0], new BigInteger(line.split("\\s*=\\s*")[1]));
        }
        //if we assign one variable to another
        else if (line.matches("[a-zA-Z]+\\s*=\\s*[a-zA-Z]+")){
            String a = line.split("\\s*=\\s*")[0];
            String b = line.split("\\s*=\\s*")[1];
            if (!variables.containsKey(b)) {
                System.out.println("Invalid assignment");
            } else {
                variables.put(a, variables.get(b));
            }
        }
    }

    //This function simplifies expression
    private static void normalize(String line) {
        expression = line.replaceAll(" ","")
                .replaceAll("--","\\+")
                .replaceAll("[+]{2,}", "\\+")
                .replaceAll("\\+-|-\\+","-")
                .replaceAll("\\+"," \\+ ")
                .replaceAll("-", " - ")
                .replaceAll("\\*", " * ")
                .replaceAll("/", " / ")
                .replaceAll("\\(", " ( ")
                .replaceAll("\\)", " ) ");
    }

    public static void help() {
        System.out.println("This programme can a lot of things");
    }

    private static boolean isOperator(String line) {
        return line.matches("[-+*/]");
    }

    //Return true if firstOp has higher priority than secondOp
    private static boolean hasHigherPrecedence(String firstOp, String secondOp) throws Exception {
        return Operators.getOperatorBySymbol(firstOp).getPriority() > Operators.getOperatorBySymbol(secondOp).getPriority();
    }
}

/*Useful links to understand postfix notation and transformation from infix to postfix notation:
https://uk.wikipedia.org/wiki/%D0%9F%D0%BE%D0%BB%D1%8C%D1%81%D1%8C%D0%BA%D0%B8%D0%B9_%D1%96%D0%BD%D0%B2%D0%B5%D1%80%D1%81%D0%BD%D0%B8%D0%B9_%D0%B7%D0%B0%D0%BF%D0%B8%D1%81*/