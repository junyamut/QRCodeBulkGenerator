package xyz.joseyamut.qrCodeBulkGen;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import javax.swing.*;

@Slf4j
@SpringBootApplication
public class QrCodeBulkGenApplication {

    public static void main(String[] args) {
		try {
			SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder(QrCodeBulkGenApplication.class);
			springApplicationBuilder.headless(false).run(args);
		} catch (Exception e) {
			String rootCause = ExceptionUtils.getRootCauseMessage(e);
			log.error("Unexpected error encountered: {}", rootCause);
			JOptionPane.showMessageDialog(null, rootCause, "QR Code Bulk Generator", JOptionPane.WARNING_MESSAGE);
		}
	}

}
