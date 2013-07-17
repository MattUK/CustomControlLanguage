package com.sky.mattca.ccl.exceptions;

/**
 * User: 06mcarter
 * Date: 24/01/13
 * Time: 13:41
 */
public class CallStatementParseException extends Exception {

    public static enum CallStatementExceptionType {
        EXPECTED_END_OF_LINE("Expected End of Line or parameter separator."),
        UNKNOWN_FUNCTION("Unknown function found: %s.");

        public String message;

        CallStatementExceptionType(String message) {
            this.message = message;
        }
    }

    public CallStatementExceptionType exceptionType;
    public String cause;
    public int line;

    public CallStatementParseException(int line, CallStatementExceptionType type) {
        exceptionType = type;
        this.line = line;
    }

    public CallStatementParseException(int line, CallStatementExceptionType type, String cause) {
        exceptionType = type;
        this.line = line;
        this.cause = cause;

    }

}
