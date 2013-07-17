package com.sky.mattca.ccl.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * User: 06mcarter
 * Date: 31/01/13
 * Time: 11:02
 */
public class UntilStatement extends Statement {

    public ConditionSequence conditions;
    public List<Statement> statements;

    public UntilStatement() {
        statements = new ArrayList<>();
    }

}
