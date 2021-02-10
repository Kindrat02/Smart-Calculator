package calculator;

public enum Operators {
    PLUS("+", 0), MINUS("-", 0), DIVISION("/", 1), MULTIPLICATION("*", 1);
    private final String operand;
    private final int priority;
    Operators(String operand, int priority) {
        this.operand = operand;
        this.priority = priority;
    }

    public static Operators getOperatorBySymbol(String op) throws Exception {
        switch (op) {
            case "+" :
                return PLUS;
            case "-":
                return MINUS;
            case "*":
                return MULTIPLICATION;
            case "/":
                return DIVISION;
            default:
                throw new Exception("There is no operators such as " + op);
        }
    }

    public int getPriority() {
        return priority;
    }
}
