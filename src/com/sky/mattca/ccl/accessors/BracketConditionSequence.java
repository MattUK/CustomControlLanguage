package com.sky.mattca.ccl.accessors;

import com.sky.mattca.ccl.interpretation.Interpreter;
import com.sky.mattca.ccl.parser.Condition;
import com.sky.mattca.ccl.parser.ConditionSequence;
import com.sky.mattca.ccl.tokenizer.Token;
import com.sky.mattca.ccl.tokenizer.TokenString;

/**
 * User: 06mcarter
 * Date: 05/03/13
 * Time: 14:21
 */
public class BracketConditionSequence extends Condition {

    public Token openBracketToken;
    public ConditionSequence conditionSequence;
    public Token closeBracketToken;

    private BracketConditionSequence() {
        super();
    }

    public static BracketConditionSequence parseBracketConditionSequence(TokenString string) {
        BracketConditionSequence bracketConditionSequence = new BracketConditionSequence();
        if (string.match(Token .TokenType.OPEN_BRACKET)) {
            bracketConditionSequence.openBracketToken = string.consume();
            bracketConditionSequence.conditionSequence = ConditionSequence.parseConditionSequence(string);
            if (string.match(Token.TokenType.CLOSE_BRACKET)) {
                bracketConditionSequence.closeBracketToken = string.consume();
                return bracketConditionSequence;
            } else if (!string.empty()) {
                Interpreter.printCompileError(string.line, BracketConditionSequence.class, "Expected ')', found '" + string.peek().contents + "'.");
            } else {
                Interpreter.printCompileError(string.line, BracketConditionSequence.class, "Expected ')', found end of line.");
            }
        } else {
            Interpreter.printCompileError(string.line, BracketConditionSequence.class, "Expected '(', found '" + string.peek().contents + "'.");
        }
        return null;
    }

    @Override
    public String toString() {
        return "BracketConditionSequence{" +
                "openBracketToken=" + openBracketToken +
                ", conditionSequence=" + conditionSequence +
                ", closeBracketToken=" + closeBracketToken +
                '}';
    }
}
