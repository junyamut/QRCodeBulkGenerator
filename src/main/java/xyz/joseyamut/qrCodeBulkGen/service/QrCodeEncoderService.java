package xyz.joseyamut.qrCodeBulkGen.service;

import io.nayuki.qrcodegen.QrCode;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

@Service
public class QrCodeEncoderService {

    private final int scale;
    private final int border;
    private final int lightColor;
    private final int darkColor;

    public QrCodeEncoderService(int scale, int border, int lightColor, int darkColor) {
        this.scale = scale;
        this.border = border;
        this.lightColor = lightColor;
        this.darkColor = darkColor;
    }

    public BufferedImage generate(String qrCodeText) {
        QrCode qrCode = QrCode.encodeText(qrCodeText, QrCode.Ecc.MEDIUM);

        return toImage(qrCode,
                scale,
                border,
                lightColor,
                darkColor);
    }

    private BufferedImage toImage(QrCode qrCode, int scale, int border, int lightColor, int darkColor) {
        if (qrCode == null) {
            throw new IllegalArgumentException("QrCode object must have a non-null value!");
        }

        if (scale <= 0 || border < 0) {
            throw new IllegalArgumentException("Invalid values provided for scale and/or border!");
        }

        if (border > (Integer.MAX_VALUE / 2)
                || (qrCode.size + border * 2L) > (Integer.MAX_VALUE) / scale) {
            throw new IllegalArgumentException("Provided values for scale and/or border too large!");
        }

        BufferedImage bufferedImage = new BufferedImage((qrCode.size + border * 2) * scale,
                (qrCode.size + border * 2) * scale,
                BufferedImage.TYPE_INT_RGB
        );

        for (int ycoordinate = 0; ycoordinate < bufferedImage.getHeight(); ycoordinate++) {
            for (int xcoordinate = 0; xcoordinate < bufferedImage.getWidth(); xcoordinate++) {
                boolean color = qrCode.getModule(xcoordinate / scale - border,
                        ycoordinate / scale - border);
                bufferedImage.setRGB(xcoordinate,
                        ycoordinate,
                        color ? darkColor : lightColor);
            }
        }

        return bufferedImage;
    }
}
