package com.sky.mattca.ccl.parser;

import com.sky.mattca.ccl.interpretation.Interpreter;
import com.sky.mattca.ccl.exceptions.CallStatementParseException;
import com.sky.mattca.ccl.exceptions.ExpressionParseException;
import com.sky.mattca.ccl.tokenizer.Token;
import com.sky.mattca.ccl.tokenizer.TokenString;

/**
 * User: 06mcarter
 * Date: 20/12/12
 * Time: 14:06
 */
public class VariableDeclaration extends Statement {
    public Token variableIdentifier;
    public Expression expression;
    public CallStatement functionCall;

    public static VariableDeclaration parseVariableDeclaration(TokenString string) {
        // Check to see if the 'expression' begins with an operator. If it does, assume it is an expression.
        // If it does not begin with an operator, check to see if it is two identifiers following one-another = Function Call.
        // Else, expression.

        VariableDeclaration variableStatement = new VariableDeclaration();

        string.removeWhitespace();

        if (string.contentMatch("var")) {
            string.consume();

            if (string.match(Token.TokenType.IDENTIFIER)) {
                variableStatement.variableIdentifier = string.consume();

                if (string.match(Token.TokenType.EQUALS)) {
                    string.consume();

                    if (string.match(Token.TokenType.IDENTIFIER)) {
                        TokenString backupString = string.createCopy();
                        try {
                            variableStatement.functionCall = CallStatement.parseCallStatement(string);
                        } catch (CallStatementParseException e) {
                            // Not a call statement, try to parse as expression
                            string = backupString;
                            try {
                                variableStatement.expression = Expression.parseExpression(string, false);
                            } catch (ExpressionParseException e1) {
                                Interpreter.printCompileError(string.line, VariableDeclaration.class, "Invalid variable declaration.");
                            }
                        }
                    } else {
                        try {
                            variableStatement.expression = Expression.parseExpression(string, false);
                        } catch (ExpressionParseException e) {
                            // Isn't an expression, and cannot be a call statement (as it doesn't begin with an identifier)
                            Interpreter.printCompileError(string.line, VariableDeclaration.class, "Invalid variable declaration.");
                        }
                    }

                    if (!string.empty()) {
                        Interpreter.printCompileError(string.line, VariableDeclaration.class, "Invalid Variable declaration, expected End of Line, found '" + string.peek().contents + "'.");
                    } else {
                        if (variableStatement.functionCall == null) {
                            System.out.println("Parsed expression.");
                        } else {
                            System.out.println("Parsed function call.");
                        }
                        return variableStatement;
                    }

                } else {
                    Interpreter.printCompileError(string.line, VariableDeclaration.class, "Expected '=' to follow variable definition.");
                }
            } else {
                Interpreter.printCompileError(string.line, Parser.class, "Expected identifier for variable name.");
            }

        } else {
            Interpreter.printCompileError(string.line, Parser.class, "Expected 'var'.");
        }
        return null;
    }
}
