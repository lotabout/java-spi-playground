# SPI playground

It's a simple expression interpreter, loading user defined functions with SPI.

## Usage

1. clone the project: `git clone https://github.com/lotabout/java-spi-playground.git`
2. build with maven(java 8): `mvn clean package`
3. Run without extensions: `java -jar app/target/spi-app-1.0-SNAPSHOT-jar-with-dependencies.jar`
4. Run with extensions: `java -jar app/target/spi-app-1.0-SNAPSHOT-jar-with-dependencies.jar provider/target`

## Example

The program itself is a simple expression interpreter:

```
$ java -jar app/target/spi-app-1.0-SNAPSHOT-jar-with-dependencies.jar provider/target
add jar /Users/jinzhouz/repos/java-spi-playground/provider/target/spi-provider-1.0-SNAPSHOT.jar
loading function: id
loading function: add
loading function: mul
loading function: concat
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

### Extension Functions are Loaded via SPI

1. We define the interface `Function`
2. Use `ServiceLoader` to load all "implementations" of `Function`.
3. The implementations should register themselves by:
    a. creating a directory `META-INF/services`
    b. creating a file indicating the interface they implement: `me.lotabout.spi.api.Function`
    b. write the full qualified name of the implementation into the file:
        ```
        me.lotabout.app.provider.FuncMul
        me.lotabout.app.provider.FuncConcat
        ```

The method to load all functions:

```java
private static Map<String, Function> loadFunctions() {
  Map<String, Function> functionPool = new HashMap<>();
  ServiceLoader<Function> loader = ServiceLoader.load(Function.class);
  for (Function func : loader) {
    System.out.println("> loading function: " + func.name());
    functionPool.put(func.name(), func);
  }

  return functionPool;
}
```

### Hack ClassLoader to Load Extensions

We want to run our application via `java -jar xxx.jar` yet load our
extensions(the `provider` submodule) easily.

Our customized ClassLoader `MethodLoader` is used to scan a directory and load
additional (extensions) jars.

```java
public synchronized void addJarOrDir(String jarName) throws MalformedURLException {
  File file = Paths.get(jarName).toFile();
  if (file.isDirectory()) {
    for (File jar : Objects.requireNonNull(file.listFiles())) {
      addJarOrDir(jar.getAbsolutePath());
    }
  } else {
    addJar(jarName);
  }
}

public synchronized void addJar(String jarName) throws MalformedURLException {
  File file = Paths.get(jarName).toFile();
  if (file.isFile() && jarName.endsWith(".jar")) {
    System.out.println("> add jar " + jarName);
    addURL(Paths.get(jarName).toUri().toURL());
  }
}
```

Another important thing is we need to set it as ContextLoader so that all our
extension functions could be loaded correctly.

```java
MethodLoader loader = new MethodLoader();
for (String fileOrDirectory : args) {
  loader.addJarOrDir(fileOrDirectory);
}
Thread.currentThread().setContextClassLoader(loader);
```

That's it.
