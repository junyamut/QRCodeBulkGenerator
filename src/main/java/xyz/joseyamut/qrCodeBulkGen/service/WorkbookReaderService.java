package xyz.joseyamut.qrCodeBulkGen.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


@Slf4j
@Service
public class WorkbookReaderService {

    private final String workbookName;
    private final String sourceDir;
    private final String destinationDir;

    private static final String NEWLINE = System.lineSeparator();

    public WorkbookReaderService(String workbookName, String sourceDir, String destinationDir) {
        this.workbookName = workbookName;
        this.sourceDir = sourceDir;
        this.destinationDir = destinationDir;
    }

    @PostConstruct
    public void init() throws IOException {
        log.info("Workbook Name: {}", workbookName);
        log.info("Source Dir: {}", sourceDir);
        log.info("Destination Dir: {}", destinationDir);

        log.info("{}", readExcel(sourceDir, workbookName));

    }

    public static String readExcel(String filePath, String fileName) throws IOException {
        File file = new File(filePath, fileName);
        FileInputStream fileInputStream;
        StringBuilder toReturn = new StringBuilder();
        try {
            fileInputStream = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(fileInputStream);
            for (Sheet sheet : workbook) {
                toReturn.append(NEWLINE);
                toReturn.append("Reading at worksheet: ")
                        .append(sheet.getSheetName())
                        .append(NEWLINE);
                toReturn.append(NEWLINE);

                int firstRow = sheet.getFirstRowNum();
                int lastRow = sheet.getLastRowNum();

                for (int index = firstRow + 1; index <= lastRow; index++) {
                    Row row = sheet.getRow(index);

                    for (int cellIndex = row.getFirstCellNum(); cellIndex < row.getLastCellNum(); cellIndex++) {
                        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        printCellValue(cell, toReturn);
                    }
                    toReturn.append(NEWLINE);
                }
            }
            fileInputStream.close();
            workbook.close();

        } catch (IOException e) {
            throw e;
        }

        return toReturn.toString();
    }

    private static void printCellValue(Cell cell, StringBuilder toReturn) {
        CellType cellType = cell.getCellType()
                .equals(CellType.FORMULA) ? cell.getCachedFormulaResultType() : cell.getCellType();

        switch (cellType) {
            case STRING:
                toReturn.append(cell.getStringCellValue()).append(NEWLINE);
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    toReturn.append(cell.getDateCellValue()).append(NEWLINE);;
                } else {
                    toReturn.append(cell.getNumericCellValue()).append(NEWLINE);;
                }
                break;
            case BOOLEAN:
            default:
                toReturn.append(" ").append(NEWLINE);;
        }
    }

}
