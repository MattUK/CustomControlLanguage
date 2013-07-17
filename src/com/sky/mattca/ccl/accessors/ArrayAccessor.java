package com.sky.mattca.ccl.accessors;

import com.sky.mattca.ccl.interpretation.Interpreter;
import com.sky.mattca.ccl.exceptions.ExpressionParseException;
import com.sky.mattca.ccl.parser.Expression;
import com.sky.mattca.ccl.tokenizer.Token;
import com.sky.mattca.ccl.tokenizer.TokenString;

/**
 * User: 06mcarter
 * Date: 07/11/12
 * Time: 12:09
 */
public class ArrayAccessor extends Token {

    public Token openSquareBracket;
    public Expression accessorExpression;
    public Token closeSquareBracket;

    public static ArrayAccessor createFrom(Token identifier) {
        ArrayAccessor accessor = new ArrayAccessor();

        accessor.contents = identifier.contents;
        accessor.line = identifier.line;
        accessor.type = TokenType.ARRAY_ACCESSOR;

        return accessor;
    }

    public static ArrayAccessor parseArrayAccessor(TokenString string) {
        if (string.match(TokenType.IDENTIFIER, TokenType.OPEN_SQUARE_BRACKET)) {
            ArrayAccessor parsedAccessor = ArrayAccessor.createFrom(string.consume());
            parsedAccessor.openSquareBracket = string.consume();
            try {
                parsedAccessor.accessorExpression = Expression.parseExpression(string, false);
            } catch (ExpressionParseException e) {
                Interpreter.printCompileError(e.line, Expression.class, e.exceptionType.message);
            }

            if (string.match(TokenType.CLOSE_SQUARE_BRACKET)) {
                parsedAccessor.closeSquareBracket = string.consume();
            } else {
                Interpreter.printCompileError(string.line, ArrayAccessor.class, "Expected ']'");
            }

            return parsedAccessor;
        }

        return null;
    }

    @Override
    public String toString() {
        return "ArrayAccessor{" +
                "identifier=" + contents +
                ", openSquareBracket=" + openSquareBracket +
                ", accessorExpression=" + accessorExpression +
                ", closeSquareBracket=" + closeSquareBracket +
                '}';
    }
}
