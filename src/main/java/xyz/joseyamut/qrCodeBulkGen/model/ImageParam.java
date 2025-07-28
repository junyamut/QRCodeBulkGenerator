package xyz.joseyamut.qrCodeBulkGen.model;

import lombok.Data;

@Data
public class ImageParam {

    private String formatName;
    private String filenamePrefix;
    private int scale;
    private int border;
    private int lightColor;
    private int darkColor;

}
