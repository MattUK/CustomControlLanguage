package com.sky.mattca.ccl.interpretation;

import com.sky.mattca.ccl.tokenizer.Token;
import com.sky.mattca.ccl.tokenizer.Token.TokenType;

import java.util.*;

/**
 * User: Matt
 * Date: 12/10/12
 * Time: 01:24
 */
public class ShuntingYard {

    private List<Token> tokens;
    private Stack<Token> stack;
    private Queue<Token> outputQueue;

    public ShuntingYard() {
        stack = new Stack<>();
        outputQueue = new LinkedList<>();
    }

    /**
     * Returns the precedence (an integer) of the specified token.
     *
     * @param token The token to check.
     * @return The precedence of said token.
     */
    public static int precedence(Token token) {
        switch (token.contents) {
            case "_":
                return 5;
            case "^":
                return 4;
            case "*":
            case "/":
                return 3;
            case "+":
            case "-":
                return 2;
            case "=":
            case "<":
            case ">":
            case "<=":
            case ">=":
            case "!=":
                return 1;
            case "&":
            case "|":
                return 0;
            default:
                return -1;
        }
    }

    /**
     * Used to confirm whether or not an operator is left associative.
     *
     * @param token The operator to check.
     * @return True if the oeprator is left-associative, false otherwise
     */
    public static boolean isLeftAssociative(Token token) {
        switch (token.contents) {
            case "^":
            case "_":
            case "=":
            case "<":
            case ">":
            case "<=":
            case ">=":
            case "!=":
                return false;
            default:
                return true;
        }
    }

    /**
     * Returns the last token that ISN'T whitespace (used for negative number
     * identification).
     *
     * @param index The starting index to look back from.
     * @return The token found.
     */
    public Token getLastExcludingWhitespace(int index) {
        try {
            for (int i = index; i >= 0; i--) {
                if (tokens.get(i).type != TokenType.WHITESPACE) {
                    return tokens.get(i);
                }
            }
        } catch (Exception e) {
        }

        return new Token();
    }

    /**
     * Returns the closest token that ISN'T whitespace (used for function
     * identification).
     *
     * @param index The starting index to look forward from/
     * @return The token found.
     */
    public Token getNextExcludingWhitespace(int index) {
        try {
            for (int i = index; i < tokens.size(); i++) {
                if (tokens.get(i).type != TokenType.WHITESPACE) {
                    return tokens.get(i);
                }
            }
        } catch (Exception e) {
        }

        return new Token();
    }

    /**
     * Implementation of Edsger W Dijkstra's shunting yard algorithm. Converts
     * infix (3 + 2, etc) equations to postfix (3 2 +) notation.
     * <p/>
     * Modified to incorporate variable identifiers, negative
     * numbers, and equalities.
     * <p/>
     * Based on the algorithm described at http://en.wikipedia.org/wiki/Shunting-yard_algorithm
     */
    public List<Token> doShuntingYard() {
        int index = 0;
        while (index != tokens.size()) {
            Token currentToken = tokens.get(index);

            if (currentToken.type == TokenType.WHITESPACE) {
                // Ignore whitespace
            } else if (currentToken.type == TokenType.NUMBER
                    || currentToken.type == TokenType.STRING
                    || currentToken.type == TokenType.BOOLEAN
                    || currentToken.type == TokenType.IDENTIFIER
                    || currentToken.type == TokenType.ARRAY_ACCESSOR) {
                if (getNextExcludingWhitespace(index + 1).type == TokenType.OPEN_BRACKET) {
                    Interpreter.printCompileError(currentToken.line, this.getClass(), "Expected operator, found '('.");
                } else if (currentToken.type == TokenType.NUMBER && (getNextExcludingWhitespace(index + 1).type == TokenType.IDENTIFIER || getNextExcludingWhitespace(index + 1).type == TokenType.ARRAY_ACCESSOR)) {
                    Interpreter.printCompileError(currentToken.line, this.getClass(), "Expected operator, found '" + getNextExcludingWhitespace(index + 1).contents + "'.");
                }
                outputQueue.add(currentToken);
            } else if (currentToken.type == TokenType.OPERATOR || currentToken.type == TokenType.COMPARISON || currentToken.type == TokenType.RELATIONAL_OPERATOR) {
                // Check for unary minus
                if (currentToken.contents.equals("-")) {
                    if (getLastExcludingWhitespace(index - 1).type == TokenType.OPERATOR
                            || getLastExcludingWhitespace(index - 1).type == TokenType.COMPARISON
                            || getLastExcludingWhitespace(index - 1).type == TokenType.RELATIONAL_OPERATOR
                            || getLastExcludingWhitespace(index - 1).type == TokenType.NONE) {
                        currentToken.contents = "_";
                    }
                }

                if (!stack.empty()) {
                    while (stack.peek().type == TokenType.OPERATOR || stack.peek().type == TokenType.COMPARISON
                            || stack.peek().type == TokenType.RELATIONAL_OPERATOR) {
                        if (isLeftAssociative(currentToken) && precedence(currentToken) <= precedence(stack.peek())
                                || precedence(currentToken) < precedence(stack.peek())) {
                            outputQueue.add(stack.pop());
                        } else {
                            break;
                        }

                        if (stack.empty()) {
                            break;
                        }
                    }
                }
                stack.push(currentToken);
            } else if (currentToken.type == TokenType.OPEN_BRACKET) {
                stack.push(currentToken);
            } else if (currentToken.type == TokenType.CLOSE_BRACKET) {
                boolean foundParenthesis = false;

                while (stack.size() > 0) {
                    if (stack.peek().type == TokenType.OPEN_BRACKET) {
                        foundParenthesis = true;
                        break;
                    } else {
                        outputQueue.add(stack.pop());
                    }
                }

                if (!foundParenthesis) {
                    Interpreter.printCompileError(currentToken.line, this.getClass(), "Expected ')'.");
                } else {
                    stack.pop();

                    if (stack.size() > 0 && (Interpreter.functionExists(stack.peek()) || InBuiltFunctions.isInBuilt(stack.peek().contents))) {
                        outputQueue.add(stack.pop());
                    }
                }
            } else {
                Interpreter.printCompileError(currentToken.line, this.getClass(), "Invalid symbol found '" + currentToken.contents + "'.");
            }

            index++;
        }

        while (stack.empty() != true) {
            if (stack.peek().type == TokenType.OPEN_BRACKET || stack.peek().type == TokenType.CLOSE_BRACKET) {
                Interpreter.printCompileError(stack.peek().line, this.getClass(), "Expected ')'.");
            } else {
                outputQueue.add(stack.pop());
            }
        }

        List<Token> tokenList = new ArrayList<>(outputQueue);
        return tokenList;
    }

    public List<Token> convertExpression(List<Token> tokens) {
        this.tokens = tokens;

        return doShuntingYard();
    }

}
