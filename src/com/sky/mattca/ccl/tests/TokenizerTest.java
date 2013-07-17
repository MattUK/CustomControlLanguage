package com.sky.mattca.ccl.tests;

import com.sky.mattca.ccl.tokenizer.Tokenizer;
import junit.framework.TestCase;

/**
 * User: 06mcarter
 * Date: 25/04/13
 * Time: 15:48
 */
public class TokenizerTest extends TestCase {
    public void testTokenize() throws Exception {
        Tokenizer tokenizer = new Tokenizer();

        // Test 1
        System.out.println(tokenizer.tokenize("", 0));

        // Test 2
        System.out.println(tokenizer.tokenize("//", 0));

        // Test 3
        System.out.println(tokenizer.tokenize("IDENTIFIER //", 0));

        // Test 4
        System.out.println(tokenizer.tokenize("09823", 0));

        // Test 5
        System.out.println(tokenizer.tokenize("98.4", 0));

        // Test 6
        System.out.println(tokenizer.tokenize("98.4.4", 0));

        // Test 7
        System.out.println(tokenizer.tokenize("\"Hello, world!\"", 0));

        // Test 8
        System.out.println(tokenizer.tokenize("\"Hello, world!", 0));

        // Test 9
        System.out.println(tokenizer.tokenize("+", 0));

        // Test 10
        System.out.println(tokenizer.tokenize(",", 0));

        // Test 11
        System.out.println(tokenizer.tokenize("(", 0));

        // Test 12
        System.out.println(tokenizer.tokenize("]", 0));

        // Test 13
        System.out.println(tokenizer.tokenize(" ", 0));
    }
}
