package xyz.joseyamut.qrCodeBulkGen.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "data-store")
public class DataStoreConfiguration {

    public String workbookName;
    public String sourceDir;
    public String destinationDir;

}
