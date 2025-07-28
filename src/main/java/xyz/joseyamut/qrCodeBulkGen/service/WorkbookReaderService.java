package xyz.joseyamut.qrCodeBulkGen.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.joseyamut.qrCodeBulkGen.QrCodeBulkGenAppException;
import xyz.joseyamut.qrCodeBulkGen.model.DataStore;
import xyz.joseyamut.qrCodeBulkGen.model.WorkbookParam;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
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
    private final int colMaxReadLimit;
    private final int colValueLenBeforeTruncate;
    private final int rowMaxReadLimit;

    public static final String NEWLINE = System.lineSeparator();

    public WorkbookReaderService(DataStore dataStore, WorkbookParam workbookParam) {
        this.workbookName = dataStore.getSrcWorkbookName();
        this.sourceDir = dataStore.getSrcDir();
        this.colMaxReadLimit = workbookParam.getColMaxReadLimit();
        this.colValueLenBeforeTruncate = workbookParam.getColValueLenBeforeTruncate();
        this.rowMaxReadLimit = workbookParam.getRowMaxReadLimit();
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
                int rowLimit;
                int firstRow = sheet.getFirstRowNum();
                int lastRow = rowLimit = sheet.getLastRowNum();

                if (lastRow < 0) break; // Value of -1, no rows exist in the sheet.
                if (rowMaxReadLimit <= lastRow ) { // Only follow rowMaxReadLimit if it is less than lastRow to avoid NPE.
                    rowLimit = rowMaxReadLimit;    // Otherwise, rowLimit == lastRow always.
                }

                for (int index = firstRow + 1; index <= rowLimit; index++) { // Read up to set maximum rows only.
                    Row row = sheet.getRow(index);
                    Cell nameCell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String nameValue = getCellValue(nameCell).replaceAll("\\s", "");

                    if (!StringUtils.hasText(nameValue)) {
                        continue;
                    }

                    for (int cellIndex = row.getFirstCellNum(); cellIndex < colMaxReadLimit; cellIndex++) { // Read up to set maximum columns only.
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
                value += cell.getStringCellValue();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    value += cell.getDateCellValue();
                } else {
                    // If cell is formatted as Numeric this will get string value without the scientific notation
                    value += BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();
                }
                break;
            case BOOLEAN:
            default:
                value += " " + NEWLINE;
        }

        // Truncate only if extracted value is greater than truncation value.
        if (value.length() > colValueLenBeforeTruncate) {
            return value.substring(0, colValueLenBeforeTruncate) + NEWLINE;
        }

        return value + NEWLINE;
    }

}
