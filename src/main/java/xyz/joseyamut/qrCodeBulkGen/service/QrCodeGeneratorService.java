package xyz.joseyamut.qrCodeBulkGen.service;

import org.apache.poi.ss.usermodel.*;
import org.springframework.util.StringUtils;
import xyz.joseyamut.qrCodeBulkGen.config.StoreImageConfiguration;
import xyz.joseyamut.qrCodeBulkGen.dialog.LogDialogWindow;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import xyz.joseyamut.qrCodeBulkGen.QrCodeBulkGenAppException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.Map;

@Service
public class QrCodeGeneratorService {

    private final StoreImageConfiguration storeImageConfiguration;
    private final QrCodeEncoderService qrCodeEncoderService;
    private final WorkbookReaderService workbookReaderService;
    private final WorkbookWriterService workbookWriterService;
    public static final String NEWLINE = System.lineSeparator();

    public QrCodeGeneratorService(StoreImageConfiguration storeImageConfiguration, QrCodeEncoderService qrCodeEncoderService,
                                  WorkbookReaderService workbookReaderService, WorkbookWriterService workbookWriterService) {
        this.storeImageConfiguration = storeImageConfiguration;
        this.qrCodeEncoderService = qrCodeEncoderService;
        this.workbookReaderService = workbookReaderService;
        this.workbookWriterService = workbookWriterService;
    }

    @PostConstruct
    private void generateFromList() {
        String sourceFile = storeImageConfiguration.getDataStore().getSrcWorkbookName();
        String filenamePrefix = storeImageConfiguration.getImageParam().getFilenamePrefix();
        String formatName = storeImageConfiguration.getImageParam().getFormatName();
        String destinationDirImg = storeImageConfiguration.getDataStore().getDstDirImg();
        String destinationDirWorkbook = storeImageConfiguration.getDataStore().getDstDirWorkbook();
        String destinationWorkbookName = storeImageConfiguration.getDataStore().getDstWorkbookName().trim();
        boolean isQrCodeToFile = storeImageConfiguration.getDataStore().isQrCodeToFile();
        List<String> colHeaderNames = storeImageConfiguration.getWorkbookParam().getColHeaderNames();
        Map<String, String> listForEncoding = workbookReaderService.getListFromWorkbook();

        assert listForEncoding != null;
        String batchSize = String.valueOf(listForEncoding.size());
        String successFormat = "( %s ) QR codes were processed in total."
                + NEWLINE + NEWLINE +
                "QR Codes saved in worksheet --> %s"
                + NEWLINE
                + " located at --> %s%s";
        String successMessage = String.format(successFormat,
                batchSize,
                destinationWorkbookName,
                destinationDirWorkbook,
                NEWLINE);
        String logHeaders = "Worksheet source list --> " + sourceFile + "%s" +
                "Found ( %s ) rows from the list. %s%s" +
                "Generating QR codes.. . ";
        String logRow = "%s --- FILE SAVED %s";

        LogDialogWindow.displayLogDialog();
        LogDialogWindow.printToLogDialog(logHeaders, NEWLINE,
                batchSize, NEWLINE, NEWLINE);

        boolean writeToWorkbook = StringUtils.hasText(destinationDirWorkbook)
                && StringUtils.hasText(destinationWorkbookName);

        if (writeToWorkbook) {
            workbookWriterService.createWorkbookWithInitialSheet();
            workbookWriterService.setNameOfSheetAtIndex(0, "QR Codes List");
            // Initialize a Font object and set attributes
            workbookWriterService.createFont();
            workbookWriterService.getFont().setBold(true);
            // Initialize a CellStyle object and set attributes
            workbookWriterService.createCellStyle();
            workbookWriterService.getCellStyle().setWrapText(true);
            workbookWriterService.getCellStyle().setFont(workbookWriterService.getFont());

            try {
                // Write sheet column headers
                workbookWriterService.writeSheetHeaders(colHeaderNames);

                int qrCodesRowCount = 1;
                for (Map.Entry<String, String> entry : listForEncoding.entrySet()) {
                    BufferedImage bufferedImage = qrCodeEncoderService.generate(entry.getValue());
                    String qrCodeFileName = filenamePrefix + entry.getKey() +
                            "." + formatName;
                    // Write text data to rows
                    workbookWriterService.writeRowTextData(qrCodesRowCount,
                            entry.getValue(), qrCodeFileName);
                    // Write QR code image to workbook
                    int qrCodeImagePicture = workbookWriterService.addPicture(bufferedImageToByteArray(bufferedImage, formatName),
                            Workbook.PICTURE_TYPE_JPEG);
                    workbookWriterService.writeRowQrCode(qrCodesRowCount, qrCodeImagePicture);
                    qrCodesRowCount++;

                    if (isQrCodeToFile) {
                        File qrCodeFile = new File(destinationDirWorkbook + destinationDirImg,
                                filenamePrefix + entry.getKey() + "." + formatName);

                        ImageIO.write(bufferedImage, formatName, qrCodeFile);
                        LogDialogWindow.printToLogDialog(logRow, "  " +destinationDirImg + qrCodeFile.getName(),
                                NEWLINE);
                    }
                }

                File dstFile = new File(destinationDirWorkbook, destinationWorkbookName);
                FileOutputStream fileOutputStream = new FileOutputStream(dstFile);
                workbookWriterService.write(fileOutputStream);
                fileOutputStream.close();
                workbookWriterService.close();
            } catch (IOException e) {
                throw new QrCodeBulkGenAppException(e);
            }
        }

        LogDialogWindow.printToLogDialog(successMessage);
    }

    private byte[] bufferedImageToByteArray(BufferedImage bufferedImage, String formatName) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, formatName, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

}
