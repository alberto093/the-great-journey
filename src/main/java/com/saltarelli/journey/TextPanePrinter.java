/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 *
 * @author Alberto
 */
public class TextPanePrinter {

    private final JTextPane textPane;
    private AttributeSet currentAttributes;

    public TextPanePrinter(JTextPane textPane) {
        this.textPane = textPane;
    }
    
    public void println() {
        updateDocument("\n");
    }
    
    public void print(String x) {
        updateDocument(x);
    }

    public void println(String x) {
        updateDocument(x + "\n");
    }

    public void print(String x, AttributeSet attributes) {
        this.currentAttributes = attributes;
        print(x);
    }

    public void println(String x, AttributeSet attributes) {
        this.currentAttributes = attributes;
        println(x);
    }

    private void updateDocument(String text) {
        Document document = textPane.getDocument();

        if (currentAttributes != null) {
            textPane.setCharacterAttributes(currentAttributes, false);
        }

        try {
            document.insertString(document.getLength(), text, null);
            textPane.setCaretPosition(document.getLength());
        } catch (BadLocationException ex) {

        }
    }
}
