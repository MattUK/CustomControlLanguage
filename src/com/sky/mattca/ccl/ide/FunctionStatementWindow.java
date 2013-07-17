package com.sky.mattca.ccl.ide;

import javax.swing.*;
import java.awt.event.*;

public class FunctionStatementWindow extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;
    private JTextField textField2;

    public String functionName;
    public int paramCount;

    public FunctionStatementWindow() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
// add your code here
        functionName = textField1.getText();
        if (!Character.isLetter(functionName.charAt(0))) {
            JOptionPane.showMessageDialog(null, "Function must begin with a letter.");
            return;
        }
        for (int i = 0; i < functionName.length(); i++) {
            if (!Character.isLetterOrDigit(functionName.charAt(i)) && !(functionName.charAt(i) == '_')) {
                JOptionPane.showMessageDialog(null, "Function name must contain only letters or numbers.");
                return;
            }
        }
        try {
            paramCount = Integer.parseInt(textField2.getText());
            if (paramCount < 0) {
                JOptionPane.showMessageDialog(null, "Parameter count must be zero or more.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "'" + textField2.getText() + "' is not a number.");
            return;
        }
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }
}
