/*
 * Copyright (C) 2019-2025 Linty Services
 * mailto:contact@linty-services.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.lintyservices.sonar.plugins.fpgametrics.sensor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metric.ValueType;

import java.util.List;

class MetricsImporterTest {

  @Test
  void should_find_the_proper_number_of_metrics() {
    List<Metric> metrics = new MetricsImporter().getMetrics();

    Assertions.assertEquals(116, metrics.size());
  }

  @Test
  void should_find_the_proper_details_for_each_metric() {
    List<Metric> metrics = new MetricsImporter().getMetrics();

    checkMetric(
      metrics.get(0),
      "NX_Log_Remarks",
      "NX logs remarks count",
      ValueType.INT,
      "NX Logs",
      "Number of remarks in NX log dataflow"
    );

    checkMetric(
      metrics.get(3),
      "NX_4LUT_PERCENT",
      "NX occupation 4-LUT",
      ValueType.PERCENT,
      "NX Occupation (%)",
      "Utilization of 4-LUT in the NX device"
    );

    checkMetric(
      metrics.get(27),
      "NX_CLK1_Max_Delay",
      "NX Timing CLK1 max delay (ns)",
      ValueType.FLOAT,
      "NX Timing Domain1",
      "Maximum data arrival time ns for clok1 domain"
    );
  }

  @Test
  void should_fail_while_loading_a_metric_without_type() {
    Exception thrown = Assertions.assertThrows(
      IllegalStateException.class,
      () -> new MetricsImporter().getMetricsFromJsonFile("src/test/resources/metrics/metric-with-no-type/format-metrics.json", "test"));

    Assertions.assertEquals("[FPGA Metrics] NX_Log_Remarks metric cannot be created since it is not properly formatted", thrown.getMessage());
  }

  private void checkMetric(Metric metric, String expectedKey, String expectedName, Metric.ValueType expectedType,
                           String expectedDomain, String expectedDescription) {
    Assertions.assertEquals(expectedKey, metric.getKey());
    Assertions.assertEquals(expectedName, metric.getName());
    Assertions.assertEquals(expectedType, metric.getType());
    Assertions.assertEquals(expectedDomain, metric.getDomain());
    Assertions.assertEquals(expectedDescription, metric.getDescription());
    Assertions.assertFalse(metric.getQualitative());
    Assertions.assertTrue(metric.getEnabled());
    Assertions.assertFalse(metric.getDeleteHistoricalData());
    Assertions.assertEquals(Integer.valueOf(0), metric.getDecimalScale());
  }
}
