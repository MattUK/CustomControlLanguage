package com.sky.mattca.ccl.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * User: 06mcarter
 * Date: 31/01/13
 * Time: 13:07
 */
public class WhileStatement extends Statement {

    public ConditionSequence conditions;
    public List<Statement> statements;

    public WhileStatement() {
        statements = new ArrayList<>();
    }

}
