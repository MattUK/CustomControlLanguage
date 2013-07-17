package com.sky.mattca.ccl.tests;

import com.sky.mattca.ccl.interpretation.Interpreter;
import com.sky.mattca.ccl.parser.Parser;
import com.sky.mattca.ccl.parser.Statement;
import com.sky.mattca.ccl.tokenizer.TokenString;
import com.sky.mattca.ccl.tokenizer.Tokenizer;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * User: 06mcarter
 * Date: 29/01/13
 * Time: 13:08
 */
public class ParserTest extends TestCase {

    private Tokenizer tokenizer = new Tokenizer();
    private Parser parser = new Parser();

    public void testPerformParse() throws Exception {
        // Test 1
//        List<TokenString> lines = new ArrayList<>();
//        lines.add(tokenizer.tokenize("Function TestFunction(a, b", 0));
//        lines.add(tokenizer.tokenize("Return 1", 1));
//        lines.add(tokenizer.tokenize("EndFunction", 2));
//        parser.setLines(lines);
//        parser.performParse();

        // Test 2
//        List<TokenString> lines = new ArrayList<>();
//        lines.add(tokenizer.tokenize("Function TestFunction2(a, b)", 0));
//        lines.add(tokenizer.tokenize("Return 2", 1));
//        lines.add(tokenizer.tokenize("EndFunction", 2));
//        parser.setLines(lines);
//        parser.performParse();

        // Test 3
//        List<TokenString> lines = new ArrayList<>();
//        lines.add(tokenizer.tokenize("If (1 + 2 = 3)", 0));
//        lines.add(tokenizer.tokenize("Print \"Hello, World\"", 1));
//        lines.add(tokenizer.tokenize("EndIf", 2));
//        parser.setLines(lines);
//        parser.performParse();

        // Test 4
//        List<TokenString> lines = new ArrayList<>();
//        lines.add(tokenizer.tokenize("If (a + b = 3) Then", 0));
//        lines.add(tokenizer.tokenize("Print \"Hello, World\"", 1));
//        lines.add(tokenizer.tokenize("ElseIf (a + b = 4) Then", 2));
//        lines.add(tokenizer.tokenize("Print \"Hello!\"", 3));
//        lines.add(tokenizer.tokenize("Else", 4));
//        lines.add(tokenizer.tokenize("Print a + b", 5));
//        parser.setLines(lines);
//        parser.performParse();

        // Test 5
//        List<TokenString> lines = new ArrayList<>();
//        lines.add(tokenizer.tokenize("For (x = 1)", 0));
//        lines.add(tokenizer.tokenize("Print x", 1));
//        lines.add(tokenizer.tokenize("Next", 2));
//        parser.setLines(lines);
//        parser.performParse();

        // Test 6
//        List<TokenString> lines = new ArrayList<>();
//        lines.add(tokenizer.tokenize("For (x = 1 To)", 0));
//        lines.add(tokenizer.tokenize("Print x", 1));
//        lines.add(tokenizer.tokenize("Next", 2));
//        parser.setLines(lines);
//        parser.performParse();

        // Test 7
//        List<TokenString> lines = new ArrayList<>();
//        lines.add(tokenizer.tokenize("While (true = true)", 0));
//        lines.add(tokenizer.tokenize("Print \"Hello, World\"", 1));
//        parser.setLines(lines);
//        parser.performParse();

        // Test 8
//        List<TokenString> lines = new ArrayList<>();
//        lines.add(tokenizer.tokenize("While (true = true)", 0));
//        lines.add(tokenizer.tokenize("Print \"Hello, World\"", 1));
//        lines.add(tokenizer.tokenize("Wend", 2));
//        parser.setLines(lines);
//        parser.performParse();

        // Test 9
//        List<TokenString> lines = new ArrayList<>();
//        lines.add(tokenizer.tokenize("Repeat", 0));
//        lines.add(tokenizer.tokenize("X = X + 1", 1));
//        lines.add(tokenizer.tokenize("Print X", 2));
//        lines.add(tokenizer.tokenize("Until (X = 5)", 3));
//        parser.setLines(lines);
//        parser.performParse();

        // Test 10
//        List<TokenString> lines = new ArrayList<>();
//        lines.add(tokenizer.tokenize("Repeat", 0));
//        lines.add(tokenizer.tokenize("X = X + 1", 1));
//        lines.add(tokenizer.tokenize("Print X", 2));
//        lines.add(tokenizer.tokenize("Until ()", 3));
//        parser.setLines(lines);
//        parser.performParse();
    }
}
