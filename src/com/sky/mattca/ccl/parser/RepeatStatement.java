package com.sky.mattca.ccl.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Repeat statement
 */
public class RepeatStatement extends Statement {

    public List<Statement> statements;
    public UntilStatement untilStatement;

    public RepeatStatement() {
        statements = new ArrayList<>();
    }

}
