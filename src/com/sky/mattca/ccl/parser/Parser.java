package com.sky.mattca.ccl.parser;

import com.sky.mattca.ccl.interpretation.Interpreter;
import com.sky.mattca.ccl.accessors.ArrayAccessor;
import com.sky.mattca.ccl.exceptions.CallStatementParseException;
import com.sky.mattca.ccl.exceptions.ExpressionParseException;
import com.sky.mattca.ccl.tokenizer.Token;
import com.sky.mattca.ccl.tokenizer.TokenString;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * User: Matt
 * Date: 11/10/12
 * Time: 22:56
 */
public class Parser {

    private enum ParserStatus {
        PARSING_FUNCTION,
        PARSING_IF,
        PARSING_FOR,
        PARSING_WHILE,
        PARSING_REPEAT,
        PARSING_SWITCH,
    }

    private int currentLine;
    private List<TokenString> lines;

    private Stack<ParserStatus> statusStack;

    public Parser() {
        currentLine = 0;
        lines = new ArrayList<>();
        statusStack = new Stack<>();
    }

    /* -- FUNCTION STATEMENTS -- */

    /**
     * Parses a function definition and the function's contents.
     *
     * @return Returns a FunctionStatement.
     */
    private Statement functionStatement() {
        // Fetch the string of tokens contained in the Function definition line.
        TokenString functionDefinition = lines.get(currentLine).createCopy();
        // The statement which is returned by the parser.
        FunctionStatement statement = new FunctionStatement();

        // Removes any whitespace from the function definition.
        functionDefinition.removeWhitespace();

        // Consume 'Function' token
        functionDefinition.consume();

        // Consume Function Name
        statement.functionIdentifier = functionDefinition.consume();

        // Check if the function already exists
        if (Interpreter.functionExists(statement.functionIdentifier.contents)) {
            Interpreter.printCompileError(functionDefinition.line, Parser.class, "'" + statement.functionIdentifier.contents + "' is already defined.");
        }

        // Check 'parsing level' of function, must be top.
        if (!statusStack.empty()) {
            Interpreter.printCompileError(functionDefinition.line, Parser.class, "Function statement cannot be nested.");
        }

        // Check whether or not the Function definition is followed by a bracket.
        if (functionDefinition.match(Token.TokenType.OPEN_BRACKET)) {
            // If the Function identifier is followed by an open bracket, check if it is immediately followed by a close bracket.
            if (functionDefinition.match(Token.TokenType.OPEN_BRACKET, Token.TokenType.CLOSE_BRACKET)) {
                // Consume (remove) the two brackets.
                functionDefinition.consume();
                functionDefinition.consume();
            } else {
                // Consume the open bracket, and, assuming the syntax is correct, parse the Function's parameter definition.
                functionDefinition.consume();
                // Continue parsing parameter definitions until we find a close bracket.
                while (!functionDefinition.match(Token.TokenType.CLOSE_BRACKET)) {
                    if (functionDefinition.match(Token.TokenType.IDENTIFIER)) {
                        statement.parameters.add(functionDefinition.consume());
                        if (functionDefinition.match(Token.TokenType.SEPARATOR)) {
                            functionDefinition.consume(); // Eat the seperator
                        } else if (functionDefinition.match(Token.TokenType.CLOSE_BRACKET)) {
                            functionDefinition.consume(); // Eat the close bracket, finish the statement.
                            break;
                        } else {
                            Interpreter.printCompileError(functionDefinition.line, this.getClass(), "Expected seperator or ')', found '" +
                                    (functionDefinition.peek() != null ? functionDefinition.peek().contents : "end of line") + "'.");
                        }
                    } else {
                        Interpreter.printCompileError(functionDefinition.line, this.getClass(), "Expected identifier, found '" +
                                (functionDefinition.peek() != null ? functionDefinition.peek().contents : "end of line") + "'.");
                    }
                }
            }
        } else {
            if (!functionDefinition.empty()) {
                Interpreter.printCompileError(functionDefinition.line, this.getClass(), "Expected '(' or end of line, found " + functionDefinition.peek().contents + ".");
            }
        }

        if (!functionDefinition.empty()) {
            Interpreter.printCompileError(functionDefinition.line, this.getClass(), "Expected end of line, found '" + functionDefinition.peek().contents + "'.");
        }

        statusStack.push(ParserStatus.PARSING_FUNCTION);

        // Parse following statements
        currentLine++;
        Statement nextStatement = new Statement();
        // Repeat statement parsing until an EndFunction statement is found.
        while (!(nextStatement instanceof EndFunctionStatement)) {
            nextStatement = parseTokenLine(currentLine);

            if (nextStatement == null) {
                Interpreter.printCompileError(currentLine, this.getClass(), "Could not find \"EndFunction\" statement.");
            }

            statement.contents.add(nextStatement);
        }

        // Add final statement to the interpreter
        Interpreter.addFunction(new Interpreter.FunctionData(statement.functionIdentifier.contents.toUpperCase(), statement));

        return statement;
    }

