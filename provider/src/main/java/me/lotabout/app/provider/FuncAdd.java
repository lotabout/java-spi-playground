package me.lotabout.app.provider;

import java.util.List;
import me.lotabout.spi.api.Function;

public class FuncAdd implements Function {

  @Override
  public String name() {
    return "add";
  }

  @Override
  public Object apply(List<Object> args) {
    return args.stream().mapToLong(arg -> (Long) arg).sum();
  }
}
