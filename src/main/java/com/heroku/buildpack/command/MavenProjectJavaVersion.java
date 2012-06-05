package com.heroku.buildpack.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.*;
import java.util.Map;

/**
 * Get the java version specified in a pom.xml file. Pulls the source and target attributes
 * from the maven-compiler-plugin. If the target is populated, that number is printed. If
 * the source is populated and the target is not, the source number is printed. Otherwise,
 * no value is printed.
 *
 * @author Naaman Newbold
 */
@Parameters(commandDescription = "Get the specified java version for a pom.xml file.")
public class MavenProjectJavaVersion implements BuildpackCommand {
  @Parameter(
    names = "-pom",
    required = true,
    description = "Location of pom.xml file on which to base the Java version."
  )
  private File pomFile;

  @Override
  public void execute(OutputStream out) {
    MavenXpp3Reader reader = new MavenXpp3Reader();
    try {
      Model model = reader.read(new FileReader(pomFile));
      Build build = model.getBuild();
      if (build == null) return;

      Map<String,Plugin> pluginsAsMap = build.getPluginsAsMap();

      Plugin compilerPlugin = pluginsAsMap.get("org.apache.maven.plugins:maven-compiler-plugin");
      if (compilerPlugin == null) return;

      Xpp3Dom configuration = (Xpp3Dom)compilerPlugin.getConfiguration();
      if (configuration == null) return;

      Xpp3Dom sourceVersion = configuration.getChild("source");
      Xpp3Dom targetVersion = configuration.getChild("target");

      if (targetVersion != null) {
        printVersion(out, targetVersion.getValue());
      } else if (sourceVersion != null) {
        printVersion(out, sourceVersion.getValue());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (XmlPullParserException e) {
      throw new RuntimeException(e);
    }
  }

  private void printVersion(OutputStream out, String version) throws IOException {
    out.write(version.getBytes("UTF-8"));
  }
}