    /**
     * Parses an EndFunction statement.
     *
     * @return The parsed EndFunction statement.
     */
    private Statement endFunctionStatement() {
        TokenString endFunctionDefinition = lines.get(currentLine).createCopy();
        EndFunctionStatement statement = new EndFunctionStatement();

        // Check parser status to confirm a function is being parsed.
        if (statusStack.empty() || statusStack.pop() != ParserStatus.PARSING_FUNCTION) {
            Interpreter.printCompileError(endFunctionDefinition.line, Parser.class, "EndFunction statement must be preceded by a Function definition.");
        }

        // Consume 'EndFunction' token
        endFunctionDefinition.consume();

        if (!endFunctionDefinition.empty()) {
            Interpreter.printCompileError(currentLine, this.getClass(), "Expected end of line, found '" + endFunctionDefinition.peek().contents + "'.");
        }

        currentLine++;
        return statement;
    }

    /* -- IF STATEMENTS -- */

    /**
     * Parses an If statement, including it's conditions, following Else(If)'s, and EndIf statements.
     *
     * @return Returns a complete If statement, including nested code and routines.
     */
    private Statement ifStatement() {
        TokenString ifStatementDefinition = lines.get(currentLine).createCopy();
        IfStatement statement = new IfStatement();
        ifStatementDefinition.removeWhitespace();

        // Consume initial 'If' token
        ifStatementDefinition.consume();

        // Check if statement is followed by an open bracket
        if (ifStatementDefinition.match(Token.TokenType.OPEN_BRACKET)) {
            ifStatementDefinition.consume();
        } else {
            Interpreter.printCompileError(ifStatementDefinition.line, Parser.class, "Expected '(' following 'IF'.");
        }

        // Parse first condition
        statement.conditions = ConditionSequence.parseConditionSequence(ifStatementDefinition);

        if (statement.conditions.firstCondition == null) {
            Interpreter.printCompileError(ifStatementDefinition.line, Parser.class, "Expected Condition, found empty line.");
        }

        // Parse closing bracket
        if (ifStatementDefinition.match(Token.TokenType.CLOSE_BRACKET)) {
            ifStatementDefinition.consume();
        } else {
            Interpreter.printCompileError(ifStatementDefinition.line, Parser.class, "Expected ')' closing IF statement.");
        }

        // Parse 'THEN' statement
        if (!ifStatementDefinition.match(Token.TokenType.IDENTIFIER)) {
            Interpreter.printCompileError(ifStatementDefinition.line, Parser.class, "Expected 'THEN' to follow IF statement.");
        } else {
            if (!ifStatementDefinition.peek().contents.toUpperCase().equals("THEN")) {
                Interpreter.printCompileError(ifStatementDefinition.line, Parser.class, "Expected 'THEN' to follow IF statement.");
            } else {
                ifStatementDefinition.consume();
            }
        }

        // Make sure there are no further characters following the 'then' statement.
        if (!ifStatementDefinition.empty()) {
            Interpreter.printCompileError(ifStatementDefinition.line, Parser.class, "Expected end of line, found '" + ifStatementDefinition.peek().contents + "'.");
        }

        // Inform future operations that we are parsing an If statement.
        statusStack.push(ParserStatus.PARSING_IF);

        // Parse following statements
        currentLine++;
        Statement nextStatement = null;

        // Repeat parsing statements until an EndIf statement is found.
        while (!(nextStatement instanceof EndIfStatement)) {
            nextStatement = parseTokenLine(currentLine);

            if (nextStatement == null) {
                Interpreter.printCompileError(currentLine, this.getClass(), "Could not find \"EndIf\" statement.");
            } else if (nextStatement instanceof ElseIfStatement) {
                statement.elseIfStatements.add((ElseIfStatement) nextStatement);
            } else if (nextStatement instanceof ElseStatement) {
                if (statement.elseStatement != null) {
                    Interpreter.printCompileError(ifStatementDefinition.line, Parser.class, "IF statement cannot have more than one ELSE statement.");
                }
                statement.elseStatement = (ElseStatement) nextStatement;
            } else if (nextStatement instanceof EndIfStatement) {
                statement.endIfStatement = (EndIfStatement) nextStatement;
            } else {
                statement.contents.add(nextStatement);
            }
        }

        if (statement.endIfStatement == null) {
            Interpreter.printCompileError(ifStatementDefinition.line, Parser.class, "If statement must be followed by an EndIf statement.");
        }

        return statement;
    }

