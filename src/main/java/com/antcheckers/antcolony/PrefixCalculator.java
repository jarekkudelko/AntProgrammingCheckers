package com.antcheckers.antcolony;

import java.util.List;
import java.util.Stack;

public class PrefixCalculator {

    private static Stack<Float> stack;
    private static float operand1;
    private static float operand2;
    private static String operator;

    public static float evaluate(List<Node> equation) {
        stack = new Stack<>();
        for (int i=equation.size()-1; i >= 0; i--) {
            Node node = equation.get(i);
            if (node.isOperand())
                stack.push(Float.parseFloat(node.getData()));
            else {
                if (!stack.empty())
                    operand1 = stack.pop();
                if (!stack.empty())
                    operand2 = stack.pop();
                operator = node.getData();
                if (dividingByZero()) {
                    return 0;
                }
                performOperation();
            }
        }
        return stack.pop();
    }

    private static boolean dividingByZero(){
        return operand2 == 0 && operator.equals("/");
    }

    private static void performOperation() {
        if(operator.equals("+"))
            stack.push(operand1 + operand2);
        else if(operator.equals("-"))
            stack.push(operand1 - operand2);
        else if(operator.equals("*"))
            stack.push(operand1 * operand2);
        else if(operator.equals("/"))
            stack.push(operand1 / operand2);
    }
}
