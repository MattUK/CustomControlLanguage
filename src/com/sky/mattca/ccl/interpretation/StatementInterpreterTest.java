package com.sky.mattca.ccl.interpretation;

import com.sky.mattca.ccl.tokenizer.Token;
import junit.framework.TestCase;

/**
 * Created with IntelliJ IDEA.
 * User: Matt
 * Date: 25/04/13
 * Time: 22:59
 * To change this template use File | Settings | File Templates.
 */
public class StatementInterpreterTest extends TestCase {
    StatementInterpreter statementInterpreter = new StatementInterpreter();

    public void testGetVariableTypeFromToken() throws Exception {
        // Test 1
        Token testToken1 = new Token(0, "Test", Token.TokenType.STRING);
        System.out.println(statementInterpreter.getVariableTypeFromToken(testToken1));

        // Test 2
        Token testToken2 = new Token(0, "123", Token.TokenType.NUMBER);
        System.out.println(statementInterpreter.getVariableTypeFromToken(testToken1));

        // Test 3
        Token testToken3 = new Token(0, "TRUE", Token.TokenType.BOOLEAN);
        System.out.println(statementInterpreter.getVariableTypeFromToken(testToken3));

        // Test 4
        Token testToken4 = new Token(0, "testIdentifier", Token.TokenType.IDENTIFIER);
        System.out.println(statementInterpreter.getVariableTypeFromToken(testToken4));
    }

    public void testIsValidArrayIndex() throws Exception {
        // Test 5
        System.out.println(statementInterpreter.isValidArrayIndex(-1));

        // Test 6
        System.out.println(statementInterpreter.isValidArrayIndex(4.5f));

        // Test 7
        System.out.println(statementInterpreter.isValidArrayIndex(-1.54f));

        // Test 8
        System.out.println(statementInterpreter.isValidArrayIndex(12));

        // Test 9
        System.out.println(statementInterpreter.isValidArrayIndex(0));
    }
}