    private Statement elseIfStatement() {
        TokenString elseIfDefinition = lines.get(currentLine).createCopy();
        ElseIfStatement statement = new ElseIfStatement();
        elseIfDefinition.removeWhitespace();

        // Check stored statuses to make sure an IF statement has been found, if not, report an error.
        if (!statusStack.empty()) {
            ParserStatus status = statusStack.peek();

            if (status != ParserStatus.PARSING_IF) {
                Interpreter.printCompileError(elseIfDefinition.line, Parser.class, "ElseIf must be preceeded by an If statement.");
            }
        } else {
            Interpreter.printCompileError(elseIfDefinition.line, Parser.class, "ElseIf statement must be preceded by an If statement.");
        }

        // Consume initial 'If' token
        elseIfDefinition.consume();

        if (elseIfDefinition.match(Token.TokenType.OPEN_BRACKET)) {
            elseIfDefinition.consume();
        } else {
            Interpreter.printCompileError(elseIfDefinition.line, Parser.class, "Expected '(' following 'ELSEIF'.");
        }

        // Parse first condition
        statement.conditions = ConditionSequence.parseConditionSequence(elseIfDefinition);

        if (statement.conditions.firstCondition == null) {
            Interpreter.printCompileError(elseIfDefinition.line, Parser.class, "Expected Condition, found empty line.");
        }

        // Parse closing bracket
        if (elseIfDefinition.match(Token.TokenType.CLOSE_BRACKET)) {
            elseIfDefinition.consume();
        } else {
            Interpreter.printCompileError(elseIfDefinition.line, Parser.class, "Expected ')' closing ELSEIF statement.");
        }

        // Parse 'THEN' statement
        if (!elseIfDefinition.match(Token.TokenType.IDENTIFIER)) {
            Interpreter.printCompileError(elseIfDefinition.line, Parser.class, "Expected 'THEN' to follow ELSEIF statement.");
        } else {
            if (!elseIfDefinition.peek().contents.toUpperCase().equals("THEN")) {
                Interpreter.printCompileError(elseIfDefinition.line, Parser.class, "Expected 'THEN' to follow ELSEIF statement.");
            } else {
                elseIfDefinition.consume();
            }
        }

        if (!elseIfDefinition.empty()) {
            Interpreter.printCompileError(elseIfDefinition.line, Parser.class, "Expected end of line, found '" + elseIfDefinition.peek().contents + "'.");
        }

        // Parse following statements
        currentLine++;

        while (true) {
            Token followingLineToken = getFollowingLineIdentifier();
            if (followingLineToken != null) {
                if (followingLineToken.contents.equalsIgnoreCase("ELSEIF") || followingLineToken.contents.equalsIgnoreCase("ELSE") || followingLineToken.contents.equalsIgnoreCase("ENDIF")) {
                    break;
                } else {
                    statement.contents.add(parseTokenLine(currentLine));
                }
            } else {
                Interpreter.printCompileError(elseIfDefinition.line, Parser.class, "Could not find \"EndIf\" statement.");
            }
        }

        return statement;
    }

