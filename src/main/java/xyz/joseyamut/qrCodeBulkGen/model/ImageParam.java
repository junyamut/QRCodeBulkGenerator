package xyz.joseyamut.qrCodeBulkGen.model;

import lombok.Data;

@Data
public class ImageParam {

    public String formatName;
    public String filenamePrefix;
    public int scale;
    public int border;
    public int lightColor;
    public int darkColor;

}
