#US Census - American Fact Finder Data Collection Tool
###This app pulls a specific set of data from the American Fact Finder API and creates a .json file containing those data elements for all counties within all US states.

American Fact Finder website - https://factfinder.census.gov/service/RESTImplementation.html
American Fact Finder API Explorer - https://factfinder.census.gov/service/apps/api-explorer/#!/statisticalData/data?p.langId=en&p.programId=ACS&p.datasetId=13_5YR

Request a census.gov API Key here: https://api.census.gov/data/key_signup.html

Ran into handshake error( error: javax.net.ssl.SSLHandshakeException: Received fatal alert: handshake_failure):
    - Download these jars: and copy to {JAVA_HOME}/jre/lib/security 
    - https://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html

Libraries used:
Apache Commons CLI - https://commons.apache.org/proper/commons-cli/
Apache HTTP Client - http://hc.apache.org/

Build:
`gradlew build jar`

Usage:
`java -jar build/libs/aff-extractor-0.0.1.jar -y <YEAR> -k <API_KEY>`