    private Statement elseStatement() {
        TokenString elseDefinition = lines.get(currentLine).createCopy();
        ElseStatement statement = new ElseStatement();
        elseDefinition.removeWhitespace();
        elseDefinition.consume();

        if (!statusStack.empty()) {
            ParserStatus status = statusStack.peek();

            if (status != ParserStatus.PARSING_IF) {
                Interpreter.printCompileError(elseDefinition.line, Parser.class, "Else must be preceeded by an If or ElseIf statement.");
            }
        } else {
            Interpreter.printCompileError(elseDefinition.line, Parser.class, "Else statement must be preceded by an If or ElseIf statement.");
        }

        if (!elseDefinition.empty()) {
            Interpreter.printCompileError(elseDefinition.line, Parser.class, "Expected end of line, found '" + elseDefinition.peek().contents + "'.");
        }

        currentLine++;

        while (true) {
            Token followingLineToken = getFollowingLineIdentifier();
            if (followingLineToken != null) {
                if (followingLineToken.contents.equalsIgnoreCase("ENDIF")) {
                    break;
                } else if (followingLineToken.contents.equalsIgnoreCase("ELSEIF") || followingLineToken.contents.equalsIgnoreCase("ELSE")) {
                    Interpreter.printCompileError(elseDefinition.line, Parser.class, "Else statement cannot be followed by ElseIf or Else, must be EndIf.");
                } else {
                    statement.contents.add(parseTokenLine(currentLine));
                }
            } else {
                Interpreter.printCompileError(elseDefinition.line, Parser.class, "Could not find \"EndIf\" statement.");
            }
        }

        return statement;
    }

    private Statement endIfStatement() {
        TokenString endIfDefinition = lines.get(currentLine).createCopy();
        endIfDefinition.removeWhitespace();

        // Consume 'EndIf' token
        endIfDefinition.consume();

        if (!statusStack.empty()) {
            ParserStatus status = statusStack.peek();

            if (status != ParserStatus.PARSING_IF) {
                Interpreter.printCompileError(endIfDefinition.line, Parser.class, "EndIf must be preceeded by an If, ElseIf, or Else statement.");
            } else {
                statusStack.pop();
            }
        } else {
            Interpreter.printCompileError(endIfDefinition.line, Parser.class, "EndIf must be preceeded by an If, ElseIf, or Else statement.");
        }

        if (!endIfDefinition.empty()) {
            Interpreter.printCompileError(endIfDefinition.line, Parser.class, "Expected end of line, found '" + endIfDefinition.peek().contents + "'.");
        }

        currentLine++;

        return new EndIfStatement();
    }

    /* -- FOR STATEMENT -- */
    private Statement forStatement() {
        TokenString forDefinition = lines.get(currentLine).createCopy();
        ForStatement forStatement = new ForStatement();

        forDefinition.removeWhitespace();

        // Consume 'For' token
        forDefinition.consume();

        if (forDefinition.match(Token.TokenType.OPEN_BRACKET)) {
            forDefinition.consume();
            if (forDefinition.match(Token.TokenType.IDENTIFIER)) {
                forStatement.variable = forDefinition.consume();
                if (forDefinition.match(Token.TokenType.EQUALS)) {
                    forDefinition.consume();

                    // Parse the minimum and maximum boundaries, catching any exceptions.
                    try {
                        // Parse minimum boundary expression.
                        forStatement.minimumBoundary = Expression.parseExpression(forDefinition, false);

                        if (forDefinition.match(Token.TokenType.IDENTIFIER) && forDefinition.contentMatch("TO")) {
                            forDefinition.consume();
                            // Parse maximum boundary expression
                            forStatement.maximumBoundary = Expression.parseExpression(forDefinition, false);

                            if (!forDefinition.match(Token.TokenType.CLOSE_BRACKET)) {
                                Interpreter.printCompileError(forDefinition.line, Parser.class, "Expected ')', found '" + forDefinition.peek().contents + "'.");
                            } else {
                                forDefinition.consume();
                            }
                        } else {
                            Interpreter.printCompileError(forDefinition.line, Parser.class, "Expected 'To', found '" + forDefinition.peek().contents + "'.");
                        }

                    } catch (ExpressionParseException e) {
                        Interpreter.printCompileError(forDefinition.line, Parser.class, e.exceptionType.message.replace("%s", e.source));
                    }
                } else {
                    Interpreter.printCompileError(forDefinition.line, Parser.class, "Expected '=', found '" + forDefinition.peek().contents + "'.");
                }
            } else {
                Interpreter.printCompileError(forDefinition.line, Parser.class, "Expected variable name, found '" + forDefinition.peek().contents + "'.");
            }
        } else {
            Interpreter.printCompileError(forDefinition.line, Parser.class, "Expected '(', found '" + forDefinition.peek().contents + "'.");
        }

        if (!forDefinition.empty()) {
            Interpreter.printCompileError(forDefinition.line, Parser.class, "Expected end of line, found '" + forDefinition.peek().contents + "'.");
        }

        // For statement has been successfully parsed, move on to loop statements
        statusStack.push(ParserStatus.PARSING_FOR);
        currentLine++;

        Statement nextStatement = null;
        while (!(nextStatement instanceof NextStatement)) {
            nextStatement = parseTokenLine(currentLine);

            if (nextStatement == null) {
                Interpreter.printCompileError(forDefinition.line, Parser.class, "Could not find 'Next' statement.");
            } else {
                forStatement.statements.add(nextStatement);
            }
        }

        return forStatement;
    }

