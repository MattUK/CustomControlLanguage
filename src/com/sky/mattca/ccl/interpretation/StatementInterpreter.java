package com.sky.mattca.ccl.interpretation;

import com.sky.mattca.ccl.parser.*;
import com.sky.mattca.ccl.tokenizer.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * User: 06mcarter
 * Date: 28/03/13
 * Time: 10:21
 */
public class StatementInterpreter {

    private List<Statement> statements;
    private StatementInterpreter parent;

    private List<Interpreter.VariableData> variableList;

    public StatementInterpreter(List<Statement> parsedStatements) {
        this.statements = parsedStatements;
        variableList = new ArrayList<>();
        parent = null;
    }

    public StatementInterpreter(List<Statement> parsedStatements, List<Interpreter.VariableData> variables, StatementInterpreter parent) {
        this.statements = parsedStatements;
        this.parent = parent;
        variableList = new ArrayList<>();

        if (variables != null) {
            variableList.addAll(variables);
        }
    }

    public StatementInterpreter() {
        variableList = new ArrayList<>();
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    private void addVariable(Interpreter.VariableData variable) {
        variableList.add(variable);
    }

    /**
     * Returns true if the variable exists within any of the interpreters.
     * @param variableName The name to check for.
     * @return True if the variable exists, false otherwise.
     */
    public boolean variableExists(String variableName) {
        // Check this objects list
        for (Interpreter.VariableData v : variableList) {
            if (v.variableName.equalsIgnoreCase(variableName)) {
                return true;
            }
        }
        // Check this objects parent list
        if (parent != null) {
            return parent.variableExists(variableName);
        }
        // Check if global interpreter has variable
        return Interpreter.variableExists(variableName);
    }

    /**
     * Returns a variable that can be accessed by the current interpreter.
     * @param name The name to return.
     * @return Null if the variable cannot be found, otherwise the VariableData that represents it.
     */
    public Interpreter.VariableData getVariableData(String name) {
        if (variableExists(name)) {
            for (Interpreter.VariableData var : variableList) {
                if (var.variableName.equalsIgnoreCase(name)) {
                    return var;
                }
            }
            if (parent != null) {
                return parent.getVariableData(name);
            }
            return Interpreter.getVariableData(name);
        }
        return null;
    }

    /**
     * Converts a token type into a variable data type, reporting an error if no match can be found
     * @param token The token that needs to be converted
     * @return The variable data type that matches the token
     */
    public Interpreter.VariableType getVariableTypeFromToken(Token token) {
        if (token.type == Token.TokenType.STRING) {
            return Interpreter.VariableType.STRING;
        } else if (token.type == Token.TokenType.NUMBER) {
            return Interpreter.VariableType.NUMBER;
        } else if (token.type == Token.TokenType.BOOLEAN) {
            return Interpreter.VariableType.BOOLEAN;
        } else {
            Interpreter.printRuntimeError(StatementInterpreter.class, "Invalid type: " + token.contents + ".");
            return null;
        }
    }

    public Token execute() {
        for (Statement statement : statements) {
            Token result = null;
            if (statement instanceof CallStatement) {
                result = interpretCallStatement((CallStatement)statement);
            } else if (statement instanceof IfStatement) {
                result = interpretIfStatement((IfStatement)statement);
            } else if (statement instanceof ForStatement) {
                result = interpretForStatement((ForStatement)statement);
            } else if (statement instanceof WhileStatement) {
                result = interpretWhileStatement((WhileStatement)statement);
            } else if (statement instanceof RepeatStatement) {
                result = interpretRepeatStatement((RepeatStatement)statement);
            } else if (statement instanceof VariableDeclaration) {
                result = interpretVariableDefinition((VariableDeclaration)statement);
            } else if (statement instanceof ArrayDeclaration) {
                result = interpretArrayDefinition((ArrayDeclaration)statement);
            } else if (statement instanceof SetStatement) {
                result = interpretSetStatement((SetStatement)statement);
            } else if (statement instanceof ReturnStatement) {
                return interpretReturnStatement((ReturnStatement)statement);
            } else if (statement instanceof ContinueStatement) {
                return interpretContinueStatement((ContinueStatement)statement);
            } else if (statement instanceof BreakStatement) {
                return interpretBreakStatement((BreakStatement)statement);
            } else if (statement instanceof FunctionStatement) {
                continue;
            }
            if (result != null && parent != null && result.isReturn) {
                return result;
            }
        }
        return null;
    }

    /**
     * Interprets Call Statements
     * @param callStatement The statement to be interpreted.
     * @return The result of the function call (given by return statement).
     */
    private Token interpretCallStatement(CallStatement callStatement) {
        // Assign this instance of the interpreter to the expression evaluator.
        ExpressionEvaluator.currentInterpreter = this;
        // Fetch the function data from the Interpreter.
        Interpreter.FunctionData function = Interpreter.getFunctionData(callStatement.functionIdentifier.contents);
        // Make sure the function being called exists
        if (function == null) {
            // If the function does not exist, check if it is an in-built function, if so execute it
            if (InBuiltFunctions.isInBuilt(callStatement.functionIdentifier.contents)) {
                return InBuiltFunctions.handleCall(callStatement);
            } else {
                // Otherwise report an error
                Interpreter.printRuntimeError(StatementInterpreter.class, "Function '" + callStatement.functionIdentifier.contents + "' does not exist.");
            }
        }
        // Get the required parameter count from the function data.
        int requiredParameterCount = function.statement.parameters.size();
        // If the count provided is different, the call is incorrect and does not provide the required number of parameters.
        if (callStatement.parameters.size() != requiredParameterCount) {
            Interpreter.printRuntimeError(StatementInterpreter.class, function.functionName + ": Expected " + requiredParameterCount + " parameters, found " + callStatement.parameters.size() + ".");
        } else {
            // Define a temporary storage for variables to be passed to the Function Interpreter as parameters
            List<Interpreter.VariableData> inputVariables = new ArrayList<>();
            // Loop through the parameters, checking them and adding them to the variable storage.
            for (int i = 0; i < function.statement.parameters.size(); i++) {
                // Fetch the current parameter
                Token parameter = function.statement.parameters.get(i);
                // Create a new Variable to store the content of the parameter passed by the user
                Token variableValue = ExpressionEvaluator.evaluateExpression(callStatement.parameters.get(i));
                Interpreter.VariableData newVariable = new Interpreter.VariableData(parameter.contents, variableValue.contents, getVariableTypeFromToken(variableValue));
                // Add the new variable to the list
                inputVariables.add(newVariable);
            }
            // Create a new interpreter instance that will execute the statements within this function
            StatementInterpreter functionInterpreter = new StatementInterpreter(function.statement.contents, inputVariables, this);
            // Execute the interpreter, if a return statement is located, it will be stored within the result
            return functionInterpreter.execute();
        }
        return null;
    }

    /**
     * Interprets an If statement.
     * @param ifStatement The If statement to execute.
     * @return The return value (if any) from the contents of the structure.
     */
    private Token interpretIfStatement(IfStatement ifStatement) {
        ExpressionEvaluator.currentInterpreter = this;
        // Evaluate first if statement condition
        boolean executeMainStructure = ConditionalEvaluator.evaluateConditionSequence(ifStatement.conditions);
        // If the condition was true, execute the contents of the if statement
        if (executeMainStructure) {
            StatementInterpreter ifInterpreter = new StatementInterpreter(ifStatement.contents, null, this);
            return ifInterpreter.execute();
        } else {
            // Loop through all the else if statements, if none are executed, execute the else statement if it exists
            for (int i = 0; i < ifStatement.elseIfStatements.size(); i++) {
                boolean executeElseIf = ConditionalEvaluator.evaluateConditionSequence(ifStatement.elseIfStatements.get(i).conditions);
                if (executeElseIf) {
                    StatementInterpreter elseIfInterpreter = new StatementInterpreter(ifStatement.elseIfStatements.get(i).contents, null, this);
                    return elseIfInterpreter.execute();
                }
            }
            // Check if the else statement exists
            if (ifStatement.elseStatement != null) {
                StatementInterpreter elseInterpreter = new StatementInterpreter(ifStatement.elseStatement.contents, null, this);
                return elseInterpreter.execute();
            }
        }
        return null;
    }

    /**
     * Interprets a For Loop statement
     * @param forStatement The For statement to be executed.
     * @return The return value (if any) of the contents of the loop.
     */
    private Token interpretForStatement(ForStatement forStatement) {
        ExpressionEvaluator.currentInterpreter = this;
        // Fetch counter, minimum, and maximum values.
        String counterName = forStatement.variable.contents;
        Token minimumValue = ExpressionEvaluator.evaluateExpression(forStatement.minimumBoundary);
        Token maximumValue = ExpressionEvaluator.evaluateExpression(forStatement.maximumBoundary);
        // Make sure the minimum and maximum boundaries are numbers.
        if (minimumValue.type != Token.TokenType.NUMBER || maximumValue.type != Token.TokenType.NUMBER) {
            Interpreter.printRuntimeError(StatementInterpreter.class, "For loop Minimum and Maximum boundaries must be a number, found '" + minimumValue.contents + "', '" + maximumValue + "'.");
        }
        // Check if the counter variable already exists, report an error if it does.
        if (variableExists(counterName)) {
            Interpreter.printRuntimeError(StatementInterpreter.class, "Variable '" + counterName + "' already exists.");
        }
        // Create a VariableData object to store the counter whilst the loop is being executed.
        Interpreter.VariableData counterVariable = new Interpreter.VariableData(counterName, "0", Interpreter.VariableType.NUMBER);
        List<Interpreter.VariableData> counterList = new ArrayList<>();
        // Convert the minimum and maximum boundaries into usable numbers.
        int minVal = (int)Float.parseFloat(minimumValue.contents);
        int maxVal = (int)Float.parseFloat(maximumValue.contents);

        if (minVal < maxVal) {
            // If the minimum value is less than the maximum, go from low to high
            for (int counter = minVal; counter < maxVal; counter ++) {
                // Update the counter variable and add it to the list
                counterVariable.value = String.valueOf(counter);
                counterList.clear();
                counterList.add(counterVariable);
                // Create a new interpreter instance to execute the contents of the loop, passing the counter at the same time
                StatementInterpreter interpreter = new StatementInterpreter(forStatement.statements, counterList, this);
                // Obtain the result of the statements execution
                Token result = interpreter.execute();
                // Check if the result is significant to the loop
                if (result != null && result.isBreak) {
                    // User wants the loop to break, break the loop.
                    return null;
                } else if (result != null && result.isContinue) {
                    // User wants to skip to the next loop
                    continue;
                } else if (result != null && result.isReturn) {
                    // A return statement has been executed, return the value
                    return result;
                }
            }
        } else if (minVal > maxVal) {
            // If the lower boundary is higher than the max, go from high to low
            for (int counter = minVal; counter >= maxVal; counter --) {
                counterVariable.value = String.valueOf(counter);
                counterList.clear();
                counterList.add(counterVariable);

                StatementInterpreter interpreter = new StatementInterpreter(forStatement.statements, counterList, this);
                Token result = interpreter.execute();

                if (result != null && result.isContinue) {
                    continue;
                } else if (result != null && result.isBreak) {
                    return null;
                } else if (result != null && result.isReturn) {
                    return result;
                }
            }
        } else if (minVal == maxVal) {
            // If the two boundaries are equal, execute the loop once
            counterVariable.value = String.valueOf(0);
            counterList.clear();
            counterList.add(counterVariable);

            StatementInterpreter interpreter = new StatementInterpreter(forStatement.statements, counterList, this);
            Token result = interpreter.execute();

            if (result != null && (result.isBreak || result.isContinue)) {
                return null;
            } else if (result != null && result.isReturn) {
                return result;
            }
        }
        return null;
    }

    /**
     * Interprets a While Loop statement
     * @param whileStatement The While loop to be executed.
     * @return The return value (if any) of the contents of the loop.
     */
    private Token interpretWhileStatement(WhileStatement whileStatement) {
        ExpressionEvaluator.currentInterpreter = this;
        // Check if the condition is valid for the statement to be executed
        boolean conditionResult = ConditionalEvaluator.evaluateConditionSequence(whileStatement.conditions);
        // Execute the loop whilst the condition result is valid
        while(conditionResult) {
            // Create an interpreter instance to execute the contents of the while loop
            StatementInterpreter interpreter = new StatementInterpreter(whileStatement.statements, null, this);
            Token result = interpreter.execute();
            // Act on the result of the loop
            if (result != null && result.isContinue) {
                continue;
            } else if (result != null && result.isBreak) {
                return null;
            } else if (result != null && result.isReturn) {
                return result;
            }
            // Reset the evaluator and re-check the condition for the next loop
            ExpressionEvaluator.currentInterpreter = this;
            conditionResult = ConditionalEvaluator.evaluateConditionSequence(whileStatement.conditions);
        }
        return null;
    }

    /**
     * Interprets a Repeat Loop statement
     * @param repeatStatement The Repeat Statement to be executed.
     * @return The return value (if any) of the contents of the loop.
     */
    private Token interpretRepeatStatement(RepeatStatement repeatStatement) {
        // Works in a similar fashion to the while loop function, but differs in that the loop will ALWAYS execute once.
        ExpressionEvaluator.currentInterpreter = this;
        boolean conditionResult = true;
        do {
            StatementInterpreter interpreter = new StatementInterpreter(repeatStatement.statements, null, this);
            Token result = interpreter.execute();

            if (result != null && result.isContinue) {
                continue;
            } else if (result != null && result.isBreak) {
                return null;
            } else if (result != null && result.isReturn) {
                return result;
            }

            ExpressionEvaluator.currentInterpreter = this;
            conditionResult = ConditionalEvaluator.evaluateConditionSequence(repeatStatement.untilStatement.conditions);
        } while(!conditionResult);
        return null;
    }

    /**
     * Interprets a Variable Declaration statement.
     * @param declaration The declaration to be executed.
     * @return Null.
     */
    private Token interpretVariableDefinition(VariableDeclaration declaration) {
        ExpressionEvaluator.currentInterpreter = this;
        // Fetch the variable name
        String variableName = declaration.variableIdentifier.contents;
        // Make sure the variable is not being defined twice
        if (variableExists(variableName)) {
            Interpreter.printRuntimeError(StatementInterpreter.class, "Variable '" + variableName + "' already exists.");
        }
        // Check if the variable is assigned to an expression or a function call
        if (declaration.expression != null) {
            // Assign the result of the expression to the variable and add it to this interpreters variable table
            Token expressionResult = ExpressionEvaluator.evaluateExpression(declaration.expression);
            Interpreter.VariableData newVariable = new Interpreter.VariableData(variableName, expressionResult.contents, getVariableTypeFromToken(expressionResult));
            addVariable(newVariable);
            return null;
        } else if (declaration.functionCall != null) {
            // Assign the result of the function call to the variable and add it to this interpreters variable table
            Token callResult = interpretCallStatement(declaration.functionCall);
            if (callResult == null) {
                Interpreter.printRuntimeError(StatementInterpreter.class, "'" + declaration.functionCall.functionIdentifier.contents + "' does not return a value.");
            }
            Interpreter.VariableData newVariable = new Interpreter.VariableData(variableName, callResult.contents, getVariableTypeFromToken(callResult));
            addVariable(newVariable);
            return null;
        }

        return null;
    }

    public boolean isValidArrayIndex(float value) {
        if (value < 0 || value % 1 != 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Interprets an Array Declaration statement.
     * @param declaration The declaration to be executed.
     * @return Null.
     */
    private Token interpretArrayDefinition(ArrayDeclaration declaration) {
        ExpressionEvaluator.currentInterpreter = this;
        // Fetch the array name
        String arrayName = declaration.arrayIdentifier.contents;
        // Make sure the array is not defined twice
        if (variableExists(arrayName)) {
            Interpreter.printRuntimeError(StatementInterpreter.class, "Array '" + arrayName + "' already exists.");
        }
        // Fetch the new size of the array
        Token arraySizeToken = ExpressionEvaluator.evaluateExpression(declaration.size);
        // Make sure the size is a number
        if (arraySizeToken.type != Token.TokenType.NUMBER) {
            Interpreter.printRuntimeError(StatementInterpreter.class, "Expected a number for array size, found '" + arraySizeToken.contents + "'.");
        }

        // Convert size to integer
        float arraySize = Float.parseFloat(arraySizeToken.contents);
        // Make sure the size is not negative and is a whole number
        if (!isValidArrayIndex(arraySize)) {
            Interpreter.printRuntimeError(StatementInterpreter.class, "Expected a positive, whole number for array size, found '" + arraySize + "'.");
        }

        // Store the array as a variable inside the variable table
        Interpreter.VariableData newArray = new Interpreter.VariableData(arrayName, (int)arraySize, new Interpreter.VariableData[(int)arraySize]);
        addVariable(newArray);
        return null;
    }

    /**
     * Interprets a Set Statement.
     * @param setStatement The set statement to execute.
     * @return Null.
     */
    private Token interpretSetStatement(SetStatement setStatement) {
        ExpressionEvaluator.currentInterpreter = this;
        // Fetch the name of the array/variable being set
        String objectName = setStatement.identifier.contents;
        // Check if the variable exists
        if (!variableExists(objectName)) {
            Interpreter.printRuntimeError(StatementInterpreter.class, "Variable/Array '" + objectName + "' does not exist.");
        }
        // Check whether this set statement acts upon an array or a variable
        if (setStatement.arraySet) {
            Token itemIndex = ExpressionEvaluator.evaluateExpression(setStatement.accessor.accessorExpression);
            if (itemIndex.type != Token.TokenType.NUMBER) {
                Interpreter.printRuntimeError(StatementInterpreter.class, "Expected a number for array item index, found '" + itemIndex.contents + "'.");
            }
            // Run checks on the array index to make sure it is valid
            float arrayIndex = Float.parseFloat(itemIndex.contents);
            if (!isValidArrayIndex(arrayIndex)) {
                Interpreter.printRuntimeError(StatementInterpreter.class, "Expected a positive, whole number for array index, found '" + arrayIndex + "'.");
            }
            // Fetch the object referenced in the statement
            Interpreter.VariableData arrayData = getVariableData(objectName);
            // Make sure the object is an array
            if (!arrayData.isArray()) {
                Interpreter.printRuntimeError(StatementInterpreter.class, "'" + objectName + "' is not an array.");
            }
            // Make sure the array index is within the bounds of the array
            if (arrayIndex > arrayData.data.length) {
                Interpreter.printRuntimeError(StatementInterpreter.class, "Array index is out of bounds.");
            }
            // Assign the array with a value depending upon whether or not it is an expression or function call
            if (setStatement.expressionNewValue != null) {
                Token expressionResult = ExpressionEvaluator.evaluateExpression(setStatement.expressionNewValue);
                arrayData.data[(int)arrayIndex] = new Interpreter.VariableData("", expressionResult.contents, getVariableTypeFromToken(expressionResult));
                return null;
            } else {
                Token functionResult = interpretCallStatement(setStatement.functionNewValue);
                if (functionResult == null) {
                    Interpreter.printRuntimeError(StatementInterpreter.class, "'" + setStatement.functionNewValue.functionIdentifier.contents + "' does not return a value.");
                }
                arrayData.data[(int)arrayIndex] = new Interpreter.VariableData("", functionResult.contents, getVariableTypeFromToken(functionResult));
                return null;
            }
        } else {
            Interpreter.VariableData variableData = getVariableData(objectName);

            if (variableData.isArray()) {
                Interpreter.printRuntimeError(StatementInterpreter.class, "'" + objectName + "' is not a variable.");
            }

            if (setStatement.expressionNewValue != null) {
                Token expressionResult = ExpressionEvaluator.evaluateExpression(setStatement.expressionNewValue);
                variableData.value = expressionResult.contents;
                variableData.type = getVariableTypeFromToken(expressionResult);
                return null;
            } else {
                Token functionResult = interpretCallStatement(setStatement.functionNewValue);
                if (functionResult == null) {
                    Interpreter.printRuntimeError(StatementInterpreter.class, "'" + setStatement.functionNewValue.functionIdentifier.contents + "' does not return a value.");
                }
                variableData.value = functionResult.contents;
                variableData.type = getVariableTypeFromToken(functionResult);
                return null;
            }
        }
    }

    /**
     * Interprets a Return statement.
     * @param returnStatement The return statement to execute.
     * @return The value passed with the return statement.
     */
    private Token interpretReturnStatement(ReturnStatement returnStatement) {
        ExpressionEvaluator.currentInterpreter = this;
        // Depending upon whether the statement was given an expression or function call, return the result
        if (returnStatement.expression != null) {
            Token expressionResult = ExpressionEvaluator.evaluateExpression(returnStatement.expression);
            expressionResult.isReturn = true;
            return expressionResult;
        } else {
            Token callResult = interpretCallStatement(returnStatement.callStatement);
            callResult.isReturn = true;
            return callResult;
        }
    }

    /**
     * Interprets a continue statement.
     * @param continueStatement The continue statement to execute.
     * @return A token representing a continue message.
     */
    private Token interpretContinueStatement(ContinueStatement continueStatement) {
        Token result = new Token();
        result.isContinue = true;
        return result;
    }

    /**
     * Interprets a break statement.
     * @param breakStatement The break statement to execute.
     * @return A token representing a break message.
     */
    private Token interpretBreakStatement(BreakStatement breakStatement) {
        Token result = new Token();
        result.isBreak = true;
        return result;
    }

}