package xyz.joseyamut.qrCodeBulkGen.config;

import jakarta.annotation.PostConstruct;
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

@Configuration
public class QrCodeBulkGenAppConfiguration {

    @Autowired
    private StoreImageConfiguration storeImageConfiguration;

    @Bean
    public WorkbookReaderService workbookReaderService() {
        return new WorkbookReaderService(storeImageConfiguration.getDataStore(),
                storeImageConfiguration.getWorkbookParam());
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
        return new QrCodeGeneratorService(storeImageConfiguration.getDataStore(),
                storeImageConfiguration.getImageParam(),
                storeImageConfiguration.getWorkbookParam());
    }

    @PostConstruct
    public void dirCheck() {
        String sourceDir = storeImageConfiguration.getDataStore().getSrcDir();
        String destinationDirWorkbook = storeImageConfiguration.getDataStore().getDstDirWorkbook();
        String destinationDirImg = destinationDirWorkbook + storeImageConfiguration.getDataStore().getDstDirImg();
        Path qrCodeTextSourceDir = Paths.get(sourceDir);
        Path qrCodeOutputDirImg = Paths.get(destinationDirImg);
        Path qrCodeOutputDirWorkbook = Paths.get(destinationDirWorkbook);

        if (!Files.exists(qrCodeTextSourceDir)) {
            File createDir = new File(sourceDir);
            createDir.mkdirs();
        }

        if (!Files.exists(qrCodeOutputDirImg)) {
            File createDir = new File(destinationDirImg);
            createDir.mkdirs();
        }

        if (!Files.exists(qrCodeOutputDirWorkbook)) {
            File createDir = new File(destinationDirWorkbook);
            createDir.mkdirs();
        }
    }

}
