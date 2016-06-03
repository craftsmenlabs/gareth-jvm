# Gareth
[![Build Status](https://travis-ci.org/craftsmenlabs/gareth-jvm.svg)](https://travis-ci.org/craftsmenlabs/gareth-jvm)
[![Coverage Status](https://coveralls.io/repos/craftsmenlabs/gareth-jvm/badge.svg?branch=master&service=github)](https://coveralls.io/github/craftsmenlabs/gareth-jvm?branch=master)

Gareth is platform that allows you to make business goal validation part of your development process. For extra information visit our [website](http://craftsmenlabs.github.io/gareth/) and our [blog](http://craftsmenlabs.github.io/gareth/blog.html).

<!-- TOC depthFrom:1 depthTo:6 withLinks:1 updateOnSave:1 orderedList:0 -->

- [Gareth](#gareth)
	- [Building Gareth from source](#building-gareth-from-source)
	- [Running Gareth](#running-gareth)
		- [Feeding Gareth](#feeding-gareth)
			- [Experiment](#experiment)
			- [Definitions](#definitions)
		- [Running Gareth (without REST interface)](#running-gareth-without-rest-interface)
		- [Running Gareth (with REST interface)](#running-gareth-with-rest-interface)
- [Matching glue lines to definition methods](#matching-glue-lines-to-definition-methods)
- [Specifying duration](#specifying-duration)
	- [Contribute](#contribute)
		- [Help wanted!](#help-wanted)

<!-- /TOC -->

## Building Gareth from source

Gareth is built using Apache maven. Download it here: https://maven.apache.org/install.html
Note: to use the code base in Eclipse or IntelliJ, make sure you install the Lombok plugin and configure the compiler settings accordingly:
https://github.com/mplushnikov/lombok-intellij-plugin

## Running Gareth

Gareth can be run in two different manners, can be run with or without a REST interface.

### Feeding Gareth

The Gareth framework must be fed with two pieces of information, the 'experiment' and the 'definitions'.

#### Experiment

The experiments describes the assumptions somebody has when they have certain functionality build. For example a certain
functionality is build is build to reduce failed logins. (The why) Within a experiment you can describe this in a
structured manner.

```
Experiment: reduce failed logins

Baseline: Get current failed logins
Assume: Failed login are reduced by 500
Time: 1 week

Baseline: Get current failed logins
Assume: Failed login are reduced by 1000
Time: 1 month

Baseline: Get current failed logins
Assume: Failed login are reduced by 2000
Time: 1 months
Success: Sent cake to the developers
Failure: Remove functionality
```

#### Definitions

Definitions is the code that is glued to the experiments structured language, by allowing this aproach there is no
limitation how you can validate your assumptions.

```java
import org.craftsmenlabs.gareth.api.annotation.Assume;
import org.craftsmenlabs.gareth.api.annotation.Baseline;
import org.craftsmenlabs.gareth.api.annotation.Failure;
import org.craftsmenlabs.gareth.api.annotation.Time;
import org.craftsmenlabs.gareth.api.storage.Storage;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class LoginFailuresDefinitions {

    @Baseline(glueLine = "Get current failed logins")
    public void baseline(final Storage storage) {
        // Code to get failed logins
        storage.store("failedLogins", 1);
    }

    @Assume(glueLine = "Failed login are reduced by 500")
    public void assume500(final Storage storage) {
        // Code to get failed logins
        int failedLoginBefore = storage.get("failedLogins");
        // Code to get failed logins
    }

    @Assume(glueLine = "Failed login are reduced by 1000")
    public void assume1000(final Storage storage) {
        // Code to get failed logins
        int failedLoginBefore = storage.get("failedLogins");
        // Code to get failed logins
    }

    @Assume(glueLine = "Failed login are reduced by 2000")
    public void assume2000(final Storage storage) {
        // Code to get failed logins
        int failedLoginBefore = storage.get("failedLogins");
        // Code to get failed logins
    }

    @Success(glueLine = "Sent cake to the developers")
    public void success(){
        // Code to sent cake to the developers
    }

    @Failure(glueLine = "Remove functionality")
    public void failure(){
        // Code to remove functionality
    }

    @Time(glueLine = "1 week")
    public Duration getOneWeek(){
        return Duration.of(1, ChronoUnit.WEEKS);
    }

    @Time(glueLine = "1 month")
    public Duration getOneMonth(){
        return Duration.of(1, ChronoUnit.MONTHS);
    }

    @Time(glueLine = "2 months")
    public Duration getTwoMonths(){
        return Duration.of(2, ChronoUnit.MONTHS);
    }
}
```


### Running Gareth (without REST interface)

Running Gareth without a REST interface can be done by creating a maven project that depends on the following maven
dependency:

```xml
<dependency>
    <groupId>org.craftsmenlabs.gareth</groupId>
    <artifactId>gareth-core</artifactId>
    <version>0.8.6</version>
</dependency>
```

Create a java application that starts the Gareth framework:

```java
import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.ExperimentEngineConfig;
import org.craftsmenlabs.gareth.core.ExperimentEngineConfigImpl;
import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;
import org.craftsmenlabs.gareth.examples.definition.SampleDefinition;

public class ExampleApplication {


    public static void main(final String[] args) {
        final ExperimentEnginePersistence experimentEnginePersistence = new FileSystemExperimentEnginePersistence.Builder().build();
        final ExperimentEngineConfig experimentEngineConfig = new ExperimentEngineConfigImpl
                .Builder()
                .addDefinitionClass(SampleDefinition.class)
                .addInputStreams(ExampleApplication.class.getClass().getResourceAsStream("/experiments/businessgoal-01.experiment"))
                .setIgnoreInvocationExceptions(true)
                .build();
        final ExperimentEngine experimentEngine = new ExperimentEngineImpl
                .Builder(experimentEngineConfig)
                .setExperimentEnginePersistence(experimentEnginePersistence)
                .build();
        experimentEngine.start();

        Runtime.getRuntime().addShutdownHook(new ShutdownHook(experimentEngine));
    }

    /**
     * Shutdown hook when application is stopped then also stop the experiment engine.
     */
    static class ShutdownHook extends Thread {

        private final ExperimentEngine experimentEngine;

        private ShutdownHook(final ExperimentEngine experimentEngine) {
            this.experimentEngine = experimentEngine;
        }

        @Override
        public void run() {
            experimentEngine.stop();
        }
    }
}
```

For building the standalone application, you can use the maven-shade-plugin to create a single jar containing all the
necessary classes. You can add this your project pom.xml.

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>2.4.1</version>
            <configuration>
                <transformers>
                    <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                        <mainClass>org.craftsmenlabs.gareth.examples.ExampleApplication</mainClass>
                    </transformer>
                </transformers>
            </configuration>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

After doing a ```maven clean package``` you now can run the Gareth platform with your own definitions and experiments.

```shell
java -jar /path/to/project.jar
```

### Running Gareth (with REST interface)

Running Gareth with a REST interface can be done by creating a maven project that depends on the following maven
dependency:

```xml
<dependency>
    <groupId>org.craftsmenlabs.gareth</groupId>
    <artifactId>gareth-core</artifactId>
    <version>0.8.6</version>
</dependency>
<dependency>
    <groupId>org.craftsmenlabs.gareth</groupId>
    <artifactId>gareth-rest</artifactId>
    <version>0.8.6</version>
</dependency>
```

Create a java application that starts the Gareth framework:

```java
import org.craftsmenlabs.gareth.api.ExperimentEngine;
import org.craftsmenlabs.gareth.api.ExperimentEngineConfig;
import org.craftsmenlabs.gareth.api.rest.RestServiceFactory;
import org.craftsmenlabs.gareth.core.ExperimentEngineConfigImpl;
import org.craftsmenlabs.gareth.core.ExperimentEngineImpl;

/**
 * Created by hylke on 17/08/15.
 */
public class GarethContext {

    public static void main(final String[] args) throws Exception {
        final RestServiceFactory restServiceFactory = new RestServiceFactoryImpl(); // Create a new rest service factory
        final ExperimentEngineConfig config = new ExperimentEngineConfigImpl.Builder().build();
        final ExperimentEngine engine = new ExperimentEngineImpl.Builder(config).setRestServiceFactory(restServiceFactory).build(); // And just include it in the engine
        engine.start();
    }
}
```

For building the standalone application, you can use the maven-shade-plugin to create a single jar containing all the
necessary classes. You can add this your project pom.xml.

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>2.4.1</version>
            <configuration>
                <transformers>
                    <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                        <mainClass>org.craftsmenlabs.gareth.examples.ExampleApplication</mainClass>
                    </transformer>
                </transformers>
            </configuration>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

After doing a ```maven clean package``` you now can run the Gareth platform with your own definitions and experiments.

```shell
java -jar /path/to/project.jar
```

# Matching glue lines to definition methods

Your baselines, assumptions, success and failure definitions need to correspond to Java methods that do the actual work.
These Java methods reside in so-called definition classes that are known to Gareth through configuration, and the relevant methods are identified through the @Baseline, @Assume, @Time, @Success and @Failure annotations. In its simplest form, it looks like this:

```shell
Baseline: sale of anvils

@Baseline(glueLine = "sale of anvils")
public void getSaleOfAnvils() {  
  System.out.println("Getting sale of anvils");
}
```
or with using the built-in Storage:
```shell
@Baseline(glueLine = "sale of anvils")
public void getSaleOfAnvils(final Storage storage) {  
  storage.store(product, dbase.getCurrentSalesOfProduct("anvil"));
}
```
A shortcoming of this approach is that there is a strict one-to-one mapping between glue line and definition method. If you want to get the sale of hammers or screwdrivers in a different experiment you'd need to write new methods for each with probably very similar code. That hardly seems efficient. It would be much better if we could make our method configurable by adding the product as a parameter:
public void getSaleByProductCode(final String productCode){...}

Then we can indicate the configurable part in the glue line by means of a grouped regular expression, like so:
```shell
@Baseline(glueLine = "sale of (.*?)")
public void getSaleOfProductByCode(final Storage storage,final String productCode) {  
  storage.store(product, dbase.getCurrentSalesOfProduct(productCode));
}
```
Multiple groupings are allowed:
```shell
@Success(glueLine = "order (\\d{1,3}) (.*?) from (.*?)")
public void sendTreats(int amount, String treat, String supplier) {
	String.format("Enjoy the %d %s from %s", amount, treat, supplier);
}
```
And this lets you re-use the same Java code for very different glue lines:
Success: order 3 carrot cakes from local bakery
Success: order 5 iPhones from Amazon

It's a powerful mechanism, but there are some rules to the game:

* The number of parenthesised regex groups must be exactly equal to the number of parameters in the method, ignoring the optional Storage parameter, which always comes first and is injected by Gareth when specified. The values are then extracted from the glueline and the definition method is called with these parameters:
"order 3 carrot cakes from local bakery" matches on 3, "carrot cakes" and "local bakery" and calls sendTreats(3,"carrot cakes","local bakery")

* Permitted arguments types are String, Integer, Long, Double and their corresponding primitive types. Use of other types in definition methods will cause an error. Gareth must be able to convert parse the regex matches (always String) to a valid Java type.

# Specifying duration
The regex mechanism as described above is not available for the Time glue line, meaning that the @Time annotated definition method cannot be configured with arguments. However, you may leave it out entirely if your Time glue line follows the pattern of [number] [duration], where duration is one of second, minute, hour, day, week, month, year, or their corresponding plurals:
* Time: 48 hours
* Time: 3 weeks
* Time: 42 days
* Time: 1 month
* Time: 1 year

Note that month is always 30 days and year is 365 days. Running the experiment with 1 month duration on the 1st of February will check the assumption on the 3rd of March, ignoring leap years. If you want specific behaviour you can still write your own implementation:
```shell
Time: Tuesday after next Easter
@Time(glueLine = "Tuesday after next Easter")
public Duration sampleTime() { .. }
```
---

## Contribute

You can contribute to this repository the by following these steps.

- Fork this repository
- Create a branch
- Do your coding
- Create a pull request to integrate the branch

### Help wanted!

We need some help to achieve the following goals:

- [ ] Create plugins for the major IDEs
- [ ] Have support for other languages than Java
