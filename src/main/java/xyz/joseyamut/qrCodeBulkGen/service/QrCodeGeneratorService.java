package xyz.joseyamut.qrCodeBulkGen.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;
import xyz.joseyamut.qrCodeBulkGen.dialog.LogDialogWindow;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.joseyamut.qrCodeBulkGen.QrCodeBulkGenAppException;
import xyz.joseyamut.qrCodeBulkGen.model.DataStore;
import xyz.joseyamut.qrCodeBulkGen.model.ImageParam;
import xyz.joseyamut.qrCodeBulkGen.model.WorkbookParam;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.Map;

import static xyz.joseyamut.qrCodeBulkGen.service.WorkbookReaderService.NEWLINE;

@Service
public class QrCodeGeneratorService {

    @Autowired
    private WorkbookReaderService workbookReaderService;

    @Autowired
    private QrCodeEncoderService qrCodeEncoderService;

    private final String destinationDirImg;
    private final String filenamePrefix;
    private final String formatName;
    private final String destinationDirWorkbook;
    private final String destinationWorkbookName;
    private final List<String> colHeaderNames;
    private final boolean isQrCodeToFile;
    // Adjust row height and width column so QR codes are not scaled too small
    private static final int DEFAULT_COL_WIDTH = 5120*2;
    private static final float DEFAULT_ROW_HEIGHT = 200f;

    public QrCodeGeneratorService(DataStore dataStore, ImageParam imageParam,
                                  WorkbookParam workbookParam) {
        this.filenamePrefix = imageParam.getFilenamePrefix();
        this.formatName = imageParam.getFormatName();
        this.destinationDirImg = dataStore.getDstDirImg();
        this.destinationDirWorkbook = dataStore.getDstDirWorkbook();
        this.destinationWorkbookName = dataStore.getDstWorkbookName().trim();
        this.colHeaderNames = workbookParam.getColHeaderNames();
        this.isQrCodeToFile = dataStore.isQrCodeToFile();
    }

    @PostConstruct
    private void generateFromList() {
        Map<String, String> fromWorkbook = workbookReaderService.getListFromWorkbook();

        assert fromWorkbook != null;
        String workbookSize = String.valueOf(fromWorkbook.size());
        String successFormat = "%s QR codes in total were processed."
                + NEWLINE + NEWLINE +
                "QR Codes generated have been saved in -- %s -- "
                + NEWLINE
                + " at the [ %s ] folder.";
        String successMessage = String.format(successFormat,
                workbookSize,
                destinationWorkbookName,
                destinationDirWorkbook);
        String logHeaders = "Generating QR codes.. . %s" +
                "Destination folder: %s%s" +
                "Found %s rows from the list: %s";
        String logRow = "%s --- FILE SAVED %s";

        LogDialogWindow.displayLogDialog();
        LogDialogWindow.printToLogDialog(logHeaders, NEWLINE, destinationDirWorkbook,
                NEWLINE, workbookSize, NEWLINE);

        boolean writeToWorkbook = StringUtils.hasText(destinationDirWorkbook)
                && StringUtils.hasText(destinationWorkbookName);

        if (writeToWorkbook) {
            Workbook dstWorkbook = new XSSFWorkbook();
            Sheet sheet = dstWorkbook.createSheet("QR Code List");
            Font font = dstWorkbook.createFont();
            font.setBold(true);
            CellStyle cellStyle = dstWorkbook.createCellStyle();
            cellStyle.setWrapText(true);
            cellStyle.setFont(font);

            try {
                // Write sheet column headers
                writeSheetHeaders(sheet);

                int qrCodesRowCount = 1;
                for (Map.Entry<String, String> entry : fromWorkbook.entrySet()) {
                    BufferedImage bufferedImage = qrCodeEncoderService.generate(entry.getValue());
                    String qrCodeFileName = filenamePrefix + entry.getKey() +
                            "." + formatName;
                    // Write text data to rows
                    writeRowTextData(sheet, qrCodesRowCount,
                            entry.getValue(), qrCodeFileName,
                            cellStyle);
                    // Write QR code image to workbook
                    int qrCodeImagePicture = dstWorkbook.addPicture(bufferedImageToByteArray(bufferedImage),
                            Workbook.PICTURE_TYPE_JPEG);
                    writeRowQrCode(sheet, qrCodesRowCount, qrCodeImagePicture);
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
                dstWorkbook.write(fileOutputStream);
                fileOutputStream.close();
                dstWorkbook.close();
            } catch (IOException e) {
                throw new QrCodeBulkGenAppException(e);
            }
        }

        JOptionPane.showMessageDialog(null, successMessage, "QR Code Bulk Generator", JOptionPane.INFORMATION_MESSAGE);
    }

    private void writeSheetHeaders(Sheet sheet) {
        int headersCount = 0;
        Row headersRow = sheet.createRow(0); // Headers row is at 0 always
        for (String headers : colHeaderNames) {
            Cell headersRowCell = headersRow.createCell(headersCount);
            headersRowCell.setCellValue(headers.trim());
            sheet.setColumnWidth(headersCount, DEFAULT_COL_WIDTH);
            headersCount++;
        }
    }

    private void writeRowTextData(Sheet sheet, int rowIndex,
                                  String cell1Value, String cell2Value,
                                  CellStyle cellStyle) {
        Row qrCodeRow = sheet.createRow(rowIndex);
        qrCodeRow.setHeightInPoints(DEFAULT_ROW_HEIGHT);
        Cell cell1 = qrCodeRow.createCell(0); // At the 1st column
        cell1.setCellValue(cell1Value); // Value of the encoded string in the QR Code
        cell1.setCellStyle(cellStyle);
        Cell cell2 = qrCodeRow.createCell(1); // At the 2nd column
        cell2.setCellValue(cell2Value); // Filename of the QR Code JPEG image
    }

    private void writeRowQrCode(Sheet sheet, int rowIndex, int qrCodeImagePicture) {
        XSSFDrawing xssfDrawing = (XSSFDrawing) sheet.createDrawingPatriarch();
        XSSFClientAnchor xssfClientAnchor = new XSSFClientAnchor();
        xssfClientAnchor.setCol1(2); // Anchor at the 3rd column
        xssfClientAnchor.setCol2(3); // End anchor at the next column
        xssfClientAnchor.setRow1(rowIndex); // Anchor at this row inddex
        xssfClientAnchor.setRow2(rowIndex+1); // End anchor at the next row
        xssfDrawing.createPicture(xssfClientAnchor, qrCodeImagePicture);
    }

    private byte[] bufferedImageToByteArray(BufferedImage bufferedImage) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, formatName, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

}
