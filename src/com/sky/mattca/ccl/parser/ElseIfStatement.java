package com.sky.mattca.ccl.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an ElseIf statement
 */
public class ElseIfStatement extends Statement {

    // The conditions that need to be met for this ElseIf statement to execute
    public ConditionSequence conditions;
    // The statements that will be executed if the conditions evaluate to true
    public List<Statement> contents;

    public ElseIfStatement() {
        contents = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "ElseIfStatement{" +
                "conditions=" + conditions +
                ", contents=" + contents +
                '}';
    }
}
