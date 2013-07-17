package com.sky.mattca.ccl.interpretation;

import com.sky.mattca.ccl.controlboard.K8055Library;
import com.sky.mattca.ccl.filesystem.FileHandler;
import com.sky.mattca.ccl.parser.CallStatement;
import com.sky.mattca.ccl.tokenizer.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Matt
 * Date: 24/04/13
 * Time: 19:52
 * To change this template use File | Settings | File Templates.
 */
public class InBuiltFunctions {

    private static boolean[] onList = new boolean[] {
            false, false, false, false, false, false, false, false
    };

    public static boolean isInBuilt(String name) {
        switch(name.toUpperCase()) {
            case "PRINT":case "INPUT":
            case "OPENFILE":case "CREATEFILE":
            case "WRITE":case "WRITELINE":
            case "READ":case "READLINE":
            case "CLOSEFILE":case "DELETEFILE":
            case "ENABLEBOARD":case "DISABLEBOARD":
            case "WRITEPORT":
            case "SETDIGITALON":case "SETDIGITALOFF":
            case "ISDIGITALON":case "ISINPUTON":
            case "READINPUTON":case "SLEEP":
                return true;
            default:
                return false;
        }
    }

    private static void updateBoardOutputs() {
        List<Integer> integerList = new ArrayList<>();
        for (int i = 0; i < onList.length; i++) {
            if (onList[i]) {
                integerList.add(i);
            }
        }

        int channelList[] = new int[integerList.size()];
        for (int i = 0; i < integerList.size(); i++) {
            channelList[i] = integerList.get(i);
        }

        if (channelList.length == 0) {
            K8055Library.clearDigitalOutput(0, 1, 2, 3, 4, 5, 6, 7);
        } else {
            K8055Library.enableDigitalOutputs(channelList);
        }
    }

