/*
 * This confidential and proprietary software may be used only as authorized
 * by a licensing agreement from Linty Services.
 * (c) Copyright 2016-2026 Linty Services
 * ALL RIGHTS RESERVED
 * The entire notice above must be reproduced on all authorized copies.
 */
package com.lintyservices.sonar.plugins.fpgametrics;

import com.lintyservices.sonar.plugins.fpgametrics.sensor.MeasuresImporter;
import com.lintyservices.sonar.plugins.fpgametrics.sensor.MetricsImporter;
import org.sonar.api.Plugin;

public class FpgaMetricsPlugin implements Plugin {

  @Override
  public void define(Context context) {
    context.addExtensions(MetricsImporter.class, MeasuresImporter.class);
  }
}
