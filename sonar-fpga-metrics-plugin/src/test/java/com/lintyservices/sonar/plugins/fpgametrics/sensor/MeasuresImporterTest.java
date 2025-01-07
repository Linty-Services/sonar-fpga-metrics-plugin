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
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.batch.sensor.measure.Measure;

import java.io.File;

class MeasuresImporterTest {

  @Test
  void should_properly_load_project_measures() {
    SensorContextTester contextTester = loadMeasuresFromPath("src/test/resources/measures/valid/");

    Measure<Integer> intMeasure = contextTester.measure(contextTester.module().key(), "NX_Log_Remarks");
    Measure<Double> floatMeasure = contextTester.measure(contextTester.module().key(), "NX_CLK1_Max_Delay");
    Measure<Double> percentage = contextTester.measure(contextTester.module().key(), "NX_4LUT_PERCENT");

    Assertions.assertEquals(1, intMeasure.value());
    Assertions.assertEquals(Double.valueOf(54.385), floatMeasure.value());
    Assertions.assertEquals(Double.valueOf(16636.0 * 100.0 / 129024.0), percentage.value());
  }

  // TODO
  //@Test
  // void should_properly_load_file_measures() {
  //}

  @Test
  void should_log_an_info_message_stating_that_json_measures_file_does_not_exist() {
    loadMeasuresFromPath("src/test/resources/measures/does-not-exist/");
    // TODO: Try to catch log message
  }

  @Test
  void should_throw_an_exception_with_an_invalid_json_file() {
    Exception thrown = Assertions.assertThrows(
      IllegalStateException.class,
      () -> loadMeasuresFromPath("src/test/resources/measures/invalid/")
    );
    Assertions.assertEquals("[FPGA Metrics] Cannot parse JSON measures report: src"
        + File.separator + "test" + File.separator + "resources" + File.separator + "measures"
        + File.separator + "invalid" + File.separator + "measures.json",
      thrown.getMessage());
  }

  @Test
  void should_throw_an_exception_while_an_unknown_metric_is_found_in_measures_json_file() {
    Exception thrown = Assertions.assertThrows(
      IllegalStateException.class,
      () -> loadMeasuresFromPath("src/test/resources/measures/unknown-metric/")
    );
    Assertions.assertEquals("[FPGA Metrics] Metric with 'UNKNOWN_METRIC' key cannot be found", thrown.getMessage());
  }

  private SensorContextTester loadMeasuresFromPath(String baseDirectoryPath) {
    MeasuresImporter measuresImporter = new MeasuresImporter();
    SensorContextTester contextTester = SensorContextTester.create(new File(baseDirectoryPath));
    measuresImporter.execute(contextTester);
    return contextTester;
  }
}
