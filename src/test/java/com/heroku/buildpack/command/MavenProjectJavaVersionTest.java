package com.heroku.buildpack.command;

import com.heroku.buildpack.BuildpackUtil;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * @author Naaman Newbold
 */
@Test
public class MavenProjectJavaVersionTest {
  @DataProvider
  public Object[][] testPomFiles() {
    return new Object[][] {
      { getTestPom("PomWithSourceAndTargetConfiguredTo1dot6"), "1.6" },
      { getTestPom("PomWithSourceAt1dot7AndTargetAt1dot6"), "1.6" },
      { getTestPom("PomWithTargetAt1dot7AndSourceAt1dot6"), "1.7" },
      { getTestPom("PomWithEmptyConfigurationElement"), "" },
      { getTestPom("PomWithEmptyPluginsElement"), "" },
      { getTestPom("PomWithEmptyBuildElement"), "" },
      { getTestPom("PomWithNoBuildElement"), "" }
    };
  }

  @DataProvider
  public Object[][] testBadPomFiles() {
    return new Object[][] {
      { getTestPom("MalformedPom") },
      { getTestPom("EmptyPom") }
    };
  }

  @Test(dataProvider = "testPomFiles")
  public void testPom(File pomFile, String expectedVersion) throws NoSuchFieldException, IllegalAccessException, UnsupportedEncodingException {
    MavenProjectJavaVersion cmd = new MavenProjectJavaVersion();
    setCommandPomFile(cmd, pomFile);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    cmd.execute(out);
    assertEquals(new String(out.toByteArray()), expectedVersion);
  }

  @Test(dataProvider = "testBadPomFiles", expectedExceptions = RuntimeException.class)
  public void badPomFilesShouldThrowRuntimeExecption(File badPom) throws NoSuchFieldException, IllegalAccessException {
    MavenProjectJavaVersion cmd = new MavenProjectJavaVersion();
    setCommandPomFile(cmd, badPom);
    cmd.execute(new ByteArrayOutputStream());
  }

  @Test //(groups = "integration")
  public void javaversionShouldBeRecognizedAsACommandAndParsedWithoutAnyOutput() throws UnsupportedEncodingException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    BuildpackUtil bpUtil = new BuildpackUtil(out);
    bpUtil.run("javaversion", "-pom", getTestPom("PomWithSourceAndTargetConfiguredTo1dot6").getAbsolutePath());
    assertEquals(new String(out.toByteArray()), "1.6");
  }

  private void setCommandPomFile(MavenProjectJavaVersion cmd, File pomFile) throws NoSuchFieldException, IllegalAccessException {
    Field pomFileField = MavenProjectJavaVersion.class.getDeclaredField("pomFile");
    pomFileField.setAccessible(true);
    pomFileField.set(cmd, pomFile);
  }

  private File getTestPom(String testPomFileName) {
    try {
      return new File(MavenProjectJavaVersionTest.class.getResource(testPomFileName + ".xml").toURI());
    } catch (URISyntaxException e) {
      fail("Unable to load resource file.", e);
      throw new RuntimeException("Unable to load resource file.", e);
    }
  }
}
