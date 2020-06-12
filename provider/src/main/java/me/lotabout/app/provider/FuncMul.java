package me.lotabout.app.provider;

import java.util.List;
import me.lotabout.spi.api.Function;

public class FuncMul implements Function {

  @Override
  public String name() {
    return "mul";
  }

  @Override
  public Object apply(List<Object> args) {
    return args.stream().mapToLong(arg -> (Long) arg).reduce((a, b) -> a * b).orElse(0);
  }
}
