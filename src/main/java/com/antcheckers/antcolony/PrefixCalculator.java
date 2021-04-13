package com.antcheckers.antcolony;

import java.util.List;
import java.util.Stack;

public class PrefixCalculator {

    private Stack<Float> stack = new Stack<>();
    private float operand1 = 0;
    private float operand2 = 0;
    private String operator = "";

    public float evaluate(List<Node> equation) {
        Node node;
        for (int i = equation.size() - 1; i >= 0; i--) {
            node = equation.get(i);
            if (node.isOperand())
                stack.push(Float.parseFloat(node.getData()));
            else {
                if (!stack.empty())
                    operand1 = stack.pop();
                if (!stack.empty())
                    operand2 = stack.pop();
                operator = node.getData();
                if (dividingByZero())
                    return 0;
                performOperation();
            }
        }
        return stack.pop();
    }

    private boolean dividingByZero(){
        return operand2 == 0 && operator.equals("/");
    }

    private void performOperation() {
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
