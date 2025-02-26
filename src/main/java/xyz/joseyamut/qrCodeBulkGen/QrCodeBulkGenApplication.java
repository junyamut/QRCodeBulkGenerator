package xyz.joseyamut.qrCodeBulkGen;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@Slf4j
@SpringBootApplication
public class QrCodeBulkGenApplication {

    public static void main(String[] args) {
		SpringApplication.run(QrCodeBulkGenApplication.class, args);

		log.info("EXITING application.");
	}

}
