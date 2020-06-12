package me.lotabout.app.provider;

import java.util.List;
import java.util.stream.Collectors;
import me.lotabout.spi.api.Function;

public class FuncConcat implements Function {

  @Override
  public String name() {
    return "concat";
  }

  @Override
  public Object apply(List<Object> args) {
    return args.stream().map(Object::toString).collect(Collectors.joining());
  }
}
