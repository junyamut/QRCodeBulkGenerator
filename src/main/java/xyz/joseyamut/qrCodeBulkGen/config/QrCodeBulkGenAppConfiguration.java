package xyz.joseyamut.qrCodeBulkGen.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.joseyamut.qrCodeBulkGen.service.QrCodeEncoderService;
import xyz.joseyamut.qrCodeBulkGen.service.QrCodeGeneratorService;
import xyz.joseyamut.qrCodeBulkGen.service.WorkbookReaderService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Configuration
public class QrCodeBulkGenAppConfiguration {

    @Autowired
    private StoreImageConfiguration storeImageConfiguration;

    @Bean
    public WorkbookReaderService workbookReaderService() {
        return new WorkbookReaderService(storeImageConfiguration.getDataStore().getWorkbookName(),
                storeImageConfiguration.getDataStore().getSourceDir());
    }

    @Bean
    public QrCodeEncoderService qrCodeEncoderService() {
        return new QrCodeEncoderService(storeImageConfiguration.getImageParam().getScale(),
                storeImageConfiguration.getImageParam().getBorder(),
                storeImageConfiguration.getImageParam().getLightColor(),
                storeImageConfiguration.getImageParam().getDarkColor());
    }

    @Bean
    public QrCodeGeneratorService qrCodeGeneratorService() {
        return new QrCodeGeneratorService(storeImageConfiguration.getDataStore().getDestinationDir(),
                storeImageConfiguration.getImageParam().getFilenamePrefix(),
                storeImageConfiguration.getImageParam().getFormatName());
    }

    @PostConstruct
    public void dirCheck() {
        String destinationDir = storeImageConfiguration.getDataStore().getDestinationDir();
        Path qrCodeOutputDir = Paths.get(destinationDir);
        if (!Files.exists(qrCodeOutputDir)) {
            File createDir = new File(destinationDir);
            if (createDir.mkdirs()) {
                log.info("Created destination dir: {}", destinationDir);
            }
        }
    }

}
