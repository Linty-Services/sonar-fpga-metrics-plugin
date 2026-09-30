/*
 * This confidential and proprietary software may be used only as authorized
 * by a licensing agreement from Linty Services.
 * (c) Copyright 2016-2026 Linty Services
 * ALL RIGHTS RESERVED
 * The entire notice above must be reproduced on all authorized copies.
 */
package com.lintyservices.sonar.plugins.fpgametrics.gsondata;

import java.util.Map;

public class JsonMetrics {
  private Map<String, JsonMetric> metrics;

  public Map<String, JsonMetric> metrics() {
    return metrics;
  }
}
