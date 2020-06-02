 /*
 * sonar-fpga-metrics plugin for Sonarqube
 * Copyright (C) 2020 Linty Services
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linty.plugins.fpgametrics.measures;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.measures.Metric;
import org.sonar.api.scanner.sensor.ProjectSensor;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;


import org.sonar.api.config.Configuration;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;


public class MeasuresImporter implements ProjectSensor {
	
	private List<Metric> metrics=null;
	
	
	 public MeasuresImporter(Configuration configuration) {
	        this(MetricsImporter.getMetricsResult(), configuration);
	    }
	 
	 /**
     * For testing purpose only
     */
	public MeasuresImporter(List<Metric> metrics, Configuration configuration) {
        this.metrics = metrics;
    }


	@Override
	public void describe(SensorDescriptor descriptor) {
		descriptor.name("Import measures from Json files");
	}

	@Override
	public void execute(SensorContext context) {
	
		if(metrics==null)
			metrics=MetricsImporter.getMetricsResult();
		Gson gson = new Gson();
		Map<String,Object> measures = new HashMap<String,Object>();
		try {
			measures = gson.fromJson(new FileReader("measures.json"), measures.getClass());
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("No measures report found in this project directory");
		}

			for(Metric metric:metrics) {
				
				 FileSystem fs = context.fileSystem();
				    // only "main" files, but not "tests"
				    Iterable<InputFile> files = fs.inputFiles(fs.predicates().hasType(InputFile.Type.MAIN));
				    for (InputFile file : files) {
				    	Gson gson_file = new Gson();
						Map<String,Object> measures_file = new HashMap<String,Object>();
						try {
							measures_file = gson_file.fromJson(new FileReader(FilenameUtils.removeExtension(file.absolutePath())+"_measures.json"), measures_file.getClass());
						} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
						
					  Object rawValue = measures_file.get(metric.getKey());
					  Double ratioMax = null;
					  if(rawValue!=null&&rawValue.getClass().isArray()) {
							if (((ArrayList) rawValue).size()==2) {
								ratioMax=(Double) ((ArrayList) rawValue).get(1);
								rawValue=((ArrayList) rawValue).get(0);
							}else {						
								rawValue=null;
							}
						}
					  
					  if (rawValue!=null) {
						  String valueTypeStr = metric.getType().name();//ExampleMetrics.measures.metrics().get(metric.getKey()).getType();
							switch (valueTypeStr) {
							case "INT":
								context.newMeasure().forMetric(metric).on(file).withValue((int)Math.round((Double) rawValue)).save();
								break;
							case "FLOAT":
								if(ratioMax==null)
									context.newMeasure().forMetric(metric).on(context.project()).withValue((Double) rawValue).save();
								else
									context.newMeasure().forMetric(metric).on(context.project()).withValue(((Double) rawValue)/ratioMax).save();
								break;
							case "PERCENT":
								if(ratioMax==null)
									context.newMeasure().forMetric(metric).on(context.project()).withValue((Double) rawValue).save();
								else
									context.newMeasure().forMetric(metric).on(context.project()).withValue(((Double) rawValue)*100.0/ratioMax).save();
								break;
							case "BOOL":
								context.newMeasure().forMetric(metric).on(file).withValue((Boolean) rawValue).save();
								break;
							case "STRING":
								context.newMeasure().forMetric(metric).on(file).withValue((String) rawValue).save();
								break;
							case "MILLISEC":
								context.newMeasure().forMetric(metric).on(file).withValue((Long)Math.round((Double) rawValue)).save();
								break;
							case "DATA":
								context.newMeasure().forMetric(metric).on(file).withValue((String) rawValue).save();
								break;
							case "DISTRIB":
								context.newMeasure().forMetric(metric).on(file).withValue((String) rawValue).save();
								break;
							case "RATING":
								context.newMeasure().forMetric(metric).on(file).withValue((int)Math.round((Double) rawValue)).save();
								break;
							case "WORK_DUR":
								context.newMeasure().forMetric(metric).on(file).withValue((Long)Math.round((Double) rawValue)).save();
								break;
							default:
								;
							}
					  }
				    }
				    
				Object rawValue = measures.get(metric.getKey());
				Double ratioMax = null;
				if(rawValue!=null&&rawValue.getClass().equals(ArrayList.class)) {
					if (((ArrayList) rawValue).size()==2) {
						ratioMax=(Double) ((ArrayList) rawValue).get(1);
						rawValue=((ArrayList) rawValue).get(0);
					}else {						
						rawValue=null;
					}
				}
				if (rawValue!=null) {
					String valueTypeStr = metric.getType().name();//ExampleMetrics.measures.metrics().get(metric.getKey()).getType();
					switch (valueTypeStr) {
					case "INT":
						context.newMeasure().forMetric(metric).on(context.project()).withValue((int)Math.round((Double) rawValue)).save();
						break;
					case "FLOAT":
						if(ratioMax==null)
							context.newMeasure().forMetric(metric).on(context.project()).withValue((Double) rawValue).save();
						else
							context.newMeasure().forMetric(metric).on(context.project()).withValue(((Double) rawValue)/ratioMax).save();
						break;
					case "PERCENT":
						if(ratioMax==null)
							context.newMeasure().forMetric(metric).on(context.project()).withValue((Double) rawValue).save();
						else
							context.newMeasure().forMetric(metric).on(context.project()).withValue(((Double) rawValue)*100.0/ratioMax).save();
						break;
					case "BOOL":
						context.newMeasure().forMetric(metric).on(context.project()).withValue((Boolean) rawValue).save();
						break;
					case "STRING":
						context.newMeasure().forMetric(metric).on(context.project()).withValue((String) rawValue).save();
						break;
					case "MILLISEC":
						context.newMeasure().forMetric(metric).on(context.project()).withValue((Long)Math.round((Double) rawValue)).save();
						break;
					case "DATA":
						context.newMeasure().forMetric(metric).on(context.project()).withValue((String) rawValue).save();
						break;
					case "DISTRIB":
						context.newMeasure().forMetric(metric).on(context.project()).withValue((String) rawValue).save();
						break;
					case "RATING":
						context.newMeasure().forMetric(metric).on(context.project()).withValue((int)Math.round((Double) rawValue)).save();
						break;
					case "WORK_DUR":
						context.newMeasure().forMetric(metric).on(context.project()).withValue((Long)Math.round((Double) rawValue)).save();
						break;
					default:
						;
					}
				}
			}
					

	}
	
}
