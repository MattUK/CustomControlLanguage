package com.sky.mattca.ccl.filesystem;

import com.sky.mattca.ccl.interpretation.Interpreter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Matt
 * Date: 24/04/13
 * Time: 19:51
 * To change this template use File | Settings | File Templates.
 */
public class FileHandler {

    private Map<Integer, File> fileMap;
    private int handleCounter;

    public FileHandler() {
        fileMap = new HashMap<>();
    }

    public int openFile(String fileName) {
        try {
            File fileHandle = new File(fileName);
            if (!fileHandle.exists()) {
                return -1;
            }
            handleCounter ++;
            fileMap.put(handleCounter, fileHandle);
            return handleCounter;
        } catch (Exception e) {
            return -1;
        }
    }

    public int createFile(String fileName) {
        try {
            File fileHandle = new File(fileName);
            if (fileHandle.exists()) {
                return -1;
            }
            FileWriter writer = new FileWriter(fileHandle);
            writer.write(" ");
            writer.close();
            handleCounter ++;
            fileMap.put(handleCounter, fileHandle);
            return handleCounter;
        } catch (Exception e) {
            return -1;
        }
    }

    public void closeFile(int handle) {
        if (fileMap.containsKey(handle)) {
            fileMap.remove(handle);
        } else {
            Interpreter.printRuntimeError(FileHandler.class, "File handle '" + handle + "' is not a valid handle.");
        }
    }

    public boolean deleteFile(String fileName) {
        try {
            File fileHandle = new File(fileName);
            fileHandle.delete();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void writeToFile(int handle, String data) {
        if (fileMap.containsKey(handle)) {
            try {
                FileWriter writer = new FileWriter(fileMap.get(handle));
                writer.write(data);
                writer.close();
            } catch (Exception e) {
                Interpreter.printRuntimeError(FileHandler.class, "Unknown error, could not write to file.");
            }
        } else {
            Interpreter.printRuntimeError(FileHandler.class, "File handle '" + handle + "' is not a valid handle.");
        }
    }

    public void writeLineToFile(int handle, String data) {
        writeToFile(handle, data + "\n");
    }

    public String readFromFile(int handle) {
        if (fileMap.containsKey(handle)) {
            try {
                FileReader reader = new FileReader(fileMap.get(handle));
                return Character.toString(Character.valueOf((char) reader.read()));
            } catch (Exception e) {
                Interpreter.printRuntimeError(FileHandler.class, "Unknown error, could not read from file.");
            }
        } else {
            Interpreter.printRuntimeError(FileHandler.class, "File handle '" + handle + "' is not a valid handle.");
        }
        return null;
    }

    public String readLineFromFile(int handle) {
        if (fileMap.containsKey(handle)) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(fileMap.get(handle)));
                return reader.readLine();
            } catch (Exception e) {
                Interpreter.printRuntimeError(FileHandler.class, "Unknown error, could not read from file.");
            }
        } else {
            Interpreter.printRuntimeError(FileHandler.class, "File handle '" + handle + "' is not a valid handle.");
        }
        return null;
    }

    public void clear() {
        fileMap.clear();
        handleCounter = 0;
    }

}
