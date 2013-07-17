package com.sky.mattca.ccl.tests;

import com.sky.mattca.ccl.interpretation.Interpreter;
import com.sky.mattca.ccl.parser.Expression;
import com.sky.mattca.ccl.tokenizer.TokenString;
import com.sky.mattca.ccl.tokenizer.Tokenizer;
import junit.framework.TestCase;

/**
 * User: 06mcarter
 * Date: 24/01/13
 * Time: 14:20
 */
public class ExpressionTest extends TestCase {
    public void testParseExpression() throws Exception {
        Tokenizer tokenizer = new Tokenizer();

        TokenString str = tokenizer.tokenize("\"hello\" + (3 * 4", 1);
        Expression e = Expression.parseExpression(str, false);
    }
}
