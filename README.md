# SPI playground

It's a simple expression interpreter, loading user defined functions with SPI + Java 9.

## Usage

1. clone the project: `git clone https://github.com/lotabout/java-spi-playground.git`
2. build with maven(java 9): `mvn clean package`
3. Run with extensions: `java -p $(find . -name "*.jar"|tr '\n' ':') -m me.lotabout.spi.app/me.lotabout.spi.app.Main`

## Example

The program itself is a simple expression interpreter:

```
$ java -p $(find . -name "*.jar"|tr '\n' ':') -m me.lotabout.spi.app/me.lotabout.spi.app.Main
loading function: mul
loading function: add
loading function: concat
loading function: id
Please input expression(e.g. x = add(10, 20))
"exit" to quit
> x = id(10)
10
> y = add(x, 20, 30)
60
> z = mul(y, y, y)
216000
> concat("z = ", z)
z = 216000
> exit
bye
```

## Technical Detail

### Extensions are Loaded by ServiceLoader at Runtime

With the interface `Function` defined, we could ask Java to load all
(registered) implementations at runtime:

```java
public static Map<String, Function> getRegistered() {
  Map<String, Function> functionPool = new HashMap<>();
  ServiceLoader<Function> loader = ServiceLoader.load(Function.class);
  for (Function func : loader) {
    System.out.println("loading function: " + func.name());
    functionPool.put(func.name(), func);
  }

  return functionPool;
}
```

### `uses`

In the service consumer module, we need to declare the interface(service)
whose implementations we'd like to load:

```java
module me.lotabout.spi.api {
  exports me.lotabout.spi.api;
  uses me.lotabout.spi.api.Function;
}
```

### register implementations via `provides`

In the implementation module, we need declare our implementations via
`provides`:

```java
module me.lotabout.spi.provider {
  requires me.lotabout.spi.api;

  provides me.lotabout.spi.api.Function with
      me.lotabout.app.provider.FuncMul,
      me.lotabout.app.provider.FuncAdd,
      me.lotabout.app.provider.FuncConcat,
      me.lotabout.app.provider.FuncId;
}
```

That's it.
