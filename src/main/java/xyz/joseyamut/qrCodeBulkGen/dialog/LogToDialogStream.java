package xyz.joseyamut.qrCodeBulkGen.dialog;

import javax.swing.*;
import java.io.OutputStream;

public class LogToDialogStream extends OutputStream {
    private final JTextArea logDialog;

    public LogToDialogStream(JTextArea logDialog) {
        this.logDialog = logDialog;
    }

    @Override
    public void write(int c) {
        logDialog.append(String.valueOf((char)c));
        logDialog.setCaretPosition(logDialog.getDocument().getLength());
    }
}
