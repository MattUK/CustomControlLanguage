package com.sky.mattca.ccl.interpretation;

import com.sky.mattca.ccl.filesystem.FileHandler;
import com.sky.mattca.ccl.parser.FunctionStatement;
import com.sky.mattca.ccl.parser.Parser;
import com.sky.mattca.ccl.parser.Statement;
import com.sky.mattca.ccl.tokenizer.Token;
import com.sky.mattca.ccl.tokenizer.TokenString;
import com.sky.mattca.ccl.tokenizer.Tokenizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * User: 06mcarter
 * Date: 11/10/12
 * Time: 11:03
 */
public class Interpreter implements Runnable {

    public static class FunctionData {
        public String functionName;
        public FunctionStatement statement;

        public FunctionData(String name, FunctionStatement statement) {
            functionName = name;
            this.statement = statement;
        }
    }

    public enum VariableType {
        NUMBER,
        BOOLEAN,
        STRING
    }

    public static class VariableData {
        public String variableName;
        public String value;
        public VariableType type;

        public VariableData[] data;
        public int arraySize;

        public boolean array;

        public VariableData(String name) {
            variableName = name;
        }

        public VariableData(String name, String value, VariableType type) {
            variableName = name;
            this.value = value;
            this.type = type;
            array = false;
        }

        public VariableData(String name, int size, VariableData[] data) {
            variableName = name;
            this.data = data;
            this.arraySize = size;
            array = true;
        }

        public boolean isArray() {
            return array;
        }

        public Token variableAsToken() {
            Token token = new Token();
            if (type == VariableType.STRING) {
                token.type = Token.TokenType.STRING;
            } else if (type == VariableType.NUMBER) {
                token.type = Token.TokenType.NUMBER;
            } else if (type == VariableType.BOOLEAN) {
                token.type = Token.TokenType.BOOLEAN;
            }
            token.contents = value;
            token.line = -1;
            return token;
        }

        public Token arrayAsToken(int index) {
            if (index > arraySize || index < 0) {
                return null;
            }
            return data[index].variableAsToken();
        }

    }

    // The Tokenizer used within the interpreter.
    public Tokenizer tokenizer;
    // The Parser used within the interpreter.
    public Parser parser;
    // The core of the interpreter, used for statement execution.
    public StatementInterpreter interpreter;

    // In-Built File Handler
    public static FileHandler fileHandler = new FileHandler();

    // The user-provided source code to be executed.
    private List<String> sourceCode = new ArrayList<>();
    // The tokenized source code, stored as a list of tokens.
    private List<TokenString> sourceTokens = new ArrayList<>();
    // The parsed statements.
    private List<Statement> statements = new ArrayList<>();

    // The table of functions within the users program, when a function is parsed, it is stored here.
    private static List<FunctionData> functionTable = new ArrayList<>();
    // The table of global variables used within the users program.
    private static List<VariableData> variableTable = new ArrayList<>();

    public static Thread interpreterThread;

    public static void printCompileError(int line, Class cause, String error) {
        System.out.println("[Error @" + cause.getSimpleName() + "] Line = " + line + ", " + error);
        interpreterThread.stop();
    }

    public static void printRuntimeError(Class cause, String error) {
        System.out.println("[RuntimeError @" + cause.getSimpleName() + "] " + error);
        interpreterThread.stop();
    }

    public static void printStatus(Class cause, String status) {
        System.out.println("[" + cause.getSimpleName() + "] " + status);
    }

    public static void addFunction(FunctionData function) {
        functionTable.add(function);
    }

    public static boolean functionExists(String functionName) {
        for (FunctionData f : functionTable) {
            if (f.functionName.equalsIgnoreCase(functionName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean functionExists(Token identifierToken) {
        return functionExists(identifierToken.contents.toUpperCase());
    }


    public static FunctionData getFunctionData(String name) {
        if (functionExists(name)) {
            for(FunctionData data : functionTable) {
                if (data.functionName.equalsIgnoreCase(name)) {
                    return data;
                }
            }
        }
        return null;
    }

    public static void addVariable(VariableData variable) {
        variableTable.add(variable);
    }

    public static boolean variableExists(String variableName) {
        for (VariableData v : variableTable) {
            if (v.variableName.equalsIgnoreCase(variableName)) {
                return true;
            }
        }
        return false;
    }

    public static VariableData getVariableData(String name) {
        if (variableExists(name)) {
            for (VariableData var : variableTable) {
                if (var.variableName.equalsIgnoreCase(name)) {
                    return var;
                }
            }
        }
        return null;
    }

    public Interpreter(List<String> source) {
        sourceCode.clear();
        sourceTokens.clear();
        statements.clear();
        functionTable.clear();
        variableTable.clear();

        sourceCode = source;

        tokenizer = new Tokenizer();
        parser = new Parser();
        interpreter = new StatementInterpreter();
    }

    public void tokenize() {
        int lineCount = 1;
        for (String line : sourceCode) {
            sourceTokens.add(tokenizer.tokenize(line, lineCount));

            lineCount++;
        }

        for (TokenString t : sourceTokens) {
            System.out.println(t.toString());
        }
    }

    public void parse() {
        parser.setLines(sourceTokens);
        statements = parser.performParse();
    }

    public void interpret() {
        int programEntryPoint = -1;

        for (int i = 0; i < statements.size(); i++) {
            if (!(statements.get(i) instanceof FunctionStatement)) {
                programEntryPoint = i;
                break;
            }
        }

        if (programEntryPoint == -1) {
            printCompileError(-1, Interpreter.class, "Could not find program entry point!");
        }

        interpreter.setStatements(statements);
        interpreter.execute();
    }

    public void run() {
        fileHandler.clear();

        tokenize();
        parse();
        interpret();

        Interpreter.printStatus(Interpreter.class, "Finished program execution!");
    }



}
