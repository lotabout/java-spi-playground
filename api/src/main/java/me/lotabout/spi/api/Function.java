package me.lotabout.spi.api;

import java.util.List;

public interface Function {
  /** The name of the function */
  String name();

  /** Execute with args and return the result */
  Object apply(List<Object> args);
}