    public static Token handleCall(CallStatement callStatement) {
        switch(callStatement.functionIdentifier.contents.toUpperCase()) {
            case "PRINT":
                if (callStatement.parameters.size() != 1) {
                    Interpreter.printRuntimeError(StatementInterpreter.class, "PRINT: Expected 1 parameters, found " + callStatement.parameters.size() + ".");
                }
                Token printValue = ExpressionEvaluator.evaluateExpression(callStatement.parameters.get(0));
                System.out.println(printValue.contents);
                return null;
            case "INPUT":
                if (callStatement.parameters.size() !=1) {
                    Interpreter.printRuntimeError(StatementInterpreter.class, "INPUT: Expected 1 parameters, found " + callStatement.parameters.size() + ".");
                }
                Token inputValue = ExpressionEvaluator.evaluateExpression(callStatement.parameters.get(0));
                System.out.println(inputValue.contents);

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    String result = reader.readLine();

                    Token resultToken = new Token(-1, result, Token.TokenType.STRING);
                    return resultToken;
                } catch (IOException e) {
                    Interpreter.printRuntimeError(InBuiltFunctions.class, "Could not read input.");
                }
                break;
            case "SLEEP":
                if (callStatement.parameters.size() != 1) {
                    Interpreter.printRuntimeError(StatementInterpreter.class, "SLEEP: Expected 1 parameters, found " + callStatement.parameters.size() + ".");
                }

                int waitTime = (int)Float.parseFloat(ExpressionEvaluator.evaluateExpression(callStatement.parameters.get(0)).contents);
                try {
                    Thread.sleep(waitTime);
                } catch (Exception e) { }
                break;
            case "OPENFILE":
                if (callStatement.parameters.size() != 1) {
                    Interpreter.printRuntimeError(StatementInterpreter.class, "OPENFILE: Expected 1 parameters, found " + callStatement.parameters.size() + ".");
                }
                int openHandle = Interpreter.fileHandler.openFile(ExpressionEvaluator.evaluateExpression(callStatement.parameters.get(0)).contents);
                if (openHandle == -1) {
                    Token returnToken = new Token(0, "-1", Token.TokenType.NUMBER);
                    returnToken.isReturn = true;
                    return returnToken;
                } else {
                    Token returnToken = new Token(0, String.valueOf(openHandle), Token.TokenType.NUMBER);
                    returnToken.isReturn = true;
                    return returnToken;
                }
            case "CREATEFILE":
                if (callStatement.parameters.size() != 1) {
                    Interpreter.printRuntimeError(StatementInterpreter.class, "CREATEFILE: Expected 1 parameters, found " + callStatement.parameters.size() + ".");
                }
                int createHandle = Interpreter.fileHandler.createFile(ExpressionEvaluator.evaluateExpression(callStatement.parameters.get(0)).contents);
                if (createHandle == -1) {
                    Token returnToken = new Token(0, "-1", Token.TokenType.NUMBER);
                    returnToken.isReturn = true;
                    return returnToken;
                } else {
                    Token returnToken = new Token(0, String.valueOf(createHandle), Token.TokenType.NUMBER);
                    returnToken.isReturn = true;
                    return returnToken;
                }
            case "WRITE":
                if (callStatement.parameters.size() != 2) {
                    Interpreter.printRuntimeError(StatementInterpreter.class, "WRITE: Expected 2 parameters, found " + callStatement.parameters.size() + ".");
                }
                int writeHandle = (int)Float.parseFloat(ExpressionEvaluator.evaluateExpression(callStatement.parameters.get(0)).contents);
                String data = ExpressionEvaluator.evaluateExpression(callStatement.parameters.get(1)).contents;
                Interpreter.fileHandler.writeToFile(writeHandle, data);
                break;
            case "WRITELINE":
                if (callStatement.parameters.size() != 2) {
                    Interpreter.printRuntimeError(StatementInterpreter.class, "WRITE: Expected 2 parameters, found " + callStatement.parameters.size() + ".");
                }
                int writeLineHandle = (int)Float.parseFloat(ExpressionEvaluator.evaluateExpression(callStatement.parameters.get(0)).contents);
                String lineData = ExpressionEvaluator.evaluateExpression(callStatement.parameters.get(1)).contents;
                Interpreter.fileHandler.writeLineToFile(writeLineHandle, lineData);
                break;
            case "READ":
                if (callStatement.parameters.size() != 1) {
                    Interpreter.printRuntimeError(StatementInterpreter.class, "READ: Expected 1 parameters, found " + callStatement.parameters.size() + ".");
                }
                int readHandle = (int)Float.parseFloat(ExpressionEvaluator.evaluateExpression(callStatement.parameters.get(0)).contents);
                Token readData = new Token(0, Interpreter.fileHandler.readFromFile(readHandle), Token.TokenType.STRING);
                readData.isReturn = true;
                return readData;
            case "READLINE":
                if (callStatement.parameters.size() != 1) {
                    Interpreter.printRuntimeError(StatementInterpreter.class, "READLINE: Expected 1 parameters, found " + callStatement.parameters.size() + ".");
                }
                int readLineHandle = (int)Float.parseFloat(ExpressionEvaluator.evaluateExpression(callStatement.parameters.get(0)).contents);
                Token readLineData = new Token(0, Interpreter.fileHandler.readLineFromFile(readLineHandle), Token.TokenType.STRING);
                readLineData.isReturn = true;
                return readLineData;
            case "CLOSEFILE":
                if (callStatement.parameters.size() != 1) {
                    Interpreter.printRuntimeError(StatementInterpreter.class, "CLOSEFILE: Expected 1 parameters, found " + callStatement.parameters.size() + ".");
                }
                int closeHandle = (int)Float.parseFloat(ExpressionEvaluator.evaluateExpression(callStatement.parameters.get(0)).contents);
                Interpreter.fileHandler.closeFile(closeHandle);
                break;
            case "DELETEFILE":
                if (callStatement.parameters.size() != 1) {
                    Interpreter.printRuntimeError(StatementInterpreter.class, "DELETEFILE: Expected 1 parameters, found " + callStatement.parameters.size() + ".");
                }
                String deleteFileName = ExpressionEvaluator.evaluateExpression(callStatement.parameters.get(0)).contents;
                Token deleteReturn = new Token(0, String.valueOf(Interpreter.fileHandler.deleteFile(deleteFileName)), Token.TokenType.BOOLEAN);
                deleteReturn.isReturn = true;
                return deleteReturn;
            case "ENABLEBOARD":
                try {
                    K8055Library.loadLibraryInterface();
                    Token enableBoardReturn = new Token(0, String.valueOf(K8055Library.enableBoard()), Token.TokenType.BOOLEAN);
                    enableBoardReturn.isReturn = true;
                    return enableBoardReturn;
                } catch (Exception e) {
                    Token falseReturn = new Token(0, "FALSE", Token.TokenType.BOOLEAN);
                    falseReturn.isReturn = true;
                    return falseReturn;
                }
            case "DISABLEBOARD":
                K8055Library.disableBoard();
                break;
            case "WRITEPORT":
                if (callStatement.parameters.size() != 1) {
                    Interpreter.printRuntimeError(StatementInterpreter.class, "WRITEPORT: Expected 1 parameters, found " + callStatement.parameters.size() + ".");
                }
                int port = (int)Float.parseFloat(ExpressionEvaluator.evaluateExpression(callStatement.parameters.get(0)).contents);
                K8055Library.getLibraryInterface().WriteAllDigital(port);
                break;
            case "SETDIGITALON":
                if (callStatement.parameters.size() != 1) {
                    Interpreter.printRuntimeError(StatementInterpreter.class, "SETDIGITALON: Expected 1 parameters, found " + callStatement.parameters.size() + ".");
                }
                int setOnChannel = (int)Float.parseFloat(ExpressionEvaluator.evaluateExpression(callStatement.parameters.get(0)).contents);
                if (setOnChannel == -1) {
                    for (int i = 0; i < 8; i++) {
                        onList[i] = true;
                    }
                } else if (setOnChannel < 0 || setOnChannel > 7) {
                    Interpreter.printRuntimeError(StatementInterpreter.class, "Expected value between 0 and 7.");
                } else {
                    onList[setOnChannel] = true;
                }
                updateBoardOutputs();
                break;
            case "SETDIGITALOFF":
                if (callStatement.parameters.size() != 1) {
                    Interpreter.printRuntimeError(StatementInterpreter.class, "SETDIGITALOFF: Expected 1 parameters, found " + callStatement.parameters.size() + ".");
                }
                int setOffChannel = (int)Float.parseFloat(ExpressionEvaluator.evaluateExpression(callStatement.parameters.get(0)).contents);
                if (setOffChannel == -1) {
                    for (int i = 0; i < 8; i++) {
                        onList[i] = false;
                    }
                } else if (setOffChannel < 0 || setOffChannel > 7) {
                    Interpreter.printRuntimeError(StatementInterpreter.class, "Expected value between 0 and 7.");
                } else {
                    onList[setOffChannel] = false;
                }
                updateBoardOutputs();
                break;
            case "ISDIGITALON":
                if (callStatement.parameters.size() != 1) {
                    Interpreter.printRuntimeError(StatementInterpreter.class, "ISDIGITALON: Expected 1 parameters, found " + callStatement.parameters.size() + ".");
                }
                int isDigitalOnChannel = (int)Float.parseFloat(ExpressionEvaluator.evaluateExpression(callStatement.parameters.get(0)).contents);
                if (isDigitalOnChannel < 0 || isDigitalOnChannel > 7) {
                    Interpreter.printRuntimeError(StatementInterpreter.class, "Expected value between 0 and 7.");
                }

                Token digitalOnReturnToken = new Token(0, String.valueOf(onList[isDigitalOnChannel]), Token.TokenType.BOOLEAN);
                digitalOnReturnToken.isReturn = true;
                return digitalOnReturnToken;
            case "ISINPUTON":
                if (callStatement.parameters.size() != 1) {
                    Interpreter.printRuntimeError(StatementInterpreter.class, "ISINPUTON: Expected 1 parameters, found " + callStatement.parameters.size() + ".");
                }
                int isInputOnChannel = (int)Float.parseFloat(ExpressionEvaluator.evaluateExpression(callStatement.parameters.get(0)).contents);
                if (isInputOnChannel < 0 || isInputOnChannel > 7) {
                    Interpreter.printRuntimeError(StatementInterpreter.class, "Expected value between 1 and 5.");
                }
                if (K8055Library.getLibraryInterface().ReadDigitalChannel(isInputOnChannel)) {
                    Token returnToken = new Token(0, "TRUE", Token.TokenType.BOOLEAN);
                    returnToken.isReturn = true;
                    return returnToken;
                } else {
                    Token returnToken = new Token(0, "FALSE", Token.TokenType.BOOLEAN);
                    returnToken.isReturn = true;
                    return returnToken;
                }
            case "READINPUTON":
                if (callStatement.parameters.size() != 1) {
                    Interpreter.printRuntimeError(StatementInterpreter.class, "READINPUTON: Expected 1 parameters, found " + callStatement.parameters.size() + ".");
                }
                int readInputOnChannel = (int)Float.parseFloat(ExpressionEvaluator.evaluateExpression(callStatement.parameters.get(0)).contents);
                if (readInputOnChannel != -1 && readInputOnChannel < 0 || readInputOnChannel > 7) {
                    Interpreter.printRuntimeError(StatementInterpreter.class, "Expected value between 1 and 5.");
                }

                boolean inputPressed = false;
                while (!inputPressed) {
                    if (readInputOnChannel == -1) {
                        for (int i = 0; i <= 5; i++) {
                            if (inputPressed == false) inputPressed = K8055Library.getLibraryInterface().ReadDigitalChannel(i);
                        }
                    } else {
                        inputPressed = K8055Library.getLibraryInterface().ReadDigitalChannel(readInputOnChannel);
                    }
                }
                break;
        }
        return null;
    }

}
