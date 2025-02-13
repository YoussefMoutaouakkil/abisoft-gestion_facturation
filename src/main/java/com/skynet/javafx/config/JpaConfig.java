package com.skynet.javafx.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "com.skynet.javafx.model")
@EnableJpaRepositories(basePackages = "com.skynet.javafx.repository")
public class JpaConfig {
}
