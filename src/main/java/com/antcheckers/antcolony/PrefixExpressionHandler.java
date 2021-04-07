package com.antcheckers.antcolony;

import java.util.List;
import java.util.Stack;

public class PrefixExpressionHandler {

        float evaluatePrefix(List<Node> equation){
            Stack<Float> stack = new Stack<>();
            Node node;
            for (int i=equation.size() - 1; i >= 0; i--) {
                node = equation.get(i);
                if(node.getNodePower() == -1){
                    stack.push(Float.parseFloat(node.getData()));
                } else {
                    float operand1=0, operand2=0;
                    if(!stack.empty()){
                        operand1 = stack.pop();
                    } else {
                        System.out.println("Stack empty - operand1!");
                    }
                    if(!stack.empty()){
                        operand2 = stack.pop();
                    } else {
                        System.out.println("Stack empty - operand2!");
                    }

                    switch (node.getData()){
                        case "+":
                            stack.push(operand1 + operand2);
                            break;
                        case "-":
                            stack.push(operand1 - operand2);
                            break;
                        case "*":
                            stack.push(operand1 * operand2);
                            break;
                        case "/":
                            if(operand2 == 0){
                                return 0;
                            }
                            stack.push(operand1 / operand2);
                            break;
                    }
                }
            }
            return stack.pop();
        }
}
