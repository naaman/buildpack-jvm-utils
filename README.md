# JVM Buildpack Utils

Java-based utilities for making jvm-based buildpacks a tad
bit easier to deal with. Hacking scripts together is awesome,
but it's buggy when you're mimicking real framework behavior.
It's better to let the framework do its job and have the
buildpack delegate. With that in mind, these are utilities
that pull in the appropriate framework and provide easy to
parse output.

# Usage

### Detect the major version of java from a pom file:

`java -jar buildpack-jvm-utils-0.1.jar javaversion -pom /path/to/pom.xml`

The above expects the presence of `org.apache.maven.plugins:maven-compiler-plugin`
along with a source and/or target configuration. If target is configured, the 
target value is printed. If source is configured and target is not, the source
value is printed. Otherwise, nothing is printed.

# Building

`git clone git://github.com/naamannewbold/buildpack-jvm-utils.git`

`mvn package`
