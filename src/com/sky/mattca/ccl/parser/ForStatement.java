package com.sky.mattca.ccl.parser;

import com.sky.mattca.ccl.tokenizer.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a parsed For statement
 */
public class ForStatement extends Statement {

    // The 'counter' variable
    public Token variable;
    // The lower boundary (must evaluate to an integer)
    public Expression minimumBoundary;
    // The upper boundary (must evaluate to an integer)
    public Expression maximumBoundary;
    // The list of statements to be executed each loop
    public List<Statement> statements;

    public ForStatement() {
        statements = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "ForStatement{" +
                "variable=" + variable +
                ", minimumBoundary=" + minimumBoundary +
                ", maximumBoundary=" + maximumBoundary +
                ", statements=" + statements +
                '}';
    }
}
