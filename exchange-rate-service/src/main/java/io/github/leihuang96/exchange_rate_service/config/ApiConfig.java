package io.github.leihuang96.exchange_rate_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "exchange-rate.api")
public class ApiConfig {

    private String baseUrl;
    private String key;

}
