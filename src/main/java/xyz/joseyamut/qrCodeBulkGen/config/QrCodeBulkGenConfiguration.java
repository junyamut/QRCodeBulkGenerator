package xyz.joseyamut.qrCodeBulkGen.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.joseyamut.qrCodeBulkGen.service.WorkbookReaderService;

@Configuration
public class QrCodeBulkGenConfiguration {

    @Autowired
    private DataStoreConfiguration dataStoreConfiguration;

    @Bean
    public WorkbookReaderService workbookReaderService() {
        return new WorkbookReaderService(dataStoreConfiguration.workbookName,
                dataStoreConfiguration.sourceDir,
                dataStoreConfiguration.destinationDir);
    }

}
