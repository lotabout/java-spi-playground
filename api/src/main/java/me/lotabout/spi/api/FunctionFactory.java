package me.lotabout.spi.api;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class FunctionFactory {

  public static Map<String, Function> getRegistered() {
    Map<String, Function> functionPool = new HashMap<>();
    ServiceLoader<Function> loader = ServiceLoader.load(Function.class);
    for (Function func : loader) {
      System.out.println("loading function: " + func.name());
      functionPool.put(func.name(), func);
    }

    return functionPool;
  }
}
