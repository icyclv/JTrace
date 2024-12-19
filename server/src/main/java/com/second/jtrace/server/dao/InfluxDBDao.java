package com.second.jtrace.server.dao;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.second.jtrace.core.protocol.GsonSerializer;
import com.second.jtrace.core.sampling.bean.SamplingMessage;
import com.second.jtrace.server.configuration.InfluxDBConfiguration;
import io.micrometer.core.instrument.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Repository
public class InfluxDBDao {
    private static final Logger logger = LoggerFactory.getLogger(InfluxDBDao.class);
    private final InfluxDBClient influxDBClient;


    @Autowired
    public InfluxDBDao(InfluxDBClient influxDBClient ) {
        this.influxDBClient = influxDBClient;


    }

    public void writeSampling(SamplingMessage samplingMessage) {
        try {
            if (influxDBClient == null) {
                logger.error("InfluxDB client is null");
                return;
            }

            String profilerName = samplingMessage.getProfilerName();
            Map<String, Object> metrics = samplingMessage.getResult();
            Map<String, Object> formattedMetrics;
            if(samplingMessage.getProfilerName().equals("Stacktrace")){
                // stacktrace直接保存字符串，用于后续生成火焰图等，不需要解析为数值类型
                formattedMetrics = getJsonFormattedMetrics(metrics);
            }else{
                formattedMetrics = getFormattedMetrics(metrics);
            }

            // Prepare write API
            WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

            // Create Point
            Point point = Point.measurement(profilerName)
                    .time(Instant.now(), WritePrecision.MS)
                    .addFields(formattedMetrics)
                    .addTag("clientId", (String) metrics.get("clientId"));

            writeApi.writePoint(point);
        }catch (Exception e){
            logger.error("Error while writing sampling data to InfluxDB", e);
        }
    }

    private Map<String,Object> getJsonFormattedMetrics(Map<String,Object> metrics){
        Map<String,Object> formattedMetrics = new HashMap<>();
        for(Map.Entry<String,Object> entry: metrics.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof List || value instanceof Map) {
                formattedMetrics.put(key, GsonSerializer.toJson(value));
            } else {
                formattedMetrics.put(key, value);
            }
        }
            return formattedMetrics;

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
