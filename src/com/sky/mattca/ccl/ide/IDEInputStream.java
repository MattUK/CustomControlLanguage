package com.sky.mattca.ccl.ide;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: Matt
 * Date: 24/04/13
 * Time: 21:26
 * To change this template use File | Settings | File Templates.
 */
public class IDEInputStream extends InputStream implements ActionListener {

    private JTextField textField;
    private String str = null;
    private int position = 0;

    public IDEInputStream(JTextField textField) {
        this.textField = textField;
        this.textField.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        str = textField.getText() + "\n";
        position = 0;
        textField.setText("");
        synchronized (this) {
            this.notifyAll();
        }
    }

    @Override
    public int read() {
        if (str != null && position == str.length()) {
            str = null;
            return java.io.StreamTokenizer.TT_EOF;
        }

        while (str == null || position >= str.length()) {
            try {
                synchronized (this) {
                    this.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return str.charAt(position ++);
    }

}
