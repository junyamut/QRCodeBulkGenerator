package xyz.joseyamut.qrCodeBulkGen.service;

import io.nayuki.qrcodegen.QrCode;
import org.springframework.stereotype.Service;
import xyz.joseyamut.qrCodeBulkGen.QrCodeBulkGenAppException;
import xyz.joseyamut.qrCodeBulkGen.config.StoreImageConfiguration;

import java.awt.image.BufferedImage;

@Service
public class QrCodeEncoderService {

    private final StoreImageConfiguration storeImageConfiguration;

    public QrCodeEncoderService(StoreImageConfiguration storeImageConfiguration) {
        this.storeImageConfiguration = storeImageConfiguration;
    }

    public BufferedImage generate(String qrCodeText) {
        QrCode qrCode = QrCode.encodeText(qrCodeText, QrCode.Ecc.MEDIUM);

        return toImage(qrCode,
                storeImageConfiguration.getImageParam().getScale(),
                storeImageConfiguration.getImageParam().getBorder(),
                storeImageConfiguration.getImageParam().getLightColor(),
                storeImageConfiguration.getImageParam().getDarkColor());
    }

    private BufferedImage toImage(QrCode qrCode, int scale, int border, int lightColor, int darkColor) throws QrCodeBulkGenAppException {
        if (qrCode == null) {
            throw new QrCodeBulkGenAppException("QrCode object must have a non-null value!");
        }

        if (scale <= 0 || border < 0) {
            throw new QrCodeBulkGenAppException("Invalid values provided for scale and/or border!");
        }

        if (border > (Integer.MAX_VALUE / 2)
                || (qrCode.size + border * 2L) > (Integer.MAX_VALUE) / scale) {
            throw new QrCodeBulkGenAppException("Provided values for scale and/or border too large!");
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
