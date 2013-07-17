package com.sky.mattca.ccl.tokenizer;

import com.sky.mattca.ccl.interpretation.Interpreter;
import com.sky.mattca.ccl.tokenizer.Token.TokenType;

import java.util.Arrays;

/**
 * User: Matt
 * Date: 07/10/12
 * Time: 23:23
 */
public class Tokenizer {

    private int currentLine;
    private char[] chars;

    public Tokenizer() {
        currentLine = 0;
        chars = new char[0];
    }

    /**
     * 'Consumes' the next character in the chars array, removing it.
     *
     * @return The 'consumed' character.
     */
    private char consume() {
        char character = chars[0];
        chars = Arrays.copyOfRange(chars, 1, chars.length);
        return character;
    }

    /**
     * BNF: <Number>::=[-]<Digit>{<Digit>}[.<Digit>{<Digit>}]
     *
     * @return Returns the number as a token.
     */
    private Token generateNumberToken() {
        Token token = new Token(currentLine, TokenType.NUMBER);
        String contents = "";

        // Initial digit
        contents += consume();

        // Tokenize any following digits
        boolean finished = false;
        while (!finished) {
            if (chars.length > 0 && Character.isDigit(chars[0])) {
                contents += consume();
            } else {
                finished = true;
            }
        }

        if (chars.length > 0) {
            if (chars[0] == '.') {

                contents += consume();
                if (chars.length == 0 || !Character.isDigit(chars[0])) {
                    Interpreter.printCompileError(currentLine, this.getClass(), "A digit must follow a decimal point.");
                }

                finished = false;
                while (!finished) {
                    if (chars.length > 0 && Character.isDigit(chars[0])) {
                        contents += consume();
                    } else {
                        finished = true;
                    }
                }
            }
        }

        // Update token
        token.contents = contents;

        return token;
    }

    /**
     * <Identifier>::=<Letter>{<Letter>|<Digit>}
     *
     * @return Returns the identifier as a token.
     */
    private Token generateIdentifierToken() {
        Token token = new Token(currentLine, TokenType.IDENTIFIER);
        String contents = "";

        contents += consume();

        if (chars.length > 0) {
            boolean finished = false;
            while (!finished) {
                if (chars.length > 0 && Character.isLetterOrDigit(chars[0])) {
                    contents += consume();
                } else {
                    finished = true;
                }
            }
        }

        token.contents = contents;

        // Check if token is a boolean token
        if (token.contents.equalsIgnoreCase("TRUE") || token.contents.equalsIgnoreCase("FALSE")) {
            token.type = TokenType.BOOLEAN;
            token.contents = token.contents.toUpperCase();
        }

        return token;
    }

    /**
     * <String>::=“{<Digit>|<Letter>|<Symbol>|<Operator>}”
     *
     * @return The string converted to a token.
     */
    private Token generateStringToken() {
        Token token = new Token(currentLine, TokenType.STRING);
        String contents = "";

        consume();

        boolean finished = false;
        while (!finished) {
            if (chars.length == 0) {
                Interpreter.printCompileError(currentLine, this.getClass(), "Cannot find end-quote of string.");
            } else if (chars[0] == '"') {
                finished = true;
                consume();
            } else {
                contents += consume();
            }
        }

        token.contents = contents;

        return token;
    }

    /**
     * Returns the 'index' of the specified operator.
     *
     * @param operator The character to check.
     * @return The index of the operator, 0 if the character is not an operator.
     */
    private int getOperator(char operator) {
        switch (operator) {
            case '+':
                return 1;
            case '-':
                return 2;
            case '*':
                return 3;
            case '/':
                return 4;
            case '^':
                return 5;
            case '>':
                return 6;
            case '<':
                return 7;
            case '!':
                return 8;
            case '=':
                return 9;
            case '&':
                return 10;
            case '|':
                return 11;
            default:
                return 0;
        }
    }

    /**
     * Handles operator and comparator handling.
     *
     * @return Returns the token for the operator.
     */
    private Token generateOperatorToken() {
        Token token = new Token(currentLine);

        int operatorType = getOperator(chars[0]);

        if (operatorType < 6 && operatorType > 0) {
            token.contents += consume();
            token.type = TokenType.OPERATOR;

            return token;
        } else if (operatorType == 10 || operatorType == 11) {
            token.contents += consume();
            token.type = TokenType.RELATIONAL_OPERATOR;

            return token;
        } else if (operatorType == 9) {
            token.contents += consume();
            token.type = TokenType.EQUALS;

            return token;
        } else {
            token.contents += consume();
            token.type = TokenType.COMPARISON;

            if (operatorType >= 6 && operatorType <= 8) {
                if (chars.length > 0 && getOperator(chars[0]) == 9) {
                    token.contents += consume();
                }
            }

            return token;
        }
    }

    private Token generateToken() {
        Token token = null;

        if (chars.length == 0) {
            return null;
        } else if (chars[0] == '/' && chars[1] == '/') {
            return null; // Rest of line is a comment
        } else if (Character.isDigit(chars[0])) {
            return generateNumberToken();
        } else if (Character.isLetter(chars[0])) {
            return generateIdentifierToken();
        } else if (chars[0] == '"') {
            return generateStringToken();
        } else if (getOperator(chars[0]) > 0) {
            return generateOperatorToken();
        } else if (chars[0] == ',') {
            return new Token(currentLine, String.valueOf(consume()), TokenType.SEPARATOR);
        } else if (chars[0] == '(') {
            return new Token(currentLine, String.valueOf(consume()), TokenType.OPEN_BRACKET);
        } else if (chars[0] == ')') {
            return new Token(currentLine, String.valueOf(consume()), TokenType.CLOSE_BRACKET);
        } else if (chars[0] == '[') {
            return new Token(currentLine, String.valueOf(consume()), TokenType.OPEN_SQUARE_BRACKET);
        } else if (chars[0] == ']') {
            return new Token(currentLine, String.valueOf(consume()), TokenType.CLOSE_SQUARE_BRACKET);
        } else if (Character.isWhitespace(chars[0])) {
            return new Token(currentLine, String.valueOf(consume()), TokenType.WHITESPACE);
        } else {
            Interpreter.printCompileError(currentLine, this.getClass(), "Invalid character found, '" + chars[0] + "'.");
        }

        return token;
    }

    public TokenString tokenize(String line, int lineNumber) {
        currentLine = lineNumber;
        chars = line.toCharArray();
        boolean finished = false;
        TokenString tokenString = new TokenString();

        tokenString.line = currentLine;

        while (true) {
            Token currentOutput = new Token();

            currentOutput.line = currentLine;

            // Get next token in the line.
            currentOutput = generateToken();

            if (currentOutput == null) break;

            // Add token to the list of tokens
            tokenString.append(currentOutput);

            // Check if we've reached end of the line.
            if (chars.length == 0) break;

        }

        Interpreter.printStatus(this.getClass(), "Finished tokenizing line.");

        return tokenString;
    }

}
