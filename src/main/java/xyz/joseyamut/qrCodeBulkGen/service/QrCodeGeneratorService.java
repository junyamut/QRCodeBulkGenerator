package xyz.joseyamut.qrCodeBulkGen.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.joseyamut.qrCodeBulkGen.QrCodeBulkGenAppException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class QrCodeGeneratorService {

    @Autowired
    private WorkbookReaderService workbookReaderService;

    @Autowired
    private QrCodeEncoderService qrCodeEncoderService;

    private final String filenamePrefix;
    private final String destinationDir;

    public QrCodeGeneratorService(String destinationDir, String filenamePrefix) {
        this.destinationDir = destinationDir;
        this.filenamePrefix = filenamePrefix;
    }

    @PostConstruct
    private void generateFromList() {
        Map<String, String> fromWorkbook = workbookReaderService.getListFromWorkbook();

        assert fromWorkbook != null;
        log.info("Found {} rows from the list.", fromWorkbook.size());

        fromWorkbook.forEach((key, value) -> {
            log.debug("Key: {}, Value: {}", key, value);

            try {
                BufferedImage bufferedImage = qrCodeEncoderService.generate(value);

                File outputfile = new File(destinationDir + filenamePrefix + key + ".jpg");

                ImageIO.write(bufferedImage, "jpg", outputfile);
            } catch (IOException e) {
                throw new QrCodeBulkGenAppException(e);
            }
        });
    }

}
