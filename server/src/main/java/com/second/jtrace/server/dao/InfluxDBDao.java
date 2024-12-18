package com.second.jtrace.server.dao;

import com.second.jtrace.core.sampling.bean.SamplingMessage;
import com.second.jtrace.server.configuration.InfluxDBConfiguration;
import io.micrometer.core.instrument.util.StringUtils;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Repository
public class InfluxDBDao {
    private static final Logger logger = LoggerFactory.getLogger(InfluxDBDao.class);
    private final InfluxDB influxDB;

    @Autowired
    private InfluxDBConfiguration influxDBConfiguration = null;

    @Autowired
    public InfluxDBDao(InfluxDB influxDB) {
        this.influxDB = influxDB;
    }

    public void writeSampling(SamplingMessage samplingMessage) {
        if(influxDB == null){
            logger.error("InfluxDB is null");
            return;
        }
        String profilerName = samplingMessage.getProfilerName();
        Map<String, Object> metrics = samplingMessage.getResult();
        Map<String, Object> formattedMetrics = getFormattedMetrics(metrics);

        // Point
        Point point = Point.measurement(profilerName)
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .fields(formattedMetrics)
                .tag("clientId", (String)metrics.get("clientId"))
                .build();
        // BatchPoints
        BatchPoints batchPoints = BatchPoints.database(influxDBConfiguration.getDatabase())
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .retentionPolicy("autogen")
                .build();
        batchPoints.point(point);
        // Write
        this.influxDB.write(batchPoints);
    }


    private Map<String, Object> getFormattedMetrics(Map<String, Object> metrics) {
        Map<String, Object> formattedMetrics = new HashMap<>();
        for (Map.Entry<String, Object> entry : metrics.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            logger.info("Raw Metric-Name = " + key + ", Metric-Value = " + value);
            if (value instanceof List) {
                List listValue = (List) value;
                if (!listValue.isEmpty() && listValue.get(0) instanceof String) {
                    List<String> metricList = (List<String>) listValue;
                    formattedMetrics.put(key, String.join(",", metricList));
                } else if (!listValue.isEmpty() && listValue.get(0) instanceof Map) {
                    List<Map<String, Object>> metricList = (List<Map<String, Object>>) listValue;
                    int num = 1;
                    for (Map<String, Object> metricMap : metricList) {
                        String name = null;
                        if(metricMap.containsKey("name") && metricMap.get("name") != null && metricMap.get("name") instanceof String){
                            name = (String) metricMap.get("name");
                            name = name.replaceAll("\\s", "");
                        }
                        for (Map.Entry<String, Object> entry1 : metricMap.entrySet()) {
                            if(StringUtils.isNotEmpty(name)){
                                formattedMetrics.put(key + "-" + name + "-" + entry1.getKey(), entry1.getValue());
                            }else{
                                formattedMetrics.put(key + "-" + entry1.getKey() + "-" + num, entry1.getValue());
                            }
                        }
                        num++;
                    }
                }
            } else if (value instanceof Map) {
                Map<String, Object> metricMap = (Map<String, Object>) value;
                for (Map.Entry<String, Object> entry1 : metricMap.entrySet()) {
                    String key1 = entry1.getKey();
                    Object value1 = entry1.getValue();
                    if (value1 instanceof Map) {
                        Map<String, Object> value2 = (Map<String, Object>) value1;
                        int num = 1;
                        for (Map.Entry<String, Object> entry2 : value2.entrySet()) {
                            formattedMetrics.put(key + "-" + key1 + "-" + entry2.getKey() + "-" + num, entry2.getValue());
                        }
                        num++;
                    }
                }
            } else {
                formattedMetrics.put(key, value);
            }
        }
        return formattedMetrics;
    }
}
