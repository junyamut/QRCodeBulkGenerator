package xyz.joseyamut.qrCodeBulkGen.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.joseyamut.qrCodeBulkGen.QrCodeBulkGenAppException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class WorkbookReaderService {

    private final String workbookName;
    private final String sourceDir;

    public static final String NEWLINE = System.lineSeparator();

    public WorkbookReaderService(String workbookName, String sourceDir) {
        this.workbookName = workbookName;
        this.sourceDir = sourceDir;
    }

    public Map<String, String> getListFromWorkbook() throws QrCodeBulkGenAppException {
        Path path = Paths.get(sourceDir + File.separator + workbookName);
        if (!Files.exists(path)) {
            throw new QrCodeBulkGenAppException("Can't find list!" + NEWLINE +
                    NEWLINE +
                    "Make sure to save the Excel workbook" + NEWLINE +
                    "   AT location: " + sourceDir + NEWLINE +
                    "   WITH filename: " + workbookName);
        }

        Map<String, String> rowMap = new HashMap<>();
        StringBuilder rowValue = new StringBuilder();

        try {
            File file = new File(sourceDir, workbookName);
            FileInputStream fileInputStream = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(fileInputStream);

            for (Sheet sheet : workbook) {
                int firstRow = sheet.getFirstRowNum();
                int lastRow = sheet.getLastRowNum();

                for (int index = firstRow + 1; index <= lastRow; index++) {
                    Row row = sheet.getRow(index);
                    Cell nameCell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String nameValue = getCellValue(nameCell).replaceAll("\\s", "");

                    if (!StringUtils.hasText(nameValue)) {
                        continue;
                    }

                    for (int cellIndex = row.getFirstCellNum(); cellIndex < row.getLastCellNum(); cellIndex++) {
                        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        rowValue.append(getCellValue(cell));
                    }
                    rowMap.put(nameValue.trim(), rowValue.toString().trim());
                    rowValue.setLength(0);
                }
            }

            fileInputStream.close();
            workbook.close();
        } catch (IOException e) {
            throw new QrCodeBulkGenAppException(e);
        }

        return rowMap;
    }

    private String getCellValue(Cell cell) {
        String value = "";
        CellType cellType = cell.getCellType()
                .equals(CellType.FORMULA) ? cell.getCachedFormulaResultType() : cell.getCellType();

        switch (cellType) {
            case STRING:
                value += cell.getStringCellValue() + NEWLINE;
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    value += cell.getDateCellValue() + NEWLINE;
                } else {
                    value += cell.getNumericCellValue() + NEWLINE;
                }
                break;
            case BOOLEAN:
            default:
                value += " " + NEWLINE;
        }

        return value;
    }

}
