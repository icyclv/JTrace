package com.second.jtrace.server.configuration;

import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Value("${influxdb.username:defaultUser}")
    private String username;

    @Value("${influxdb.password:defaultPassword}")
    private String password;

    @Value("${influxdb.database:defaultDatabase}")
    private String database;


    public String getDatabase() {
        return database;
    }




    @Bean
    @ConditionalOnProperty(name = "influxdb.enabled", havingValue = "true")
    public InfluxDB influxDBClient() {
        if(!enabled) {
            return null;
        }
        InfluxDB influxDB = InfluxDBFactory.connect(url, username, password);
       influxDB.enableBatch(BatchOptions.DEFAULTS);
        influxDB.setLogLevel(InfluxDB.LogLevel.BASIC);

        return influxDB;
    }

}
