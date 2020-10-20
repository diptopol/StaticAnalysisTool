# Static Program Analyzer

This tool provides static program analysis on the source code. The analyzer can detect three types of bug patterns.
  - Classes that defines hashCode but not equals
  - Useless control flow
  - Inadequate logging information in catch blocks

### Prerequisites
- JAVA (version >= 1.8) should be installed
- MAVEN should be installed

### Procedure to analyze a project
 1. Clone the project from github.
 2. Copy the intended source code (which will be analyzed)  directory under **projectDirectory**.
 2. Run **mvn clean install** command from project directory.
 3. Run **java -jar target/staticAnalysisTool-1.0-SNAPSHOT-jar-with-dependencies.jar**
 4. The result will be available in **log/static-analysis-result.log**
