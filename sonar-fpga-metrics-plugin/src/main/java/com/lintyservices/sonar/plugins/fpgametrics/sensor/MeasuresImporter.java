/*
 * This confidential and proprietary software may be used only as authorized
 * by a licensing agreement from Linty Services.
 * (c) Copyright 2016-2026 Linty Services
 * ALL RIGHTS RESERVED
 * The entire notice above must be reproduced on all authorized copies.
 */
package com.lintyservices.sonar.plugins.fpgametrics.sensor;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.measures.Metric;
import org.sonar.api.scanner.sensor.ProjectSensor;

public class MeasuresImporter implements ProjectSensor {

  private static final Logger LOG = LoggerFactory.getLogger(MeasuresImporter.class);
  private Map<String, Metric> metrics;

  @Override
  public void describe(SensorDescriptor descriptor) {
    descriptor.name("Import custom FPGA measures from JSON files");
  }

  @Override
  public void execute(SensorContext context) {
    metrics = Maps.uniqueIndex(new MetricsImporter().getMetrics(), Metric::getKey);
    addAllMeasuresToProject(context);

    FileSystem fs = context.fileSystem();
    Iterable<InputFile> files =
        fs.inputFiles(
            fs.predicates()
                .and(
                    fs.predicates().hasLanguage("vhdl"),
                    fs.predicates().hasType(InputFile.Type.MAIN)));
    for (InputFile file : files) {
      addAllMeasuresToFile(context, file);
    }
  }

  private Map<String, Object> getMeasuresFromJsonFile(String filePath) {
    try {
      return new Gson().fromJson(new FileReader(filePath, StandardCharsets.UTF_8), Map.class);
    } catch (FileNotFoundException e) {
      LOG.debug("[FPGA Metrics] No measures report found: " + filePath);
      return Collections.emptyMap();
    } catch (IOException e) {
      throw new IllegalStateException(
          "[FPGA Metrics] Cannot parse JSON measures report: " + filePath, e);
    } catch (JsonSyntaxException | JsonIOException e) {
      throw new IllegalStateException(
          "[FPGA Metrics] Cannot parse JSON measures report: " + filePath);
    }
  }

  private void addAllMeasuresToProject(SensorContext context) {
    Map<String, Object> measures =
        getMeasuresFromJsonFile(
            context.fileSystem().baseDir().getPath() + File.separator + "measures.json");
    for (Map.Entry<String, Object> measure : measures.entrySet()) {
      addNewMeasure(context, null, getMetricFromKey(measure.getKey()), measure.getValue());
    }
  }

  private void addAllMeasuresToFile(SensorContext context, InputFile file) {
    Map<String, Object> measures =
        getMeasuresFromJsonFile(
            context.fileSystem().baseDir().getPath()
                + File.separator
                + stripExtension(file.filename())
                + "_measures.json");

    for (Map.Entry<String, Object> measure : measures.entrySet()) {
      addNewMeasure(context, file, getMetricFromKey(measure.getKey()), measure.getValue());
    }
  }

  private void addNewMeasure(
      SensorContext context, InputFile file, Metric metric, Object rawMeasure) {
    Serializable measure =
        getTypedMeasure(metric.getType().name(), getMeasure(rawMeasure), getRatioMax(rawMeasure));

    if (file != null) {
      context.newMeasure().forMetric(metric).on(file).withValue(measure).save();
    } else {
      context.newMeasure().forMetric(metric).on(context.project()).withValue(measure).save();
    }
  }

  private Object getMeasure(Object rawValue) {
    if (rawValue.getClass().equals(ArrayList.class)) {
      return ((ArrayList) rawValue).get(0);
    }
    return rawValue;
  }

  private Double getRatioMax(Object rawValue) {
    if (rawValue.getClass().equals(ArrayList.class)) {
      return (Double) ((ArrayList) rawValue).get(1);
    }
    return null;
  }

  private Metric getMetricFromKey(String metricKey) {
    Metric metric = metrics.get(metricKey);
    if (metric == null) {
      throw new IllegalStateException(
          "[FPGA Metrics] Metric with '" + metricKey + "' key cannot be found");
    }
    return metric;
  }

  private Serializable getTypedMeasure(String metricType, Object measureValue, Double ratioMax) {
    switch (metricType) {
      case "INT":
        return (int) Math.round((Double) measureValue);
      case "FLOAT":
        return (Double) measureValue;
      case "PERCENT":
        return ((Double) measureValue) * 100.0 / ratioMax;
      case "BOOL":
        return (Boolean) measureValue;
      case "STRING":
      case "DATA":
      case "DISTRIB":
        return (String) measureValue;
      case "MILLISEC":
      case "RATING":
      case "WORK_DUR":
        return Math.round((Double) measureValue);
      default:
        throw new IllegalStateException(
            "[FPGA Metrics] '" + metricType + "' metric type not recognized.");
    }
  }

  private static String stripExtension(String filename) {
    int lastDot = filename.lastIndexOf('.');
    return lastDot > 0 ? filename.substring(0, lastDot) : filename;
  }
}
