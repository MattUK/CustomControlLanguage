package com.sky.mattca.ccl.tests;

import com.sky.mattca.ccl.interpretation.Interpreter;
import com.sky.mattca.ccl.exceptions.CallStatementParseException;
import com.sky.mattca.ccl.parser.CallStatement;
import com.sky.mattca.ccl.tokenizer.TokenString;
import junit.framework.TestCase;

/**
 * User: 06mcarter
 * Date: 10/01/13
 * Time: 14:30
 */
public class CallStatementTest extends TestCase {
    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testParseCallStatement() throws Exception {
//        try {
//        TokenString str = Interpreter.tokenizer.tokenize("PRINT 3 + 2, 3 + 1 9", 1);
//        CallStatement callStatement = CallStatement.parseCallStatement(str.removeWhitespace());
//        } catch (CallStatementParseException e) {
//            Interpreter.printRuntimeError(CallStatement.class, e.exceptionType.message.replace("%s", e.cause));
//        }
    }
}