    private Statement nextStatement() {
        TokenString nextDefinition = lines.get(currentLine).createCopy();
        nextDefinition.removeWhitespace();
        if (!statusStack.empty()) {
            if (statusStack.pop() != ParserStatus.PARSING_FOR) {
                Interpreter.printCompileError(nextDefinition.line, Parser.class, "'Next' statement must follow a 'For' statement.");
            }
        }

        nextDefinition.consume();

        if (!nextDefinition.empty()) {
            Interpreter.printCompileError(nextDefinition.line, Parser.class, "Expected end of line, found '" + nextDefinition.peek().contents + "'.");
        }

        currentLine++;

        return new NextStatement();
    }

    /* -- WHILE STATEMENT -- */
    private Statement whileStatement() {
        TokenString whileDefinition = lines.get(currentLine).createCopy();
        WhileStatement whileStatement = new WhileStatement();

        whileDefinition.removeWhitespace();

        whileDefinition.consume();

        if (whileDefinition.match(Token.TokenType.OPEN_BRACKET)) {
            whileDefinition.consume();

            // Parse first condition
            whileStatement.conditions = ConditionSequence.parseConditionSequence(whileDefinition);

            if (whileStatement.conditions.firstCondition == null) {
                Interpreter.printCompileError(whileDefinition.line, Parser.class, "Expected Condition, found empty line.");
            }

            if (whileDefinition.match(Token.TokenType.CLOSE_BRACKET)) {
                whileDefinition.consume();
            } else {
                Interpreter.printCompileError(whileDefinition.line, Parser.class, "Expected ')', found '" + whileDefinition.peek().contents + "'.");
            }
        } else {
            Interpreter.printCompileError(whileDefinition.line, Parser.class, "Expected '(', found '" + whileDefinition.peek().contents + "'.");
        }

        if (!whileDefinition.empty()) {
            Interpreter.printCompileError(whileDefinition.line, Parser.class, "Expected end of line, found '" + whileDefinition.peek().contents + "'.");
        }

        statusStack.push(ParserStatus.PARSING_WHILE);
        currentLine++;

        Statement nextStatement = null;
        while (!(nextStatement instanceof WendStatement)) {
            nextStatement = parseTokenLine(currentLine);

            if (nextStatement == null) {
                Interpreter.printCompileError(whileDefinition.line, Parser.class, "Could not find 'Wend' statement.");
            } else {
                whileStatement.statements.add(nextStatement);
            }
        }

        return whileStatement;
    }

    private Statement wendStatement() {
        TokenString wendDefinition = lines.get(currentLine).createCopy();

        wendDefinition.removeWhitespace();

        wendDefinition.consume();

        if (!statusStack.empty()) {
            if (statusStack.pop() != ParserStatus.PARSING_WHILE) {
                Interpreter.printCompileError(wendDefinition.line, Parser.class, "'Wend' statement must follow a 'While' statement.");
            }
        }

        if (!wendDefinition.empty()) {
            Interpreter.printCompileError(wendDefinition.line, Parser.class, "Expected end of line, found '" + wendDefinition.peek().contents + "'.");
        }

        currentLine++;

        return new WendStatement();
    }

