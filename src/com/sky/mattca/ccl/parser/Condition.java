package com.sky.mattca.ccl.parser;

import com.sky.mattca.ccl.interpretation.Interpreter;
import com.sky.mattca.ccl.exceptions.ExpressionParseException;
import com.sky.mattca.ccl.tokenizer.Token;
import com.sky.mattca.ccl.tokenizer.TokenString;

/**
 * Represents a condition, two expressions compared by an comparison operator
 */
public class Condition extends Statement {

    public Expression firstExpression;
    public TokenString comparisonOperators;
    public Expression secondExpression;

    public Condition() {
        comparisonOperators = new TokenString();
    }

    /**
     * Parses a condition from a provided Token String
     *
     * @param string The string used for parsing
     * @return The parsed Condition object
     */
    public static Condition parseCondition(TokenString string) {
        Condition condition = new Condition();

        // First expression
        try {
            condition.firstExpression = Expression.parseExpression(string, true);

            // Parse comparison operator(s)
            while (string.match(Token.TokenType.COMPARISON) || string.match(Token.TokenType.EQUALS)) {
                condition.comparisonOperators.append(string.consume());
            }

            // Check operators are in a valid format, either '=', '!=', '>', '<', '>=', or '<='
            if (!condition.comparisonOperators.match(Token.TokenType.EQUALS)) {
                if (!condition.comparisonOperators.match(Token.TokenType.COMPARISON)) {
                    if (!condition.comparisonOperators.match(Token.TokenType.EQUALS, Token.TokenType.COMPARISON)) {
                        if (!condition.comparisonOperators.match(Token.TokenType.COMPARISON, Token.TokenType.EQUALS)) {
                            Interpreter.printCompileError(string.line, Condition.class, "Expected '=', '<', '>', or '!='.");
                        }
                    } else {
                        if (condition.comparisonOperators.contentMatch("=", "!")) {
                            Interpreter.printCompileError(string.line, Condition.class, "Expected '!=', found '=!'.");
                        }
                    }
                } else {
                    if (condition.comparisonOperators.contentMatch("!")) {
                        Interpreter.printCompileError(string.line, Condition.class, "Expected '!=', found '!'.");
                    }
                }
            }

            // Parse second expression
            condition.secondExpression = Expression.parseExpression(string, true);
        } catch (ExpressionParseException e) {
            Interpreter.printCompileError(e.line, Expression.class, e.exceptionType.message);
        }

        return condition;
    }

    @Override
    public String toString() {
        return "Condition{" +
                "firstExpression=" + firstExpression +
                ", comparisonOperators=" + comparisonOperators +
                ", secondExpression=" + secondExpression +
                '}';
    }
}
