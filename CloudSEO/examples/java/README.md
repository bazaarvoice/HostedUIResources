The Java example code here demonstrates a sample API for retrieving SmartSEO content (and possibly our integration code as well).

Before running the code, you must edit client.properties and include the settings we've provided you for your integration.

After editing the properties, the main method Example.java is a good place to start.  The example simply reads a sample HTML page file, retrieves the corresponding SmartSEO content from S3,
inserts it into the document and prints the results to stdout.  Be sure to change the hardcoded values in the example to a valid product ID and page URL for your environment.


To compile:

    $ mvn compile

To run the included tests:

    $ mvn clean test

To run the Example class:

    $ mvn exec:java -Dexec.mainClass="Example"
