package com.sky.mattca.ccl.parser;

import com.sky.mattca.ccl.interpretation.Interpreter;
import com.sky.mattca.ccl.exceptions.ExpressionParseException;
import com.sky.mattca.ccl.tokenizer.Token;
import com.sky.mattca.ccl.tokenizer.TokenString;

/**
 * Represents an array declaration
 */
public class ArrayDeclaration extends Statement {

    // The name of the array
    public Token arrayIdentifier;
    // The size of the array
    public Expression size;

    /**
     * Parses an array declaration in the form: ARRAY <Identifier> <Size (an expression)>
     *
     * @param string
     * @return
     */
    public static ArrayDeclaration parseArrayDeclaration(TokenString string) {
        ArrayDeclaration declaration = new ArrayDeclaration();

        // Remove whitespace from the definition
        string.removeWhitespace();
        // Remove the "ARRAY" keyword
        string.consume();

        // Check the definition is followed by an identifier - the name of the array
        if (string.match(Token.TokenType.IDENTIFIER)) {
            // Consume said name and set the arrayIdentifier variable
            declaration.arrayIdentifier = string.consume();
            // Consume the separating comma
            if (string.peek().type == Token.TokenType.SEPARATOR) {
                string.consume();
            } else {
                Interpreter.printCompileError(string.line, Parser.class, "Expected ',', found '" + string.peek().contents + "'.");
            }
            // Attempt to parse the size expression, catch any resulting errors and print them to the output
            try {
                declaration.size = Expression.parseExpression(string, false);
            } catch (ExpressionParseException e) {
                Interpreter.printCompileError(string.line, Parser.class, e.exceptionType.message.replace("%s", e.source));
            }
        } else {
            Interpreter.printCompileError(string.line, Parser.class, "Expected identifier, found '" + string.peek().contents + "'.");
        }

        // Make sure what follows the size is an empty line, otherwise the statement is incorrect
        if (!string.empty()) {
            Interpreter.printCompileError(string.line, Parser.class, "Expected end of line, found '" + string.peek().contents + "'.");
        }

        // Return the new array declaration
        return declaration;
    }

}
