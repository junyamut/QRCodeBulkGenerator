package xyz.joseyamut.qrCodeBulkGen.service;

import xyz.joseyamut.qrCodeBulkGen.dialog.LogDialogWindow;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.joseyamut.qrCodeBulkGen.QrCodeBulkGenAppException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import static xyz.joseyamut.qrCodeBulkGen.service.WorkbookReaderService.NEWLINE;

@Slf4j
@Service
public class QrCodeGeneratorService {

    @Autowired
    private WorkbookReaderService workbookReaderService;

    @Autowired
    private QrCodeEncoderService qrCodeEncoderService;

    private final String destinationDir;
    private final String filenamePrefix;
    private final String formatName;

    public QrCodeGeneratorService(String destinationDir, String filenamePrefix, String formatName) {
        this.destinationDir = destinationDir;
        this.filenamePrefix = filenamePrefix;
        this.formatName = formatName;
    }

    @PostConstruct
    private void generateFromList() {
        Map<String, String> fromWorkbook = workbookReaderService.getListFromWorkbook();

        assert fromWorkbook != null;
        String successFormat = "%s QR codes in total were processed." + NEWLINE + NEWLINE +
                "QR Codes generated as %s format are saved in %s folder.";
        String successMessage = String.format(successFormat,
                fromWorkbook.size(),
                formatName.toUpperCase(),
                destinationDir);
        log.info("Found {} rows from the list.", fromWorkbook.size());

        LogDialogWindow.displayLogDialog();
        LogDialogWindow.printToLogDialog("Found (" + fromWorkbook.size() + ") rows from the list:" + NEWLINE);

        try {
            int counter = 1;
            for (Map.Entry<String, String> entry : fromWorkbook.entrySet()) {
                BufferedImage bufferedImage = qrCodeEncoderService.generate(entry.getValue());
                File outputfile = new File(destinationDir +
                        filenamePrefix + entry.getKey() +
                        "." + formatName);
                ImageIO.write(bufferedImage, formatName, outputfile);

                LogDialogWindow.printToLogDialog("--- " + counter +
                        ".) Saved QR Code FILE (" +
                        outputfile.getName() +
                        ") TO " + destinationDir + NEWLINE);
                counter++;
            }
        } catch (IOException e) {
            throw new QrCodeBulkGenAppException(e);
        }

        JOptionPane.showMessageDialog(null, successMessage, "QR Code Bulk Generator", JOptionPane.INFORMATION_MESSAGE);
    }

}
