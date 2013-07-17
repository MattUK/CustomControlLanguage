package com.sky.mattca.ccl.parser;

import com.sky.mattca.ccl.interpretation.Interpreter;
import com.sky.mattca.ccl.accessors.BracketConditionSequence;
import com.sky.mattca.ccl.tokenizer.Token;
import com.sky.mattca.ccl.tokenizer.TokenString;

/**
 * User: 06mcarter
 * Date: 05/03/13
 * Time: 14:13
 */
public class ConditionSequence {

    public Condition firstCondition;
    public Token relationalOperator;
    public ConditionSequence followingSequence;

    public ConditionSequence() {
        firstCondition = null;
        relationalOperator = null;
        followingSequence = null;
    }

    public static ConditionSequence parseConditionSequence(TokenString string) {
        ConditionSequence sequence = new ConditionSequence();

        if (string.match(Token.TokenType.OPEN_BRACKET)) {
            sequence.firstCondition = BracketConditionSequence.parseBracketConditionSequence(string);
        } else if (string.match(Token.TokenType.CLOSE_BRACKET)) {
            return sequence;
        } else {
            sequence.firstCondition = Condition.parseCondition(string);
        }

        // Parse relational operator
        if (!string.empty()) {
            if (string.peek().type == Token.TokenType.CLOSE_BRACKET) {
                return sequence;
            }

            if (string.peek().type == Token.TokenType.RELATIONAL_OPERATOR) {
                sequence.relationalOperator = string.consume();

                if (!string.empty()) {
                    sequence.followingSequence = ConditionSequence.parseConditionSequence(string);
                } else {
                    Interpreter.printCompileError(string.line, ConditionSequence.class, "Expected following condition, found End of Line.");
                }

            } else {
                Interpreter.printCompileError(string.line, ConditionSequence.class, "Expected '&' or '|', found '" + string.peek().contents + "'.");
            }
        }

        return sequence;
    }

    @Override
    public String toString() {
        return "ConditionSequence{" +
                "firstCondition=" + firstCondition +
                ", relationalOperator=" + relationalOperator +
                ", followingSequence=" + followingSequence +
                '}';
    }
}
