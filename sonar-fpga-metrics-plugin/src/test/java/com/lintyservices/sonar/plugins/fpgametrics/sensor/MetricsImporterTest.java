/*
 * SonarQube Linty FPGA Metrics :: Plugin
 * Copyright (C) 2020-2020 Linty Services
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

import org.junit.Test;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metric.ValueType;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class MetricsImporterTest {

  @Test
  public void test() {
    List<Metric> metrics = new MetricsImporter().getMetrics();
    assertEquals(116, metrics.size());
    assertEquals("NX logs remarks count", metrics.get(0).getName());
    assertEquals(ValueType.INT, metrics.get(1).getType());
    assertEquals(false, metrics.get(2).getQualitative());
    // TODO: Add more checks
  }
}