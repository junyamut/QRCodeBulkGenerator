package xyz.joseyamut.qrCodeBulkGen;

import java.io.Serial;

public class QrCodeBulkGenAppException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -986744057436730741L;

    public QrCodeBulkGenAppException(String message, Throwable cause) {
        super(message, cause);
    }

    public QrCodeBulkGenAppException(String message) {
        super(message);
    }

    public QrCodeBulkGenAppException(Throwable cause) {
        super(cause);
    }

}
