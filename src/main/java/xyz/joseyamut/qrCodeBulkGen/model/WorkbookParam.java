package xyz.joseyamut.qrCodeBulkGen.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WorkbookParam {

    private int colMaxReadLimit = 3;
    private int colValueLenBeforeTruncate = 10;
    private int rowMaxReadLimit = 5;
    private List<String> colHeaderNames = new ArrayList<>();

}
