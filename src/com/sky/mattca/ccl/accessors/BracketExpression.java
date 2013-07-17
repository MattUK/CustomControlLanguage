package com.sky.mattca.ccl.accessors;

import com.sky.mattca.ccl.interpretation.Interpreter;
import com.sky.mattca.ccl.exceptions.ExpressionParseException;
import com.sky.mattca.ccl.parser.Expression;
import com.sky.mattca.ccl.tokenizer.Token;
import com.sky.mattca.ccl.tokenizer.TokenString;

/**
 * User: 06mcarter
 * Date: 15/11/12
 * Time: 11:42
 */
public class BracketExpression extends Token {

    public Token openBracket;
    public Expression expression;
    public Token closeBracket;

    public static BracketExpression parseBracketExpression(TokenString string) {
        if (string.match(TokenType.OPEN_BRACKET)) {
            BracketExpression bracketExpression = new BracketExpression();
            bracketExpression.openBracket = string.consume();

            try {
                bracketExpression.expression = Expression.parseExpression(string, false);
            } catch (ExpressionParseException e) {
                Interpreter.printCompileError(e.line, Expression.class, e.exceptionType.message);
            }

            if (string.match(TokenType.CLOSE_BRACKET)) {
                bracketExpression.closeBracket = string.consume();
            } else {
                Interpreter.printCompileError(string.line, BracketExpression.class, "Expected ')'.");
            }

            return bracketExpression;
        }
        return null;
    }

    @Override
    public String toString() {
        return "BracketExpression{" +
                "openBracket=" + openBracket +
                ", expression=" + expression +
                ", closeBracket=" + closeBracket +
                '}';
    }
}
