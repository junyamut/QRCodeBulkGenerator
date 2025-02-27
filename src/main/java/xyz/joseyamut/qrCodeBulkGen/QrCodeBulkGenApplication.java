package xyz.joseyamut.qrCodeBulkGen;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import javax.swing.*;

@SpringBootApplication
public class QrCodeBulkGenApplication {

    public static void main(String[] args) {
		try {
			SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder(QrCodeBulkGenApplication.class);
			springApplicationBuilder.headless(false).run(args);
		} catch (Exception e) {
			String rootCause = ExceptionUtils.getRootCauseMessage(e);
			JOptionPane.showMessageDialog(null, rootCause, "QR Code Bulk Generator - Error", JOptionPane.WARNING_MESSAGE);
		}
	}

}