    /* -- REPEAT STATEMENT -- */
    private Statement repeatStatement() {
        TokenString repeatDefinition = lines.get(currentLine).createCopy();
        RepeatStatement repeatStatement = new RepeatStatement();

        repeatDefinition.removeWhitespace();

        repeatDefinition.consume();

        if (!repeatDefinition.empty()) {
            Interpreter.printCompileError(repeatDefinition.line, Parser.class, "Expected end of line, found '" + repeatDefinition.peek().contents + "'.");
        }

        statusStack.push(ParserStatus.PARSING_REPEAT);
        currentLine++;

        Statement nextStatement = null;
        while (!(nextStatement instanceof UntilStatement)) {
            nextStatement = parseTokenLine(currentLine);

            if (nextStatement == null) {
                Interpreter.printCompileError(repeatDefinition.line, Parser.class, "Could not find 'Until' statement.");
            } else if (nextStatement instanceof UntilStatement) {
                repeatStatement.untilStatement = (UntilStatement) nextStatement;
            } else {
                repeatStatement.statements.add(nextStatement);
            }
        }

        return repeatStatement;
    }

    private Statement untilStatement() {
        TokenString untilDefinition = lines.get(currentLine).createCopy();
        UntilStatement untilStatement = new UntilStatement();

        untilDefinition.removeWhitespace();

        untilDefinition.consume();

        if (!statusStack.empty()) {
            if (statusStack.pop() != ParserStatus.PARSING_REPEAT) {
                Interpreter.printCompileError(untilDefinition.line, Parser.class, "'Until' statement must follow a 'Repeat' statement.");
            }
        }

        if (untilDefinition.match(Token.TokenType.OPEN_BRACKET)) {
            untilDefinition.consume();

            // Parse first condition
            untilStatement.conditions = ConditionSequence.parseConditionSequence(untilDefinition);

            if (untilStatement.conditions.firstCondition == null) {
                Interpreter.printCompileError(untilDefinition.line, Parser.class, "Expected Condition, found empty line.");
            }

            if (untilDefinition.match(Token.TokenType.CLOSE_BRACKET)) {
                untilDefinition.consume();
            } else {
                Interpreter.printCompileError(untilDefinition.line, Parser.class, "Expcected ')', found '" + untilDefinition.peek().contents + "'.");
            }

        } else {
            Interpreter.printCompileError(untilDefinition.line, Parser.class, "Expected '(', found '" + untilDefinition.peek().contents + "'.");
        }

        if (!untilDefinition.empty()) {
            Interpreter.printCompileError(untilDefinition.line, Parser.class, "Expected end of line, found '" + untilDefinition.peek().contents + "'.");
        }

        currentLine ++;

        return untilStatement;
    }

    /* -- SWITCH STATEMENT -- */
    private Statement switchStatement() {
        TokenString switchDefinition = lines.get(currentLine).createCopy();
        SwitchStatement statement = new SwitchStatement();


        currentLine ++;

        return null;
    }

    private Statement caseStatement() {
        currentLine ++;
        return null;
    }

    private Statement endCaseStatement() {
        currentLine ++;
        return null;
    }

    private Statement defaultStatement() {
        currentLine ++;
        return null;
    }

    private Statement endSwitchStatement() {
        currentLine ++;
        return null;
    }

    /* -- OTHER -- */
    private Statement varStatement() {
        TokenString varDefinition = lines.get(currentLine).createCopy();
        return VariableDeclaration.parseVariableDeclaration(varDefinition);
    }

    private Statement arrayStatement() {
        TokenString arrayDefinition = lines.get(currentLine).createCopy();
        return ArrayDeclaration.parseArrayDeclaration(arrayDefinition);
    }

