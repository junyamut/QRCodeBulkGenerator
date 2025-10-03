package xyz.joseyamut.qrCodeBulkGen.service;

import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class WorkbookWriterService {

    private Workbook workbook;
    private Sheet sheet;
    @Getter
    @Setter
    private Font font;
    @Getter
    @Setter
    private CellStyle cellStyle;
    // Adjust row height and column width, so QR codes are not scaled too small
    private static final int DEFAULT_COL_WIDTH = 5120 * 2;
    private static final float DEFAULT_ROW_HEIGHT = 200f;

    protected void createWorkbookWithInitialSheet() {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet();
    }

    protected void setNameOfSheetAtIndex(int index, String name) {
        workbook.setSheetName(index, name);
    }

    protected int addPicture(byte[] byteArray, int pictureType) {
        return workbook.addPicture(byteArray, pictureType);
    }

    protected void createFont() {
        font = workbook.createFont();
    }

    protected void createCellStyle() {
        cellStyle = workbook.createCellStyle();
    }

    protected void write(FileOutputStream fileOutputStream) throws IOException {
        workbook.write(fileOutputStream);
    }

    protected void close() throws IOException {
        workbook.close();
    }

    protected void writeSheetHeaders(List<String> colHeaderNames) {
        int headersCount = 0;
        Row headersRow = sheet.createRow(0); // Headers row is at 0 always
        for (String headers : colHeaderNames) {
            Cell headersRowCell = headersRow.createCell(headersCount);
            headersRowCell.setCellValue(headers.trim());
            sheet.setColumnWidth(headersCount, DEFAULT_COL_WIDTH);
            headersCount++;
        }
    }

    protected void writeRowTextData(int rowIndex,
                                  String cell1Value, String cell2Value) {
        Row qrCodeRow = sheet.createRow(rowIndex);
        qrCodeRow.setHeightInPoints(DEFAULT_ROW_HEIGHT);
        Cell cell1 = qrCodeRow.createCell(0); // At the 1st column
        cell1.setCellValue(cell1Value); // Value of the encoded string in the QR Code
        cell1.setCellStyle(cellStyle);
        Cell cell2 = qrCodeRow.createCell(1); // At the 2nd column
        cell2.setCellValue(cell2Value); // Filename of the QR Code JPEG image
    }

    protected void writeRowQrCode(int rowIndex, int qrCodeImagePicture) {
        XSSFDrawing xssfDrawing = (XSSFDrawing) sheet.createDrawingPatriarch();
        XSSFClientAnchor xssfClientAnchor = new XSSFClientAnchor();
        xssfClientAnchor.setCol1(2); // Anchor at the 3rd column
        xssfClientAnchor.setCol2(3); // End anchor at the next column
        xssfClientAnchor.setRow1(rowIndex); // Anchor at this row inddex
        xssfClientAnchor.setRow2(rowIndex + 1); // End anchor at the next row
        xssfDrawing.createPicture(xssfClientAnchor, qrCodeImagePicture);
    }
}
