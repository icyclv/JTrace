package com.second.jtrace.server.configuration;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "influxdb")
public class InfluxDBConfiguration {


    @Value("${influxdb.enabled:false}")
    private boolean enabled;

    @Value("${influxdb.url:http://localhost:8086}")
    private String url;

    @Value("${influxdb.token}")
    private String token;

    @Value("${influxdb.org:test}")
    private String org;

    @Value("${influxdb.bucket:test}")
    private String bucket;


    public String getOrg() {
        return org;
    }

   public String getBucket() {
        return bucket;
    }

    @Bean
    @ConditionalOnProperty(name = "influxdb.enabled", havingValue = "true")
    public InfluxDBClient influxDBClient() {
        if(!enabled) {
            return null;
        }

        return InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
    }

}
