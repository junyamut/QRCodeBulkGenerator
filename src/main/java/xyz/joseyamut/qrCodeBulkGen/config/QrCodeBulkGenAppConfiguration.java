package xyz.joseyamut.qrCodeBulkGen.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class QrCodeBulkGenAppConfiguration {

    private StoreImageConfiguration storeImageConfiguration;

    @Autowired
    public void setStoreImageConfiguration(StoreImageConfiguration storeImageConfiguration) {
        this.storeImageConfiguration = storeImageConfiguration;
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
