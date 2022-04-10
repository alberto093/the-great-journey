/*
 * Copyright (C) 2020 pierpaolo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.saltarelli.journey;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 *
 * @author Alberto
 */
public class Console extends javax.swing.JFrame {

    private final PipedInputStream inputStream = new PipedInputStream();
    private final PipedOutputStream outputStream = new PipedOutputStream();
    private final PrintStream textFieldStream;
    private final TextPanePrinter textPanePrinter;

    private List<String> previousInputs = new ArrayList<>();
    private Integer previousInputsIndex;

    /**
     * Creates new form ConsoleUI
     */
    public Console() {
        initComponents();

        try {
            outputStream.connect(inputStream);

        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        textFieldStream = new PrintStream(outputStream);

        // start engine
        textPanePrinter = new TextPanePrinter(textPane);
        Engine engine = new Engine(inputStream, textPanePrinter);
        Thread engineThread = new Thread(engine);
        engineThread.start();

        textField.requestFocus();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Console.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane = new javax.swing.JScrollPane();
        textPane = new javax.swing.JTextPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        textField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Journey");
        setPreferredSize(new java.awt.Dimension(850, 600));

        scrollPane.setBackground(new java.awt.Color(0, 0, 0));
        scrollPane.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(70, 60, 60), 5), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        scrollPane.setPreferredSize(new java.awt.Dimension(600, 400));

        textPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));
        textPane.setFont(textPane.getFont().deriveFont((float)16));
        textPane.setForeground(new java.awt.Color(20, 20, 20));
        textPane.setFocusable(false);
        textPane.setSelectionColor(new java.awt.Color(0, 0, 0));
        scrollPane.setViewportView(textPane);

        getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);

        jPanel1.setBackground(new java.awt.Color(70, 60, 60));
        jPanel1.setPreferredSize(new java.awt.Dimension(100, 80));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setBackground(new java.awt.Color(70, 60, 60));
        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 8, 0, 8));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 69, 0));
        jLabel1.setText(">");
        jPanel2.add(jLabel1);

        textField.setBackground(new java.awt.Color(70, 60, 60));
        textField.setFont(textField.getFont().deriveFont(textField.getFont().getStyle() | java.awt.Font.BOLD, 16));
        textField.setForeground(new java.awt.Color(255, 69, 0));
        textField.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 2, 0, 6));
        textField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textFieldActionPerformed(evt);
            }
        });
        textField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textFieldKeyPressed(evt);
            }
        });
        jPanel2.add(textField);

        jPanel1.add(jPanel2, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void textFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldKeyPressed

        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                String input = textField.getText();
                previousInputs.add(input);
                previousInputsIndex = previousInputs.size();
                textFieldStream.println(input);
                textField.setText("");

                StyleContext context = StyleContext.getDefaultStyleContext();
                AttributeSet attributes = context.addAttribute(
                        SimpleAttributeSet.EMPTY,
                        StyleConstants.Foreground,
                        textField.getForeground());

                attributes = context.addAttribute(attributes, StyleConstants.FontFamily, textField.getFont().getFontName());
                textPanePrinter.println(input, attributes);

                synchronized (inputStream) {
                    inputStream.notify();
                }
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_UP:
                previousInputsIndex += evt.getKeyCode() == KeyEvent.VK_DOWN ? 1 : -1;

                if (previousInputsIndex > 0 && previousInputsIndex < previousInputs.size()) {
                    java.awt.EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            if (previousInputsIndex > 0 && previousInputsIndex < previousInputs.size()) {
                                textField.setText(previousInputs.get(previousInputsIndex));
                            }
                        }
                    });
                }

                break;
            default:
                break;
        }
    }//GEN-LAST:event_textFieldKeyPressed

    private void textFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textFieldActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Console.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Console.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Console.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Console.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Console().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextField textField;
    private javax.swing.JTextPane textPane;
    // End of variables declaration//GEN-END:variables
}
