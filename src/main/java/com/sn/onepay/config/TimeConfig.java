package com.sn.onepay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/*Horloge de l'application, en UTC (heure de Dakar) : injectable pour figer la date dans les tests*/
@Configuration
public class TimeConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
