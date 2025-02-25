package xyz.joseyamut.qrCodeBulkGen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QrCodeBulkGenApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(QrCodeBulkGenApplication.class);

	public static void main(String[] args) {
		log.info("RUNNING application...");

		SpringApplication.run(QrCodeBulkGenApplication.class, args);

		log.info("EXITING application.");
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("EXECUTING  command line runner");

		for (int i = 0; i < args.length; ++i) {
			log.info("args [{}]: {}", i, args[i]);
		}
	}
}
