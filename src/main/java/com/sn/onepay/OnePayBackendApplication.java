package com.sn.onepay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class OnePayBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnePayBackendApplication.class, args);
	}

}
