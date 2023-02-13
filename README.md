# SonarQube Linty FPGA Metrics Plugin  

Create custom metrics and populate them through external file import. 

## Build Plugin

Without integration tests:
```bash
mvn clean package
```

With integration tests on SonarQube 9.7.0.61563 version:
```bash
mvn clean verify -Pits -Dsonar.runtimeVersion=9.7.0.61563
```

Update license headers:
```bash
mvn license:format -Pits
```


## Update All Dependencies
```bash
# Check for Maven dependencies to update
mvn org.codehaus.mojo:versions-maven-plugin:2.12.0:display-dependency-updates -Pits

# Check for Maven plugins to update
mvn org.codehaus.mojo:versions-maven-plugin:2.12.0:display-plugin-updates -Pits

# Check for versions in properties to update
mvn org.codehaus.mojo:versions-maven-plugin:2.12.0:display-property-updates -Pits

# Update parent POM
mvn org.codehaus.mojo:versions-maven-plugin:2.12.0:update-parent
```

## Usage

All custom metrics should be defined in a unique 'format-metrics.json' file.  

By default src/main/resources/fpgametrics/fomat-metrics.json is used to define the list of metrics.

"Domain" field is optional. Metrics are automatically placed as "undefined" but it's allowed to use a custom metric with an existing domain like "Maintenability". 

Pay attention, case is sensitive.

Measures for each custom metric should be defined in a file named 'measures.json' at the root of the analyzed project, when they relate to a project.

PERCENT and FLOAT types could be used with a tab [current_value, max_value].

Metrics are grouped by unit. Coverage measure (PERCENT) and number of lines of code (INT) could not be displayed in the same graph.

Only two graphs could be displayed, for instance it's not permitted to display nb of LOC, coverage and security rating in the same time.

A warning could appear when you are trying to display a third type of measure graph like this:
- Graph 1 measures unit = INT
- Graph 2 measures unit = PERCENT
- Graph 3 measures = Security Rating

When measures relate to a single file, they should be in a json file named [Source file name without extension]_measures.json, in the same folder as the corresponding file.    

Measures will be imported in Sonarqube user interface after executing sonar-scanner. When a measure is not provided for a metric, this one is not displayed in Sonarqube user interface.


# Examples

format-metrics.json file :   


{
	"metrics": {
		"Metric1": {
			"name": "Metric 1",
			"type": "FLOAT"
		},
		"Metric2": {
			"name": "Metric 2",
			"type": "INT"
		},
		"Metric3": {
			"name": "Metric 4",
			"type": "PERCENT",
			"domain":"Maintenability"
		},
		"Metric4": {
			"name": "Metric 5",
			"type": "FLOAT",
			"domain":"custom"
		},
		"Metric5": {
			"name": "Metric 6",
			"type": "PERCENT",
			"domain":"custom"
		},
		"Metric6": {
			"name": "Metric 7",
			"type": "MILLISEC"
		},
		"Metric7": {
			"name": "Metric 8",
			"type": "RATING"
		},
		"Metric8": {
			"name": "Metric 9",
			"type": "WORK_DUR"
		},
		"Metric9": {
			"name": "Metric 10",
			"type": "BOOL"
		}
	}
}


measures.json file :   

{
	"Metric1": 80,
	"Metric2": 10,
	"Metric3": [60, 200],
	"Metric4": [400, 1000],
	"Metric5": 70,
	"Metric6": 20,
	"Metric7": 30,
	"Metric8": 55,
	"Metric9": true
}   


## Tips

To ensure storing parameters, the activity graph URL could be saved in favorite, thanks to the link which contains all metrics information.
