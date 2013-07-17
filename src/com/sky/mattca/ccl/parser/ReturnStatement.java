package com.sky.mattca.ccl.parser;

import com.sky.mattca.ccl.interpretation.Interpreter;
import com.sky.mattca.ccl.exceptions.CallStatementParseException;
import com.sky.mattca.ccl.exceptions.ExpressionParseException;
import com.sky.mattca.ccl.tokenizer.Token;
import com.sky.mattca.ccl.tokenizer.TokenString;

/**
 * User: 06mcarter
 * Date: 28/02/13
 * Time: 13:59
 */
public class ReturnStatement extends Statement {

    public Expression expression;
    public CallStatement callStatement;

    public static ReturnStatement parseReturnStatement(TokenString string) {
        TokenString returnDefinition = string.createCopy();
        ReturnStatement statement = new ReturnStatement();

        returnDefinition.removeWhitespace();
        returnDefinition.consume();

        TokenString backupString = returnDefinition.createCopy();
        if (returnDefinition.match(Token.TokenType.IDENTIFIER)) {
            try {
                statement.callStatement = CallStatement.parseCallStatement(returnDefinition);
            } catch (CallStatementParseException e) {
                returnDefinition = backupString;
                try {
                    statement.expression = Expression.parseExpression(returnDefinition, false);
                } catch (ExpressionParseException e1) {
                    Interpreter.printCompileError(returnDefinition.line, ReturnStatement.class, "Invalid return statement, expected expression or function call.");
                }
            }
        } else {
            try {
                statement.expression = Expression.parseExpression(returnDefinition, false);
            } catch (ExpressionParseException e) {
                Interpreter.printCompileError(returnDefinition.line, ReturnStatement.class, "Invalid return statement, expected expression or function call.");
            }
        }

        if (!returnDefinition.empty()) {
            Interpreter.printCompileError(returnDefinition.line, Parser.class, "Expected End of Line, found '" + returnDefinition.peek().contents + "'.");
        }

        return statement;
    }

}
