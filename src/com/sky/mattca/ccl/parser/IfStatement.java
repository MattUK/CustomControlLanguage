package com.sky.mattca.ccl.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a parsed If statement
 */
public class IfStatement extends Statement {
    // The conditions that must be met for this If statement to execute
    public ConditionSequence conditions;
    // The number of ElseIf statements that are checked if the above conditions are not met
    public List<ElseIfStatement> elseIfStatements;
    // The else statement to be executed if neither of the above evaluate to true
    public ElseStatement elseStatement;
    // The EndIf statement
    public EndIfStatement endIfStatement;
    // The statements to be executed if the conditions are true
    public List<Statement> contents;

    public IfStatement() {
        contents = new ArrayList<>();
        elseIfStatements = new ArrayList<>();
    }

    @Override
    public String toString() {
        String temp = "IfStatement {\n";

        temp += conditions.toString() + "\n";

        for (Statement s : contents) {
            temp += s.toString() + "\n";
        }

        temp += "}";

        return temp;
    }
}
