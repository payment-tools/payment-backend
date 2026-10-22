package com.sn.onepay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.ZoneOffset;
import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class OnePayBackendApplication {

	public static void main(String[] args) {
		useUtcTimeZone();
		SpringApplication.run(OnePayBackendApplication.class, args);
	}

	/**
	 * Toutes les dates (paiement, création, modification, audit) sont en UTC,
	 * quel que soit le pays d'hébergement du serveur ; les clients les
	 * convertissent en heure locale à l'affichage.
	 */
	static void useUtcTimeZone() {
		TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
	}

}
