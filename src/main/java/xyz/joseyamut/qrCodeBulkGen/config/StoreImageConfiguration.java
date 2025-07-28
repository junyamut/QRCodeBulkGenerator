package xyz.joseyamut.qrCodeBulkGen.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import xyz.joseyamut.qrCodeBulkGen.model.DataStore;
import xyz.joseyamut.qrCodeBulkGen.model.ImageParam;
import xyz.joseyamut.qrCodeBulkGen.model.WorkbookParam;

@Data
@Configuration
@ConfigurationProperties(prefix = "qr-code")
public class StoreImageConfiguration {

    private DataStore dataStore;
    private ImageParam imageParam;
    private WorkbookParam workbookParam;

}
