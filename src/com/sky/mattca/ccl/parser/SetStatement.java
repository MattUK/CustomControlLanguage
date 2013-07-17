package com.sky.mattca.ccl.parser;

import com.sky.mattca.ccl.accessors.ArrayAccessor;
import com.sky.mattca.ccl.tokenizer.Token;

/**
 * User: 06mcarter
 * Date: 04/02/13
 * Time: 11:55
 */
public class SetStatement extends Statement {

    public Token identifier;

    public boolean arraySet;
    public ArrayAccessor accessor;

    public Expression expressionNewValue;
    public CallStatement functionNewValue;

}
