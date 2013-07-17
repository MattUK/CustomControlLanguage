package com.sky.mattca.ccl.parser;

import com.sky.mattca.ccl.tokenizer.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a parsed Function definition statement
 */
public class FunctionStatement extends Statement {
    // The name of the function being defined
    public Token functionIdentifier;
    // The list of parameters (stored as identifiers) that this function accepts
    public List<Token> parameters;
    // The statements that are executed when this function is called
    public List<Statement> contents;

    public FunctionStatement() {
        parameters = new ArrayList<>();
        contents = new ArrayList<>();
    }
}
