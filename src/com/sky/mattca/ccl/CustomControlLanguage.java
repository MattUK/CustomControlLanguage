package com.sky.mattca.ccl;

import com.sky.mattca.ccl.controlboard.K8055Library;
import com.sky.mattca.ccl.controlboard.SevenSegmentDisplay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

// TODO: Improve error messages. Group them together into one class.

/**
 * User: Matt
 * Date: 07/10/12
 * Time: 22:27
 */
public class CustomControlLanguage {

    public static void wait(int time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        System.out.println(Float.toString(23.13f));
//        System.exit(0);
//
//        System.out.println("Please enter a file to interpret: ");
//
//        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
//
//        try {
//            String file = inputReader.readLine();
//
//            Interpreter fileInterpreter = Interpreter.loadFile(file);
//            fileInterpreter.tokenize();
//            fileInterpreter.parse();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("Finished.");
//        System.exit(0);

        K8055Library.loadLibraryInterface();

        K8055Library.enableBoard();

        while(!K8055Library.getLibraryInterface().ReadDigitalChannel(1)) {
            continue;
        }

        K8055Library.enableDigitalOutputs(1, 2, 3, 4, 5, 6, 7, 8);

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        System.out.println("Please enter the program to run (Calculator, Counter, Chase):");

//        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));

//        try {
//            String file = inputReader.readLine();
//
//            if (file.toUpperCase().equals("CALCULATOR")) {
//                int numberOne = 0, numberTwo = 0, operator = 0;
//
//                try {
//                    while (true) {
//                        K8055Library.getLibraryInterface().WriteAllDigital(0);
//
//                        if (K8055Library.getLibraryInterface().ReadDigitalChannel(5)) {
//                            numberOne++;
//                            System.out.println(numberOne);
//                            Thread.sleep(1000);
//                        } else if (K8055Library.getLibraryInterface().ReadDigitalChannel(4)) {
//                            numberTwo++;
//                            System.out.println(numberTwo);
//                            Thread.sleep(1000);
//                        } else if (K8055Library.getLibraryInterface().ReadDigitalChannel(3)) {
//                            operator++;
//                            System.out.println(operator);
//                            Thread.sleep(1000);
//                        } else if (K8055Library.getLibraryInterface().ReadDigitalChannel(2)) {
//                            SevenSegmentDisplay.displayCharacter(String.valueOf(numberOne + numberTwo).charAt(0));
//                            numberOne = 0;
//                            numberTwo = 0;
//                            operator = 0;
//                            Thread.sleep(1000);
//                        } else if (K8055Library.getLibraryInterface().ReadDigitalChannel(1)) {
//                            break;
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } else if (file.toUpperCase().equals("COUNTER")) {
//                boolean cont = true;
//                while (cont) {
////                        K8055Library.getLibraryInterface().WriteAllDigital(0);
//
////                        if (K8055Library.getLibraryInterface().ReadDigitalChannel(4) == true) {
//                    for (int i = 0; i < 10; i++) {
//                        SevenSegmentDisplay.displayCharacter(String.valueOf(i).charAt(0));
//
//                        Thread.sleep(500);
//
//                        if (K8055Library.getLibraryInterface().ReadDigitalChannel(1) == true) {
//                            cont = false;
//                        }
//                    }
////                        } else if (K8055Library.getLibraryInterface().ReadDigitalChannel(1) == true) {
////                            break;
////                        }
//                }
//            } else if (file.toUpperCase().equals("CHASE")) {
//                boolean cont = true;
//                while (cont) {
////                    if (K8055Library.getLibraryInterface().ReadDigitalChannel(5)) {
//                    for (int i = 0; i < 6; i++) {
//                        K8055Library.enableDigitalOutputs(i);
//
//                        Thread.sleep(200);
//
//                        if (K8055Library.getLibraryInterface().ReadDigitalChannel(1) == true) {
//                            cont = false;
//                        }
//                    }
////                    } else if (K8055Library.getLibraryInterface().ReadDigitalChannel(1)) {
////                        break;
////                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        try {
//            while(true) {
//                K8055Library.getLibraryInterface().WriteAllDigital(0);
//
//                if (K8055Library.getLibraryInterface().ReadDigitalChannel(5) == true) {
//                    SevenSegmentDisplay.displayCharacter('!');
//
//                    Thread.sleep(500);
//                } else if (K8055Library.getLibraryInterface().ReadDigitalChannel(4) == true) {
//                    for (int i = 0; i < 10; i++) {
//                        SevenSegmentDisplay.displayCharacter(String.valueOf(i).charAt(0));
//
//                        Thread.sleep(500);
//                    }
//                } else if (K8055Library.getLibraryInterface().ReadDigitalChannel(1) == true) {
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        K8055Library.disableBoard();

        System.exit(0);

//        Interpreter interpreter = new Interpreter();
//
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//
//        System.out.println("Please enter a line to tokenize: ");
//
//        try {
//            String line = reader.readLine();
//            interpreter.addLine(line);
//
//            interpreter.tokenize();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("Completed tokenization.");
//
//        System.exit(0);
    }

}
