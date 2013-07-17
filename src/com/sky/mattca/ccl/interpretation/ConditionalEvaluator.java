package com.sky.mattca.ccl.interpretation;

import com.sky.mattca.ccl.accessors.BracketConditionSequence;
import com.sky.mattca.ccl.parser.Condition;
import com.sky.mattca.ccl.parser.ConditionSequence;
import com.sky.mattca.ccl.tokenizer.Token;
import com.sky.mattca.ccl.tokenizer.TokenString;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * User: 06mcarter
 * Date: 05/02/13
 * Time: 13:17
 */
public class ConditionalEvaluator {

    public static boolean evaluateCondition(Condition condition) {
        Token result01 = ExpressionEvaluator.evaluateExpression(condition.firstExpression);
        Token result02 = ExpressionEvaluator.evaluateExpression(condition.secondExpression);
        TokenString comparisons = condition.comparisonOperators;

        // =, >, <, >=, <=, !=
        if (comparisons.size() == 1 && comparisons.peek().type == Token.TokenType.EQUALS) {
            // Equals comparison
            return result01.compare(result02);
        } else if (comparisons.size() == 1 && comparisons.peek().contents.equals(">")) {
            // Assume numbers
            if (result01.type != Token.TokenType.NUMBER || result02.type != Token.TokenType.NUMBER) {
                Interpreter.printRuntimeError(ConditionalEvaluator.class, "Only numbers can be compared with the '>' operator.");
            } else {
                float value1 = Float.parseFloat(result01.contents);
                float value2 = Float.parseFloat(result02.contents);
                return value1 > value2;
            }
        } else if (comparisons.size() == 1 && comparisons.peek().contents.equals("<")) {
            // Assume numbers
            if (result01.type != Token.TokenType.NUMBER || result02.type != Token.TokenType.NUMBER) {
                Interpreter.printRuntimeError(ConditionalEvaluator.class, "Only numbers can be compared with the '<' operator.");
            } else {
                float value1 = Float.parseFloat(result01.contents);
                float value2 = Float.parseFloat(result02.contents);
                return value1 < value2;
            }
        } else if (comparisons.size() == 2 && comparisons.contentMatch(">=")) {
            // Assume numbers
            if (result01.type != Token.TokenType.NUMBER || result02.type != Token.TokenType.NUMBER) {
                Interpreter.printRuntimeError(ConditionalEvaluator.class, "Only numbers can be compared with the '>=' operator.");
            } else {
                float value1 = Float.parseFloat(result01.contents);
                float value2 = Float.parseFloat(result02.contents);
                return value1 >= value2;
            }
        } else if (comparisons.size() == 2 && comparisons.contentMatch("<=")) {
            // Assume numbers
            if (result01.type != Token.TokenType.NUMBER || result02.type != Token.TokenType.NUMBER) {
                Interpreter.printRuntimeError(ConditionalEvaluator.class, "Only numbers can be compared with the '<=' operator.");
            } else {
                float value1 = Float.parseFloat(result01.contents);
                float value2 = Float.parseFloat(result02.contents);
                return value1 <= value2;
            }
        } else if (comparisons.size() == 1 && comparisons.contentMatch("!=")) {
            return !result01.compare(result02);
        } else {
            Interpreter.printRuntimeError(ConditionalEvaluator.class, "Expected '=', '>', '<', '>=', '<=', or '!='.");
        }

        return false;
    }

    /**
     * Converts a condition sequence into a list of tokens.
     * @param sequence The sequence to convert.
     * @return A list of tokens that represent the condition.
     */
    public static List<Token> sequenceToList(ConditionSequence sequence) {
        List<Token> list = new ArrayList<>();

        if (sequence.firstCondition instanceof BracketConditionSequence) {
            list.add(((BracketConditionSequence) sequence.firstCondition).openBracketToken);
            list.addAll(sequenceToList(((BracketConditionSequence) sequence.firstCondition).conditionSequence));
            list.add(((BracketConditionSequence) sequence.firstCondition).closeBracketToken);
        } else {
            list.add(new Token(-1, Boolean.toString(evaluateCondition(sequence.firstCondition)), Token.TokenType.BOOLEAN));
        }

        if (sequence.relationalOperator != null) {
            list.add(sequence.relationalOperator);
            list.addAll(sequenceToList(sequence.followingSequence));
        }

        return list;
    }

    /**
     * Evaluates an entire condition sequence into either 'True' or 'False'.
     * @param sequence The sequence to evaluate.
     * @return True or False
     */
    public static boolean evaluateConditionSequence(ConditionSequence sequence) {
        try {
            List<Token> conditions = sequenceToList(sequence);
            ShuntingYard shuntingYard = new ShuntingYard();
            conditions = shuntingYard.convertExpression(conditions);

            Stack<Token> valueStack = new Stack<>();
            boolean finished = false;
            while(!finished) {
                if (conditions.isEmpty()) {
                    finished = true;
                } else if (conditions.get(0).type != Token.TokenType.RELATIONAL_OPERATOR && conditions.get(0).type == Token.TokenType.BOOLEAN) {
                    valueStack.push(conditions.remove(0));
                } else if (conditions.get(0).type == Token.TokenType.RELATIONAL_OPERATOR) {
                    if (conditions.get(0).contents.equals("&")) {
                        boolean value1 = Boolean.parseBoolean(valueStack.pop().contents);
                        boolean value2 = Boolean.parseBoolean(valueStack.pop().contents);
                        valueStack.push(new Token(-1, String.valueOf(value1 && value2), Token.TokenType.BOOLEAN));
                    } else {
                        boolean value1 = Boolean.parseBoolean(valueStack.pop().contents);
                        boolean value2 = Boolean.parseBoolean(valueStack.pop().contents);
                        valueStack.push(new Token(-1, String.valueOf(value1 || value2), Token.TokenType.BOOLEAN));
                    }
                    conditions.remove(0);
                } else {
                    Interpreter.printRuntimeError(ConditionalEvaluator.class, "Expected 'true' or 'false', found invalid condition.");
                }
            }

            if (valueStack.size() != 1) {
                Interpreter.printRuntimeError(ConditionalEvaluator.class, "More than one value remaining on the value stack.");
            }

            return Boolean.parseBoolean(valueStack.pop().contents);

        } catch (Exception e) {
            Interpreter.printRuntimeError(ConditionalEvaluator.class, "Encountered an error whilst evaluating condition.");
        }

        return false;
    }

}
