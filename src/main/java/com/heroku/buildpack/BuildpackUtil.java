package com.heroku.buildpack;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.heroku.buildpack.command.BuildpackCommand;
import com.heroku.buildpack.command.MavenProjectJavaVersion;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * @author Naaman Newbold
 */
public class BuildpackUtil {
  private final OutputStream out;

  public BuildpackUtil(OutputStream out) {
    this.out = out;
  }

  public static void main(String... args) {
    BuildpackUtil bpUtil = new BuildpackUtil(System.out);
    bpUtil.run(args);
  }

  public void run(String... args) {
    JCommander jc = new JCommander();
    jc.addCommand("javaversion", new MavenProjectJavaVersion());

    try {
      jc.parse(args);

      final String parsedCommand = jc.getParsedCommand();

      if (parsedCommand == null) {
        jc.usage();
      } else {
        JCommander commander = jc.getCommands().get(parsedCommand);
        for (Object o : commander.getObjects()) {
          ((BuildpackCommand)o).execute(out);
        }
      }
    } catch (ParameterException e) {
      try {
        out.write((e.getMessage() + "\n").getBytes("UTF-8"));
      } catch (IOException e1) {
        throw new RuntimeException(e);
      }
      if (isVerbose(args)) {
        e.printStackTrace(new PrintStream(out));
      }
    } catch (RuntimeException e) {
      if (isVerbose(args)) {
        e.printStackTrace(new PrintStream(out));
      }
    }

  }

  private boolean isVerbose(String... args) {
    for (String s : args) {
      if (s.equals("-v") || s.equals("--verbose")) {
        return true;
      }
    }
    return false;
  }

}
