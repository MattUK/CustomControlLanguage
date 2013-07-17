package com.sky.mattca.ccl.interpretation;

import com.sky.mattca.ccl.accessors.ArrayAccessor;
import com.sky.mattca.ccl.accessors.BracketExpression;
import com.sky.mattca.ccl.parser.Expression;
import com.sky.mattca.ccl.tokenizer.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * User: 06mcarter
 * Date: 05/02/13
 * Time: 13:37
 */
public class ExpressionEvaluator {

    public static StatementInterpreter currentInterpreter;

    private static List<Token> expressionToTokenList(Expression e) {
        List<Token> expressionTokens = new ArrayList<>();

        if (e.valueModifier != null) expressionTokens.add(e.valueModifier);
        if (e.expressionValue instanceof BracketExpression) {
            expressionTokens.add(((BracketExpression) e.expressionValue).openBracket);
            expressionTokens.addAll(expressionToTokenList(((BracketExpression) e.expressionValue).expression));
            expressionTokens.add(((BracketExpression) e.expressionValue).closeBracket);
        } else
        {
            expressionTokens.add(e.expressionValue);
        }

        if (e.operator != null) {
            expressionTokens.add(e.operator);
            expressionTokens.addAll(expressionToTokenList(e.followingExpression));
        }

        return expressionTokens;
    }

    public static float tokenToNumber(Token t) {
        if (t.type == Token.TokenType.NUMBER) {
            return Float.parseFloat(t.contents);
        } else {
            Interpreter.printRuntimeError(ExpressionEvaluator.class, "Cannot convert '" + t.contents + "' to number.");
        }
        return 0.0f;
    }

    /**
     * Evaluates an expression into one value.
     * @param e The expression to evaluate.
     * @return The token representing the value calculated from the expression.
     */
    public static Token evaluateExpression(Expression e) {
        List<Token> expression = expressionToTokenList(e);
        expression = new ShuntingYard().convertExpression(expression);

        try {
            Stack<Token> valueStack = new Stack<>();
            boolean finished = false;
            while (!finished) {
                if (expression.isEmpty()) {
                    finished = true;
                } else if (expression.get(0).type == Token.TokenType.IDENTIFIER) {
                    // Variable identifier
                    if (currentInterpreter.variableExists(expression.get(0).contents)) {
                        // Fetch variable data
                        Interpreter.VariableData data = currentInterpreter.getVariableData(expression.get(0).contents);
                        // Make sure the variable isn't an array (shouldn't be anyway)
                        if (data.isArray()) {
                            Interpreter.printRuntimeError(ExpressionEvaluator.class, "Expected variable or array value, found array: '" + data.variableName + "'.");
                        }
                        expression.set(0, data.variableAsToken());
                    } else {
                        Interpreter.printRuntimeError(ExpressionEvaluator.class, "Variable does not exist: '" + expression.get(0).contents + "'.");
                    }
                } else if (expression.get(0).type == Token.TokenType.ARRAY_ACCESSOR) {
                    // Array identifier
                    ArrayAccessor accessor = (ArrayAccessor)(expression.get(0));
                    if (currentInterpreter.variableExists(accessor.contents)) {
                        Interpreter.VariableData data = currentInterpreter.getVariableData(expression.get(0).contents);
                        if (data.isArray()) {
                            Token index = ExpressionEvaluator.evaluateExpression(accessor.accessorExpression);
                            if (index.type != Token.TokenType.NUMBER) {
                                Interpreter.printRuntimeError(ExpressionEvaluator.class, "Expected number for array index, found '" + index.contents + "'.");
                            } else {
                                Token valueAtIndex = data.arrayAsToken((int)Float.parseFloat(index.contents));
                                if (valueAtIndex == null) {
                                    Interpreter.printRuntimeError(ExpressionEvaluator.class, "Array index is out of bounds: " + index.contents + "'.");
                                } else {
                                    expression.set(0, valueAtIndex);
                                }
                            }
                        } else {
                            Interpreter.printRuntimeError(ExpressionEvaluator.class, "Expected array, found variable: '" + data.variableName + "'.");
                        }
                    }
                } else if (expression.get(0).type != Token.TokenType.OPERATOR) {
                    valueStack.push(expression.remove(0));
                } else {
                    switch (expression.remove(0).contents) {
                        case "_":
                            Token tempNegative = valueStack.pop();
                            tempNegative.contents = "-" + tempNegative.contents;
                            valueStack.push(tempNegative);
                            break;
                        case "^":
                            Token power = valueStack.pop();
                            Token value = valueStack.pop();
                            float powResult = (float) Math.pow(tokenToNumber(value), tokenToNumber(power));
                            value.contents = Float.toString(powResult);
                            valueStack.push(value);
                            break;
                        case "*":
                            Token mulRightOperator = valueStack.pop();
                            Token mulLeftOperator = valueStack.pop();
                            float mulResult = tokenToNumber(mulLeftOperator) * tokenToNumber(mulRightOperator);
                            valueStack.push(new Token(mulLeftOperator.line, Float.toString(mulResult), Token.TokenType.NUMBER));
                            break;
                        case "/":
                            Token divRightOperator = valueStack.pop();
                            Token divLeftOperator = valueStack.pop();
                            float divResult = tokenToNumber(divLeftOperator) / tokenToNumber(divRightOperator);
                            valueStack.push(new Token(divLeftOperator.line, Float.toString(divResult), Token.TokenType.NUMBER));
                            break;
                        case "+":
                            Token addRightOperator = valueStack.pop();
                            Token addLeftOperator = valueStack.pop();
                            if (addRightOperator.type != Token.TokenType.NUMBER || addLeftOperator.type != Token.TokenType.NUMBER) {
                                valueStack.push(new Token(addLeftOperator.line, addLeftOperator.contents + addRightOperator.contents, Token.TokenType.STRING));
                            } else {
                                float addResult = tokenToNumber(addLeftOperator) + tokenToNumber(addRightOperator);
                                valueStack.push(new Token(addLeftOperator.line, Float.toString(addResult), Token.TokenType.NUMBER));
                            }
                            break;
                        case "-":
                            Token subRightOperator = valueStack.pop();
                            Token subLeftOperator = valueStack.pop();
                            float subResult = tokenToNumber(subLeftOperator) - tokenToNumber(subRightOperator);
                            valueStack.push(new Token(subLeftOperator.line, Float.toString(subResult), Token.TokenType.NUMBER));
                            break;
                        default:
                            Interpreter.printRuntimeError(ExpressionEvaluator.class, "Expected operator, number, boolean, or string.");
                    }
                }
            }

            if (valueStack.size() > 1) {
                Interpreter.printRuntimeError(ExpressionEvaluator.class, "Expression value stack contains more than 1 item.");
            }

            return valueStack.pop();
        } catch (Exception e1) {
            Interpreter.printRuntimeError(ExpressionEvaluator.class, "Encountered exception whilst evaluating expression. " + expression.get(0).line);
        }
        return null;
    }

}
