/*
 * This confidential and proprietary software may be used only as authorized
 * by a licensing agreement from Linty Services.
 * (c) Copyright 2016-2026 Linty Services
 * ALL RIGHTS RESERVED
 * The entire notice above must be reproduced on all authorized copies.
 */
package com.lintyservices.sonar.plugins.fpgametrics.sensor;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import com.lintyservices.sonar.plugins.fpgametrics.gsondata.JsonMetric;
import com.lintyservices.sonar.plugins.fpgametrics.gsondata.JsonMetrics;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metric.ValueType;
import org.sonar.api.measures.Metrics;

public class MetricsImporter implements Metrics {

  @Override
  public List<Metric> getMetrics() {
    return getMetricsFromJsonFile("fpgametrics/format-metrics.json", "production");
  }

  @VisibleForTesting
  List<Metric> getMetricsFromJsonFile(String jsonFilePath, String type) {
    InputStreamReader inputStreamReader;
    if ("test".equals(type)) {
      try {
        inputStreamReader = new FileReader(jsonFilePath, StandardCharsets.UTF_8);
      } catch (IOException e) {
        throw new IllegalStateException("[FPGA Metrics] Cannot find JSON metrics file", e);
      }
    } else {
      inputStreamReader =
          new InputStreamReader(
              getClass().getClassLoader().getResourceAsStream(jsonFilePath),
              StandardCharsets.UTF_8);
    }
    JsonMetrics jsonMetrics = new Gson().fromJson(inputStreamReader, JsonMetrics.class);

    List<Metric> metrics = new ArrayList<>();
    for (Map.Entry<String, JsonMetric> metric : jsonMetrics.metrics().entrySet()) {
      try {
        metrics.add(convertToSonarQubeMetric(metric));
      } catch (Exception e) {
        throw new IllegalStateException(
            "[FPGA Metrics] "
                + metric.getKey()
                + " metric cannot be created since it is not properly formatted",
            e);
      }
    }
    return metrics;
  }

  private Metric convertToSonarQubeMetric(Map.Entry<String, JsonMetric> metric) {
    String key = metric.getKey();
    JsonMetric value = metric.getValue();
    return new Metric.Builder(key, value.getName(), ValueType.valueOf(value.getType()))
        .setDescription(value.getDescription())
        .setDirection(value.getDirection())
        .setQualitative(value.isQualitative())
        .setDomain(value.getDomain())
        .setWorstValue(value.getWorstValue())
        .setBestValue(value.getBestValue())
        .setOptimizedBestValue(value.isOptimizedBestValue())
        .setDecimalScale(value.getDecimalScale())
        .setDeleteHistoricalData(value.isDeleteHistoricalData())
        .setHidden(value.isHidden())
        .create();
  }
}
