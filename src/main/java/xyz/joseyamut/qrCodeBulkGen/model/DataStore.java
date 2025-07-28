package xyz.joseyamut.qrCodeBulkGen.model;

import lombok.Data;

@Data
public class DataStore {

    private String srcWorkbookName;
    private String dstWorkbookName;
    private String srcDir;
    private String dstDirImg;
    private String dstDirWorkbook;
    private boolean qrCodeToFile = false;

}
