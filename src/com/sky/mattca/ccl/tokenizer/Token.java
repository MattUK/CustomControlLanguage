package com.sky.mattca.ccl.tokenizer;

/**
 * User: Matt
 * Date: 07/10/12
 * Time: 22:33
 */
public class Token {

    /**
     * List of possible token types, these are used by the parser and interpreter to ascertain the context of a line or statement.
     */
    public enum TokenType {
        NUMBER,
        STRING,
        OPEN_BRACKET,
        CLOSE_BRACKET,
        OPEN_SQUARE_BRACKET,
        CLOSE_SQUARE_BRACKET,
        OPERATOR,
        RELATIONAL_OPERATOR,
        COMPARISON,
        SEPARATOR,
        IDENTIFIER,
        WHITESPACE,
        BOOLEAN,
        EQUALS,
        NONE,

        ARRAY_ACCESSOR
    }

    public int line; // Line the token is on.
    public String contents; // Contents of the token.
    public TokenType type; // Type of the token.

    public boolean isReturn;
    public boolean isContinue;
    public boolean isBreak;

    public Token() {
        type = TokenType.NONE;
        contents = "";
    }

    public Token(int line) {
        this.line = line;
        type = TokenType.NONE;
        contents = "";
    }

    public Token(int line, TokenType type) {
        this.line = line;
        this.type = type;
        contents = "";
    }

    public Token(int line, String contents, TokenType type) {
        this.line = line;
        this.contents = contents;
        this.type = type;
    }

    public boolean compare(Token t) {
        if (t.type != type) {
            return false;
        } else {
            if (t.type == TokenType.NUMBER) {
                return Float.parseFloat(t.contents) == Float.parseFloat(contents);
            } else if (t.type == TokenType.BOOLEAN) {
                return Boolean.parseBoolean(t.contents) == Boolean.parseBoolean(contents);
            } else if (t.type == TokenType.STRING) {
                return t.contents.equals(contents);
            } else {
                return false;
            }
        }
    }

    @Override
    public String toString() {
        return "Token{" +
                "line=" + line +
                ", contents='" + contents + '\'' +
                ", type=" + type +
                '}';
    }
}
