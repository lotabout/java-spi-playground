package me.lotabout.spi.app;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.ServiceLoader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import me.lotabout.spi.api.Function;

public class Main {
  // variable = func(args, ...);
  private static final Pattern SCRIPT_PATTERN =
      Pattern.compile(" *(?:([_a-zA-Z][_a-zA-Z0-9]*) *=)? *([_a-zA-Z][_a-zA-Z0-9]+) *\\((.*)\\)");

  private static final Pattern TOKEN_NUM = Pattern.compile("[0-9]+");
  private static final Pattern TOKEN_STR = Pattern.compile("\"([^\"]+)\"");

  public static void main(String[] args) throws MalformedURLException {
    // set classloader for additional methods
    if (args.length > 0) {
      MethodLoader loader = new MethodLoader();
      for (String fileOrDirectory : args) {
        loader.addJarOrDir(fileOrDirectory);
      }
      Thread.currentThread().setContextClassLoader(loader);
    }

    Map<String, Function> functionPool = loadFunctions();
    Map<String, Object> variablePool = new HashMap<>();

    System.out.println("> Please input expression(e.g. x = add(10, 20))");
    System.out.println("> \"exit\" to quit");
    Scanner sc = new Scanner(System.in);
    while (true) {
      String line = sc.nextLine();

      // statement: exit
      if ("exit".equalsIgnoreCase(line.trim())) {
        System.out.println("bye");
        break;
      }

      // statement: print variable
      if (!line.contains("(")) {
        // print the variable
        Object value = variablePool.get(line.trim());
        if (value != null) {
          System.out.println(value.toString());
        } else {
          System.out.println("> variable not found");
        }
        continue;
      }

      // statement: execute and assign
      // e.g. var = func(arg1, arg2, ...)
      // e.g. func(arg1, arg2, ...)
      Matcher matcher = SCRIPT_PATTERN.matcher(line);
      if (matcher.find()) {
        String variableName = matcher.group(1);
        String funcName = matcher.group(2);
        String argStr = matcher.group(3);
        try {
          Object value = execute(funcName, argStr, functionPool, variablePool);
          if (variableName != null) {
            variablePool.put(variableName, value);
          }
          System.out.println("> " + value.toString());
        } catch (Exception ex) {
          System.out.println("> Error on execution: " + ex.getMessage());
        }
      } else {
        System.out.println("could not handle script: " + line);
      }
    }
  }

  // use SPI to load functions
  private static Map<String, Function> loadFunctions() {
    Map<String, Function> functionPool = new HashMap<>();
    ServiceLoader<Function> loader = ServiceLoader.load(Function.class);
    for (Function func : loader) {
      System.out.println("> loading function: " + func.name());
      functionPool.put(func.name(), func);
    }

    return functionPool;
  }

  // execute function and return result
  private static Object execute(
      String funcName,
      String argStr,
      Map<String, Function> functionPool,
      Map<String, Object> variablePool) {

    List<Object> args =
        Arrays.stream(argStr.split(","))
            .map(String::trim)
            .map(arg -> parseArg(arg, variablePool))
            .collect(Collectors.toList());

    if (!functionPool.containsKey(funcName)) {
      throw new IllegalArgumentException("function not found: " + funcName);
    }

    Function function = functionPool.get(funcName);
    return function.apply(args);
  }

  private static Object parseArg(String variable, Map<String, Object> variablePool) {
    if (TOKEN_NUM.matcher(variable).matches()) {
      return Long.valueOf(variable);
    } else if (TOKEN_STR.matcher(variable).matches()) {
      return variable.substring(1, variable.length() - 1);
    } else if (variablePool.containsKey(variable)) {
      return variablePool.get(variable);
    } else {
      throw new IllegalArgumentException("could not resolve argument: " + variable);
    }
  }
}
