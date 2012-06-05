package com.heroku.buildpack.command;

import java.io.OutputStream;

/**
 * @author Naaman Newbold
 */
public interface BuildpackCommand {
  void execute(OutputStream out);
}
