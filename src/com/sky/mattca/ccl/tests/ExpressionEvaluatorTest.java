package com.sky.mattca.ccl.tests;

import com.sky.mattca.ccl.interpretation.Interpreter;
import com.sky.mattca.ccl.interpretation.ExpressionEvaluator;
import com.sky.mattca.ccl.parser.Expression;
import com.sky.mattca.ccl.tokenizer.TokenString;
import junit.framework.TestCase;

/**
 * User: 06mcarter
 * Date: 07/02/13
 * Time: 14:12
 */
public class ExpressionEvaluatorTest extends TestCase {
    public void testEvaluateExpression() throws Exception {
        // TODO: Divisions by zero are accepted, should probably consider using this as a test plan item
//        TokenString str = Interpreter.tokenizer.tokenize("-3 + -2 * --5", 1);
//        Expression e = Expression.parseExpression(str, false);
//
//        System.out.println("Parsed expression.");
//        System.out.println(ExpressionEvaluator.evaluateExpression(e));
    }
}
