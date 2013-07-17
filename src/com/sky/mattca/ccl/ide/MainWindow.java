package com.sky.mattca.ccl.ide;

import com.sky.mattca.ccl.controlboard.K8055Library;
import com.sky.mattca.ccl.interpretation.Interpreter;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Arrays;

/**
 * User: 06mcarter
 * Date: 26/02/13
 * Time: 14:18
 */
public class MainWindow implements ActionListener {
    private JPanel panel1;
    private JTextArea textArea1;
    private JButton functionButton;
    private JButton ifStatementButton;
    private JButton forLoopButton;
    private JButton whileLoopButton;
    private JButton repeatLoopButton;
    private JButton switchStatementButton;
    private JButton caseStatementButton;
    private JButton variableDefinitionButton;
    private JButton arrayDefinitionButton;
    private JButton callStatementButton;
    private JTextField interpreterInputArea;
    private JTextArea interpreterOutputArea;
    private JMenuBar menuBar;
    private JFrame frame;

    private boolean startedNewFile;
    private boolean textChanged;
    private String filePath;
    private JFileChooser fileChooser;

    public MainWindow() {
        setupMenuBar();

        textArea1.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                textChanged = true;
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                textChanged = true;
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                textChanged = true;
            }
        });

        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".txt") || file.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Text File (*.TXT)";
            }
        });
    }

    private void setupMenuBar() {
        menuBar = new JMenuBar();

        // Set up file menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem newItem = new JMenuItem("New...");
        newItem.addActionListener(this);
        JMenuItem openItem = new JMenuItem("Open...");
        openItem.addActionListener(this);
        JMenuItem saveItem = new JMenuItem("Save...");
        saveItem.addActionListener(this);
        JMenuItem saveAsItem = new JMenuItem("Save As...");
        saveAsItem.addActionListener(this);
        JMenuItem exitItem = new JMenuItem("Exit.");
        exitItem.addActionListener(this);
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.add(exitItem);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem cutItem = new JMenuItem("Cut");
        cutItem.addActionListener(this);
        JMenuItem copyItem = new JMenuItem("Copy");
        copyItem.addActionListener(this);
        JMenuItem pasteItem = new JMenuItem("Paste");
        pasteItem.addActionListener(this);
        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);

        JMenu interpreterMenu = new JMenu("Interpreter");
        JMenuItem runItem = new JMenuItem("Run...");
        runItem.addActionListener(this);
        interpreterMenu.add(runItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(interpreterMenu);

        functionButton.addActionListener(this);

        newFile();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.exit(-10);
        }

        final MainWindow mainWindow = new MainWindow();
        final JFrame frame = new JFrame("CustomControlLanguage IDE");

        JFrame.setDefaultLookAndFeelDecorated(true);
        frame.setJMenuBar(mainWindow.menuBar);
        frame.setContentPane(mainWindow.panel1);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent windowEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void windowClosing(WindowEvent windowEvent) {
                System.out.println("Window Closing!");
                if (mainWindow.textChanged) {
                    int result = JOptionPane.showConfirmDialog(frame, "The file has changed, would you like to save?", "Exit", JOptionPane.YES_NO_CANCEL_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        mainWindow.saveFile(false);
                        frame.dispose();
                    } else if (result == JOptionPane.CANCEL_OPTION) {
                        frame.setVisible(true);
                    } else {
                        frame.dispose();
                    }
                } else {
                    frame.dispose();
                }
            }

            @Override
            public void windowClosed(WindowEvent windowEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
                System.exit(0);
            }

            @Override
            public void windowIconified(WindowEvent windowEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void windowDeiconified(WindowEvent windowEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void windowActivated(WindowEvent windowEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void windowDeactivated(WindowEvent windowEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setSize(800, 600);
        frame.pack();
        frame.setVisible(true);

        mainWindow.frame = frame;

        // Set the interpreter output/input to the main window
        IDEOutputStream outputStream = new IDEOutputStream(mainWindow.interpreterOutputArea);
        System.setOut(new PrintStream(outputStream));

        IDEInputStream inputStream = new IDEInputStream(mainWindow.interpreterInputArea);
        System.setIn(inputStream);
    }

    private void newFile() {
        filePath = "";
        startedNewFile = true;
        textChanged = false;
        textArea1.setText("");
    }

    private void saveFile(boolean forceDialog) {
        if ((textChanged || startedNewFile) || forceDialog) {
            int result = fileChooser.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    File newFile = new File(fileChooser.getSelectedFile().getAbsolutePath() + ".txt");
                    newFile.createNewFile();

                    FileWriter writer = new FileWriter(newFile);
                    writer.write(textArea1.getText());
                    writer.close();

                    filePath = newFile.getAbsolutePath();
                    textChanged = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (textChanged) {
            try {
                File fileHandle = new File(filePath);
                FileWriter writer = new FileWriter(fileHandle);
                writer.write(textArea1.getText());
                writer.close();

                textChanged = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openFile() {
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File openedFile = fileChooser.getSelectedFile();
                BufferedReader reader = new BufferedReader(new FileReader(openedFile));

                String line = "";
                String fileContents = "";
                while (true) {
                    line = reader.readLine();

                    if (line == null) {
                        break;
                    }

                    fileContents += line;
                    fileContents += "\r\n";
                }

                textArea1.setText(fileContents);

                filePath = openedFile.getAbsolutePath();
                textChanged = false;
                startedNewFile = false;

                System.out.println(filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String itemPressed = actionEvent.getActionCommand();
        switch (itemPressed) {
            case "New...":
                System.out.println("New pressed.");
                if (textChanged) {
                    int result = JOptionPane.showConfirmDialog(frame, "The file has changed, would you like to save?", "New...", JOptionPane.YES_NO_CANCEL_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        saveFile(false);
                    }
                    if (result != JOptionPane.CANCEL_OPTION) {
                        newFile();
                    }
                } else {
                    newFile();
                }
                break;
            case "Open...":
                System.out.println("Open pressed.");
                if (textChanged) {
                    int result = JOptionPane.showConfirmDialog(frame, "The file has changed, would you like to save?", "Open...", JOptionPane.YES_NO_CANCEL_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        saveFile(false);
                    }
                    if (result != JOptionPane.CANCEL_OPTION) {
                        openFile();
                    }
                } else {
                    openFile();
                }
                break;
            case "Save...":
                System.out.println("Save pressed.");
                saveFile(false);
                break;
            case "Save As...":
                System.out.println("Save As pressed.");
                saveFile(true);
                break;
            case "Exit.":
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                System.out.println("Exit pressed.");
            case "Cut":
                System.out.println("Cut pressed.");
                textArea1.cut();
                break;
            case "Copy":
                System.out.println("Copy pressed.");
                textArea1.copy();
                break;
            case "Paste":
                System.out.println("Paste pressed.");
                textArea1.paste();
                break;
            case "Run...":
                System.out.println("Run pressed.");

                try {
                    interpreterOutputArea.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Interpreter interpreter = new Interpreter(Arrays.asList(textArea1.getText().split("\n")));
                Interpreter.interpreterThread = new Thread(interpreter);
                Interpreter.interpreterThread.start();
                break;
            case "Function Statement":
                FunctionStatementWindow window = new FunctionStatementWindow();
                window.setTitle("Insert...");
                window.setMinimumSize(new Dimension(400, 200));
                window.setResizable(false);
                window.setVisible(true);

                StringBuffer buffer = new StringBuffer();
                buffer.append("Function ");
                buffer.append(window.functionName);
                buffer.append(" (");
                for (int i = 0; i < window.paramCount; i++) {
                    buffer.append("param");
                    buffer.append(i);
                    if (i < window.paramCount - 1) {
                        buffer.append(", ");
                    }
                }
                buffer.append(")\n");
                buffer.append("\nEndFunction");

                textArea1.insert(buffer.toString(), textArea1.getCaretPosition());

                break;
            default:
                System.out.println("Unhandled action!");
                break;
        }
    }
}
