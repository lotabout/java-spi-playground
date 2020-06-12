package me.lotabout.spi.app.builtin;

import java.util.List;
import me.lotabout.spi.api.Function;

public class FuncId implements Function {

  @Override
  public String name() {
    return "id";
  }

  @Override
  public Object apply(List<Object> args) {
    return args.get(0);
  }
}
