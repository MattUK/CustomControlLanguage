package com.sky.mattca.ccl.parser;

import com.sky.mattca.ccl.interpretation.Interpreter;
import com.sky.mattca.ccl.accessors.ArrayAccessor;
import com.sky.mattca.ccl.accessors.BracketExpression;
import com.sky.mattca.ccl.exceptions.ExpressionParseException;
import com.sky.mattca.ccl.tokenizer.Token;
import com.sky.mattca.ccl.tokenizer.TokenString;

/**
 * Represents an Expression used as parameters, assignments, conditions, etc
 */
public class Expression extends Statement {
    // The value modifier (e.g. for definition of negative numbers)
    public Token valueModifier;
    // The value on the left-hand side of the expression
    public Token expressionValue;
    // The operation to be used on the left and right side of this expression (can be null)
    public Token operator;
    // The expression that follows this one 
    public Expression followingExpression;

    private Expression() {
        expressionValue = null;
        operator = null;
        followingExpression = null;
    }

    public static Expression parseExpression(TokenString string, boolean parsingConditional) throws ExpressionParseException {
        Expression newExpression = new Expression();

        /*
        <Expression>::=[-|+]<Number>|<String>|[!]<Boolean>|[-|+]<Array-Get>|[-|+]<Identifier>|
                       [-|+]<Number><Operator><Expression>|<String><Operator><Expression>|[-|+]<Identifier><Operator><Expression>|[-|+]<Array-Get><Operator><Expression>|
                       (<Expression>)|(<Expression>)<Operator><Expression>

                       3+(9)*2
                       3+<Expression>
                       3+(9)*<Expression>
                       3+(9)*2
         */

        string.removeWhitespace();

        // Parse optional modifiers
        if (string.match(Token.TokenType.OPERATOR, Token.TokenType.NUMBER)) {
            if (string.contentMatch("-") || string.contentMatch("+")) {
                newExpression.valueModifier = string.consume();
            } else {
                throw new ExpressionParseException(string.line, ExpressionParseException.ExpressionExceptionType.INVALID_OPERATOR, string.peek().contents);
            }
        }

        if (string.match(Token.TokenType.COMPARISON, Token.TokenType.BOOLEAN)) {
            if (string.contentMatch("!")) {
                newExpression.valueModifier = string.consume();
            } else {
                throw new ExpressionParseException(string.line, ExpressionParseException.ExpressionExceptionType.INVALID_OPERATOR, string.peek().contents);
            }
        }

        if (string.match(Token.TokenType.OPERATOR, Token.TokenType.IDENTIFIER)) {
            if (string.contentMatch("-") || string.contentMatch("+")) {
                newExpression.valueModifier = string.consume();
            } else {
                throw new ExpressionParseException(string.line, ExpressionParseException.ExpressionExceptionType.INVALID_OPERATOR, string.peek().contents);
            }
        }

        // Parse expression contents
        if (string.match(Token.TokenType.NUMBER)) {
            // Number
            newExpression.expressionValue = string.consume();
        } else if (string.match(Token.TokenType.STRING)) {
            // String
            newExpression.expressionValue = string.consume();
        } else if (string.match(Token.TokenType.BOOLEAN)) {
            // Boolean
            newExpression.expressionValue = string.consume();
        } else if (string.match(Token.TokenType.IDENTIFIER, Token.TokenType.OPEN_SQUARE_BRACKET)) {
            // Array-Get
            newExpression.expressionValue = ArrayAccessor.parseArrayAccessor(string);
        } else if (string.match(Token.TokenType.IDENTIFIER)) {
            // Identifier
            newExpression.expressionValue = string.consume();
        } else if (string.match(Token.TokenType.OPEN_BRACKET)) {
            // Open bracket
            newExpression.expressionValue = BracketExpression.parseBracketExpression(string);
        } else if (string.empty()) {
            Interpreter.printCompileError(string.line, Expression.class, "End of line found, expected expression.");
        } else {
            if (parsingConditional && (string.match(Token.TokenType.COMPARISON) || string.match(Token.TokenType.EQUALS))) {
                return newExpression;
            } else if (string.match(Token.TokenType.SEPARATOR)) {
                System.out.println("Found end of expression. " + newExpression.toString());
                return newExpression;
            }
            throw new ExpressionParseException(string.line, ExpressionParseException.ExpressionExceptionType.INVALID_TOKEN, string.peek().contents);
//			Interpreter.printCompileError(string.line, Expression.class, "Invalid token found while parsing expression '" + string.peek().contents + "'.");
        }

        // Parse following operator
        if (string.match(Token.TokenType.OPERATOR)) {
            newExpression.operator = string.consume();

            if (!string.empty()) {
                newExpression.followingExpression = Expression.parseExpression(string, parsingConditional); // Recursive parsing ftw.
            } else {
                throw new ExpressionParseException(string.line, ExpressionParseException.ExpressionExceptionType.EXPECTED_VALUE, "");
            }
        } else {
            System.out.println("Finished parsing expression.");
        }

        return newExpression;
    }

    @Override
    public String toString() {
        return "Expression{" +
                "valueModifier=" + valueModifier +
                ", expressionValue=" + expressionValue +
                ", operator=" + operator +
                ", followingExpression=" + followingExpression +
                '}';
    }
}
