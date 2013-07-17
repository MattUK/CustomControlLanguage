package com.sky.mattca.ccl.controlboard;

import com.sky.mattca.ccl.interpretation.Interpreter;

import java.util.HashMap;
import java.util.Map;

/**
 * User: 06mcarter
 * Date: 20/11/12
 * Time: 14:17
 */
public class SevenSegmentDisplay {

    public static final int topSegment = 0;
    public static final int topRightSegment = 1;
    public static final int bottomRightSegment = 2;
    public static final int bottomSegment = 3;
    public static final int bottomLeftSegment = 4;
    public static final int topLeftSegment = 5;
    public static final int middleSegment = 6;

    public static Map<Character, Byte> characterMapping = new HashMap<Character, Byte>() {
        {
            put('0', K8055Library.setBits(topSegment, topRightSegment, bottomRightSegment, bottomSegment, bottomLeftSegment, topLeftSegment));
            put('1', K8055Library.setBits(topRightSegment, bottomRightSegment));
            put('2', K8055Library.setBits(topSegment, topRightSegment, middleSegment, bottomLeftSegment, bottomSegment));
            put('3', K8055Library.setBits(topSegment, topRightSegment, middleSegment, bottomRightSegment, bottomSegment));
            put('4', K8055Library.setBits(topLeftSegment, middleSegment, topRightSegment, bottomRightSegment));
            put('5', K8055Library.setBits(topSegment, topLeftSegment, middleSegment, bottomRightSegment, bottomSegment));
            put('6', K8055Library.setBits(topSegment, topLeftSegment, bottomLeftSegment, bottomSegment, bottomRightSegment, middleSegment));
            put('7', K8055Library.setBits(topSegment, topRightSegment, bottomRightSegment));
            put('8', (byte) 255);
            put('9', K8055Library.setBits(topSegment, topRightSegment, middleSegment, topLeftSegment, bottomRightSegment, bottomSegment));
            put('!', K8055Library.setBits(topRightSegment, topLeftSegment, bottomSegment));
        }
    };

    public static void displayCharacter(char displayCharacter) {
        if (K8055Library.getLibraryInterface() == null) {
            Interpreter.printCompileError(-1, SevenSegmentDisplay.class, "The K8055 Library is required.");
        }

        K8055Library.getLibraryInterface().WriteAllDigital(characterMapping.get(displayCharacter));
    }

}