    private Statement setStatement() {
        TokenString setDefinition = lines.get(currentLine).createCopy();
        SetStatement statement = new SetStatement();

        setDefinition.removeWhitespace();

        if (setDefinition.match(Token.TokenType.IDENTIFIER, Token.TokenType.OPEN_SQUARE_BRACKET)) {
            // Parse array set statement
            statement.accessor = ArrayAccessor.parseArrayAccessor(setDefinition);
            statement.identifier = statement.accessor;

            if (setDefinition.match(Token.TokenType.EQUALS)) {
                setDefinition.consume();
                if (setDefinition.match(Token.TokenType.IDENTIFIER)) {
                    // Starts with identifier, assume function call, if not, check it's an expression
                    TokenString backupString = setDefinition.createCopy();
                    try {
                        statement.functionNewValue = CallStatement.parseCallStatement(setDefinition);
                    } catch (CallStatementParseException e) {
                        // Not a call statement, try to parse as expression
                        setDefinition = backupString;
                        try {
                            statement.expressionNewValue = Expression.parseExpression(setDefinition, false);
                        } catch (ExpressionParseException e1) {
                            Interpreter.printCompileError(setDefinition.line, Parser.class, "Invalid statement, expected expression or function call.");
                        }
                    }

                } else {
                    // Doesn't start with an identifier, assume expression
                    try {
                        statement.expressionNewValue = Expression.parseExpression(setDefinition, false);
                    } catch (ExpressionParseException e) {
                        // Isn't an expression and doesn't start with an identifier, therefore is not an expression or function call
                        Interpreter.printCompileError(setDefinition.line, Parser.class, "Invalid statement, expected expression or function call.");
                    }
                }

            } else {
                Interpreter.printCompileError(setDefinition.line, Parser.class, "Expected '=', found '" + setDefinition.peek().contents + "'.");
            }

            statement.arraySet = true;

        } else if (setDefinition.match(Token.TokenType.IDENTIFIER, Token.TokenType.EQUALS)) {
            // Parse variable set statement
            statement.identifier = setDefinition.consume();
            statement.arraySet = false;

            // Remove '='
            setDefinition.consume();

            if (setDefinition.match(Token.TokenType.IDENTIFIER)) {
                // Starts with identifier, assume function call, if not, check it's an expression
                TokenString backupString = setDefinition.createCopy();
                try {
                    statement.functionNewValue = CallStatement.parseCallStatement(setDefinition);
                } catch (CallStatementParseException e) {
                    // Not a call statement, try to parse as expression
                    setDefinition = backupString;
                    try {
                        statement.expressionNewValue = Expression.parseExpression(setDefinition, false);
                    } catch (ExpressionParseException e1) {
                        Interpreter.printCompileError(setDefinition.line, Parser.class, "Invalid statement, expected expression or function call.");
                    }
                }

            } else {
                // Doesn't start with an identifier, assume expression
                try {
                    statement.expressionNewValue = Expression.parseExpression(setDefinition, false);
                } catch (ExpressionParseException e) {
                    // Isn't an expression and doesn't start with an identifier, therefore is not an expression or function call
                    Interpreter.printCompileError(setDefinition.line, Parser.class, "Invalid statement, expected expression or function call.");
                }
            }

        } else {
            Interpreter.printCompileError(setDefinition.line, Parser.class, "Set statement must be used with either a variable, or array item.");
        }

        currentLine ++;

        return statement;
    }

    private Statement callStatement() {
        TokenString callDefinition = lines.get(currentLine).createCopy();

        try {
            return CallStatement.parseCallStatement(callDefinition);
        } catch (CallStatementParseException e) {
            Interpreter.printCompileError(callDefinition.line, Parser.class, e.exceptionType.message.replace("%s", e.cause));
        }
        return null;
    }

    private Statement returnStatement() {
        TokenString returnDefinition = lines.get(currentLine).createCopy();

        if (statusStack.contains(ParserStatus.PARSING_FUNCTION)) {
            currentLine ++;
            return ReturnStatement.parseReturnStatement(returnDefinition);
        } else {
            Interpreter.printCompileError(returnDefinition.line, Parser.class, "Return statement must be located within a Function.");
        }
        return null;
    }

