package com.sky.mattca.ccl.ide;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Matt
 * Date: 24/04/13
 * Time: 20:57
 * To change this template use File | Settings | File Templates.
 */
public class IDEOutputStream extends OutputStream {

    private JTextArea textArea;

    public IDEOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
        textArea.append(String.valueOf((char)b));
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

}
