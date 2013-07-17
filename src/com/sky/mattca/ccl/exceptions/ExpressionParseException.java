package com.sky.mattca.ccl.exceptions;

/**
 * User: 06mcarter
 * Date: 24/01/13
 * Time: 13:19
 */
public class ExpressionParseException extends Exception {

    public static enum ExpressionExceptionType {
        INVALID_OPERATOR("Invalid operator found, '%s'."),
        INVALID_TOKEN("Invalid token found, '%s'."),
        EXPECTED_VALUE("Expected expression to follow operator, found end of line.");

        public String message;

        ExpressionExceptionType(String message) {
            this.message = message;
        }
    }

    public ExpressionExceptionType exceptionType;
    public int line;
    public String source;

    public ExpressionParseException(int line, ExpressionExceptionType type, String sourceValue) {
        exceptionType = type;
        this.line = line;
        source = sourceValue;
    }

}