    private Statement continueStatement() {
        TokenString continueDefinition = lines.get(currentLine).createCopy();
        continueDefinition.consume();

        if (!continueDefinition.empty()) {
            Interpreter.printCompileError(continueDefinition.line, Parser.class, "Expected End of Line, found '" + continueDefinition.peek().contents + "'.");
        }

        return new ContinueStatement();
    }

    private Statement breakStatement() {
        TokenString breakDefinition = lines.get(currentLine).createCopy();
        breakDefinition.consume();

        if (!breakDefinition.empty()) {
            Interpreter.printCompileError(breakDefinition.line, Parser.class, "Expected End of Line, found '" + breakDefinition.peek().contents + "'.");
        }

        return new BreakStatement();
    }

    public void setLines(List<TokenString> lines) {
        this.lines = lines;
    }

    public Token getFollowingLineIdentifier() {
        if (currentLine >= lines.size()) {
            System.out.println("Returned null.");
            return null;
        } else {
            TokenString followingLine = lines.get(currentLine).createCopy();
            Token followingLineToken = followingLine.trim().get(0);
            if (followingLineToken.type == Token.TokenType.IDENTIFIER)
                return followingLineToken;
            else
                return null;
        }
    }

    public Statement parseTokenLine(int line) {
        if (line >= lines.size()) {
            return null;
        }

        Statement statement = new Statement();

        // Remove leading whitespace
        TokenString string = lines.get(line);
        string.trimLeft();

        if (string.empty()) {
            currentLine++;
            return parseTokenLine(currentLine);
        }

        if (string.peek().type == Token.TokenType.IDENTIFIER) {
            switch (string.peek().contents.toUpperCase()) {
                case "FUNCTION":
                    statement = functionStatement();
                    break;
                case "ENDFUNCTION":
                    statement = endFunctionStatement();
                    break;
                case "RETURN":
                    statement = returnStatement();
                    break;
                case "IF":
                    statement = ifStatement();
                    break;
                case "ELSEIF":
                    statement = elseIfStatement();
                    break;
                case "ELSE":
                    statement = elseStatement();
                    break;
                case "ENDIF":
                    statement = endIfStatement();
                    break;
                case "FOR":
                    statement = forStatement();
                    break;
                case "NEXT":
                    statement = nextStatement();
                    break;
                case "WHILE":
                    statement = whileStatement();
                    break;
                case "WEND":
                    statement = wendStatement();
                    break;
                case "REPEAT":
                    statement = repeatStatement();
                    break;
                case "UNTIL":
                    statement = untilStatement();
                    break;
                case "SWITCH":
                    statement = switchStatement();
                    break;
                case "CASE":
                    statement = caseStatement();
                    break;
                case "ENDCASE":
                    statement = endCaseStatement();
                    break;
                case "DEFAULT":
                    statement = defaultStatement();
                case "ENDSWITCH":
                    statement = endSwitchStatement();
                    break;
                case "VAR":
                    statement = varStatement();
                    currentLine ++;
                    break;
                case "ARRAY":
                    statement = arrayStatement();
                    currentLine ++;
                    break;
                case "CONTINUE":
                    statement = continueStatement();
                    currentLine ++;
                    break;
                case "BREAK":
                    statement = breakStatement();
                    currentLine ++;
                    break;
                default:
                    // <Identifier> param1, param2, etc
                    // <Identifier> = <Expression>
                    // <Identifier>[<Expression>]
                    string.removeWhitespace();
                    if (string.match(Token.TokenType.IDENTIFIER, Token.TokenType.EQUALS) || string.match(Token.TokenType.IDENTIFIER, Token.TokenType.OPEN_SQUARE_BRACKET)) {
                        // Variable/Array statement
                        statement = setStatement();
                    } else if (string.match(Token.TokenType.IDENTIFIER)) {
                        // Function call
                        statement = callStatement();
                        currentLine ++;
                    } else {
                        Interpreter.printCompileError(string.line, Parser.class, "Invalid identifier found '" + string.peek().contents + "'.");
                    }
            }
        }

        return statement;
    }

    public List<Statement> performParse() {
        List<Statement> parsedStatements = new ArrayList<>();

        while (currentLine < lines.size()) {
            parsedStatements.add(parseTokenLine(currentLine));
        }

        return parsedStatements;
    }

}
