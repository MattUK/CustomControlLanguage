package com.sky.mattca.ccl.parser;

import com.sky.mattca.ccl.interpretation.InBuiltFunctions;
import com.sky.mattca.ccl.interpretation.Interpreter;
import com.sky.mattca.ccl.exceptions.CallStatementParseException;
import com.sky.mattca.ccl.exceptions.ExpressionParseException;
import com.sky.mattca.ccl.tokenizer.Token;
import com.sky.mattca.ccl.tokenizer.TokenString;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a call to a user-defined, or internal, function
 */
public class CallStatement extends Statement {
    // The name of the function being called
    public Token functionIdentifier;
    // The parameters being passed to the function
    public List<Expression> parameters;

    private CallStatement() {
        parameters = new ArrayList<>();
    }

    /**
     * Constructs a new Call Statement from the supplied token string
     *
     * @param string The Token String to containing the call statement
     * @return A CallStatement object
     * @throws CallStatementParseException
     */
    public static CallStatement parseCallStatement(TokenString string) throws CallStatementParseException {
        CallStatement callStatement = new CallStatement();
        string.removeWhitespace();

        // Check if the string isn't empty, and make sure it begins with an identifier
        if (!string.empty() && string.match(Token.TokenType.IDENTIFIER)) {
            // Check that the function being called by the statement actually exists, otherwise throw an error
            if (Interpreter.functionExists(string.peek()) || InBuiltFunctions.isInBuilt(string.peek().contents)) {
                // Consume the name of the function being called and assign it to the functionIdentifier
                callStatement.functionIdentifier = string.consume();

                // If there are no parameters, return the current CallStatement, otherwise attempt to parse them
                if (string.empty()) {
                    return callStatement;
                } else {
                    try {
                        // Parse the first parameter
                        callStatement.parameters.add(Expression.parseExpression(string, false));
                        // If there are any following parameters, repeatedly parse them as expressions until either the
                        // end is reached or an error occurs.
                        while (string.match(Token.TokenType.SEPARATOR)) {
                            string.consume();
                            if (!string.empty()) {
                                callStatement.parameters.add(Expression.parseExpression(string, false));
                            } else {
                                Interpreter.printCompileError(string.line, CallStatement.class, "Found End of Line, expected parameter expression.");
                            }
                        }
                    } catch (ExpressionParseException e) {
                        Interpreter.printCompileError(e.line, Expression.class, e.exceptionType.message);
                    }

                    // Make sure the line is now empty
                    if (!string.empty()) {
                        throw new CallStatementParseException(string.line, CallStatementParseException.CallStatementExceptionType.EXPECTED_END_OF_LINE);
                    } else {
                        return callStatement;
                    }
                }

            } else {
                throw new CallStatementParseException(string.line, CallStatementParseException.CallStatementExceptionType.UNKNOWN_FUNCTION, string.peek().contents);
            }
        } else {
            Interpreter.printCompileError(string.line, Parser.class, "Could not parse empty line.");
        }
        return null;
    }

    @Override
    /**
     * Returns the CallStatement as a string containing the contents, used for debugging
     */
    public String toString() {
        return "CallStatement{" +
                "functionIdentifier=" + functionIdentifier +
                ", parameters=" + parameters +
                '}';
    }

}
