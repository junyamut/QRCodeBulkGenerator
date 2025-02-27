package xyz.joseyamut.qrCodeBulkGen.dialog;

import javax.swing.*;
import java.awt.*;
import java.io.PrintStream;

public class LogDialogWindow extends JFrame {
    private PrintStream standardOut;

    public LogDialogWindow() {
        JTextArea jTextArea = new JTextArea(50, 10);
        jTextArea.setEditable(false);
        PrintStream printStream = new PrintStream(new LogToDialogStream(jTextArea));

        standardOut = System.out;
        System.setOut(printStream);
        System.setErr(printStream);

        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(10, 5, 10, 5);
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        JLabel jLabel = new JLabel("QR Code Bulk Generator");
        add(jLabel, gridBagConstraints);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(new JScrollPane(jTextArea), gridBagConstraints);
        setTitle("QR Code Bulk Generator - Log");
        setSize(640, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void printToLogDialog(String logMessage, String... args) {
        if (args.length != 0) {
            logMessage = String.format(logMessage, (Object[]) args);
        }
        String finalLogMessage = logMessage;
        Thread thread = new Thread(() -> System.out.println(finalLogMessage));
        thread.start();
    }

    public static void displayLogDialog() {
        new LogDialogWindow().setVisible(true);
    }
}