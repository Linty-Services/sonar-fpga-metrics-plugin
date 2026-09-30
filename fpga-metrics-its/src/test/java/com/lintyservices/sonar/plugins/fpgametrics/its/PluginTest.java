/*
 * This confidential and proprietary software may be used only as authorized
 * by a licensing agreement from Linty Services.
 * (c) Copyright 2016-2026 Linty Services
 * ALL RIGHTS RESERVED
 * The entire notice above must be reproduced on all authorized copies.
 */
package com.lintyservices.sonar.plugins.fpgametrics.its;

import static org.assertj.core.api.Assertions.assertThat;

import com.lintyservices.testharness.Orchestrator;
import com.sonar.orchestrator.junit5.OrchestratorExtension;
import com.sonar.orchestrator.locator.FileLocation;
import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class PluginTest {

  @RegisterExtension
  static final OrchestratorExtension ORCHESTRATOR =
      Orchestrator.createOrchestratorExtensionBuilder()
          .addPlugin(
              FileLocation.byWildcardMavenFilename(
                  new File("../sonar-fpga-metrics-plugin/target"),
                  "sonar-fpga-metrics-plugin-*.jar"))
          .build();

  @Test
  void sonar_fpag_metrics_plugin_is_installed() {
    String body =
        ORCHESTRATOR
            .getServer()
            .newHttpCall("api/plugins/installed")
            .setAdminCredentials()
            .execute()
            .getBodyAsString();
    assertThat(body).contains("\"key\":\"fpgametrics\"");
  }
}
