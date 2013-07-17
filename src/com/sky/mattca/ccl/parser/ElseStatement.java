package com.sky.mattca.ccl.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * User: 06mcarter
 * Date: 06/11/12
 * Time: 13:29
 */
public class ElseStatement extends Statement {

    public List<Statement> contents;

    public ElseStatement() {
        contents = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "ElseStatement{" +
                "contents=" + contents +
                '}';
    }
}
